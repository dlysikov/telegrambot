package com.telegram.bot.handler;

import com.telegram.bot.exception.NoAddingIsAllowedException;
import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.CasinoService;
import com.telegram.bot.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.telegram.bot.model.enums.PreDefinedErrors.*;
import static com.telegram.bot.model.enums.Step.START;
import static com.telegram.bot.utils.CommonUtils.*;
import static com.telegram.bot.utils.Constants.PD;
import static com.telegram.bot.utils.Constants.STAKE;
import static org.thymeleaf.util.StringUtils.isEmpty;

@Component
public class ExchangeHandler {

    private static Logger log = LoggerFactory.getLogger(ExchangeHandler.class);

    @Autowired
    private CacheService cacheService;

    @Autowired
    @Qualifier("stakeService")
    private CasinoService stakeService;

    @Autowired
    @Qualifier("primeDiceService")
    private CasinoService pdService;

    @Autowired
    private NotificationService notificationService;


    private UserWorkflow getUserWorkflow(Update update) {
        return cacheService.getUserWorkflow(getChatId(update));
    }

    public void addNewUserWorkflow(Update update) throws NoAddingIsAllowedException {
        UserWorkflow newUserWorkflow = new UserWorkflow();
        newUserWorkflow.setChatId(getChatId(update));
        newUserWorkflow.setTelegramUserFirstName(update.getMessage().getFrom().getFirstName());
        newUserWorkflow.setTelegramUserLastName(update.getMessage().getFrom().getLastName());
        newUserWorkflow.setTelegramUserName(update.getMessage().getFrom().getUserName());
        newUserWorkflow.setStep(START);
        cacheService.add(getChatId(update), newUserWorkflow);
    }

    public void directionHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (Arrays.asList(PD, STAKE).contains(getMessage(update))) {
            userWorkflow.setFrom(getMessage(update));
            userWorkflow.setTo(PD.equals(getMessage(update)) ? STAKE : PD);
        } else {
            userWorkflow.setErrorMessage(WRONG_DIRECTION_ERROR.getMessage());
        }
    }

    public void stakeUserHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (isEmpty(getMessage(update))) {
            userWorkflow.setErrorMessage(EMPTY_VALUE_ERROR.getMessage());
            return;
        }
        User user = stakeService.getUserByName(getMessage(update));
        if (user != null) {
            userWorkflow.setStakeUserName(user.getName());
            userWorkflow.setStakeUserId(user.getId());
        } else {
            userWorkflow.setErrorMessage(WRONG_STAKE_USER_ERROR.getMessage());
        }
    }

    public void pdUserHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (isEmpty(getMessage(update))) {
            userWorkflow.setErrorMessage(EMPTY_VALUE_ERROR.getMessage());
            return;
        }
        User user = pdService.getUserByName(getMessage(update));
        if (user != null) {
            userWorkflow.setPdUserName(user.getName());
            userWorkflow.setPdUserId(user.getId());
        } else {
            userWorkflow.setErrorMessage(WRONG_PD_USER_ERROR.getMessage());
        }
    }

    public void amountChoiceHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        BigDecimal amountForExchange;
        if (!isDigit(getMessage(update))) {
            userWorkflow.setErrorMessage(AMOUNT_FORMAT_ERROR.getMessage());
            return;
        } else {
            amountForExchange = new BigDecimal(getMessage(update)).multiply(new BigDecimal("0.97"));
        }

        if (new BigDecimal(getMessage(update)).compareTo(new BigDecimal(userWorkflow.getCurrency().getMinAmount())) < 0) {
            userWorkflow.setErrorMessage(MIN_AMOUNT_ERROR.getMessage());
        } else if (!isAmountAvailable(userWorkflow, amountForExchange)) {
            userWorkflow.setErrorMessage(AMOUNT_AVAILABILITY_ERROR.getMessage());
        } else {
            userWorkflow.setAmount(getMessage(update));
            userWorkflow.setAmountForExchange(amountForExchange.toString());
        }
    }

    public void currencyChoiceHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (Currency.getCurrencyList().contains(getMessage(update).substring(2))) {
            Currency currency = Currency.valueOf(getMessage(update).substring(2).toUpperCase());
            userWorkflow.setCurrency(currency);
        } else {
            userWorkflow.setErrorMessage(WRONG_CURRENCY_ERROR.getMessage());
        }

    }


    public void checkResultHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        CasinoService fromCasinoService = getServiceFrom(userWorkflow);
        CasinoService toCasinoService = getServiceTo(userWorkflow);
        boolean wasAmountReceived = fromCasinoService.wasAmountReceived(userWorkflow);

        if (!wasAmountReceived) {
            userWorkflow.setErrorMessage(NO_AMOUNT_RECEIVED_ERROR.getMessage());
            return;
        }

        try {
            ResponseDTO responseDTO = toCasinoService.sendTips(userWorkflow);
            if (responseDTO.getErrors() == null || responseDTO.getErrors().isEmpty()) {
                log.info("Request was successfully proceeded with responseDTO [{}]", responseDTO);
            } else {
                log.warn("Request was proceeded with errors, responseDTO [{}]", responseDTO);
                userWorkflow.setErrorMessage(responseDTO.getErrors().get(0).getMessage());
            }
            notificationService.sendMail(userWorkflow);

        } catch (Exception exception) {
            log.error("We have exception in the process of sending tips -> ", exception);
            userWorkflow.setErrorMessage(exception.getMessage());
            try {
                notificationService.sendMail(userWorkflow);
                fromCasinoService.sendTipsBack(userWorkflow);
                userWorkflow.setErrorMessage("Internal server error. Your tips were successfully returned back to your account. Please contact admin person or try one more time later.");
            } catch (Exception ex) {
                log.error("We have a serious issue -> ", ex);
                userWorkflow.setErrorMessage("Internal server error. Please contact admin person");
            }
        }
    }

    private boolean isAmountAvailable(UserWorkflow userWorkflow, BigDecimal value) {
        boolean result = false;
        if (userWorkflow != null) {
            CasinoService toCasinoService = getServiceTo(userWorkflow);
            result = toCasinoService.isBalanceAvailable(userWorkflow.getCurrency(), value);
        }
        return result;
    }

    private CasinoService getServiceTo(UserWorkflow userWorkflow) {
        return userWorkflow.getTo().equals(STAKE) ? stakeService : pdService;
    }

    private CasinoService getServiceFrom(UserWorkflow userWorkflow) {
        return userWorkflow.getFrom().equals(STAKE) ? stakeService : pdService;
    }

}
