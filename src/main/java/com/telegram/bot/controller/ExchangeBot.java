package com.telegram.bot.controller;

import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.CasinoService;
import com.telegram.bot.service.WorkFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.telegram.bot.model.enums.Actions.*;
import static com.telegram.bot.model.enums.Step.*;
import static com.telegram.bot.utils.CommonUtils.*;
import static com.telegram.bot.utils.Constants.*;
import static org.thymeleaf.util.StringUtils.isEmpty;

//@Component
public class ExchangeBot extends TelegramLongPollingBot {

    private static Logger log = LoggerFactory.getLogger(ExchangeBot.class);

    @Value("${botUserName}")
    private String botUserName;

    @Value("${token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    @Qualifier("stakeService")
    private CasinoService stakeService;

    @Autowired
    @Qualifier("primeDiceService")
    private CasinoService pdService;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() || update.hasCallbackQuery()) {
                switch (getMessage(update)) {
                    case "/start": {
                        startProcessing(update);
                        break;
                    }
                    case "\uD83D\uDEBEGo Exchange": {
                        goToExchangeProcessing(update);
                        break;
                    }
                    case "❌Cancel": {
                        cancelProcessing(update);
                        break;
                    }
                    case "✅Done": {
                        doneProcessing(update);
                        break;
                    }
                    default: {
                        handleRequest(update);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            try {
                execute(addReplyButtons(update, "Something went wrong... Please try again", Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
            } catch (TelegramApiException ex) {
                log.error("Have a big issue: ", e);
            }
            log.error("Something went wrong: ", e);
        }
    }

    private UserWorkflow getUserWorkflow(Update update) {
        return cacheService.getUserWorkflow(getChatId(update));
    }

    private String getChatId(Update update) {
        return update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
    }

    private String getMessage(Update update) {
        return update.hasMessage() ? update.getMessage().getText() : update.getCallbackQuery().getData();
    }

    private void handleRequest(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null) {
            switch (userWorkflow.getStep()) {
                case DIRECTION:
                    directionHandler(update);
                    break;
                case STAKE_USER:
                    stakeUserHandler(update);
                    break;
                case PD_USER:
                    pdUserHandler(update);
                    break;
                case AMOUNT:
                    amountChoiceHandler(update);
                    break;
                case CURRENCY:
                    currencyChoiceHandler(update);
                    break;
                case CHECK_RESULT:
                    checkResultHandler(update);
                    break;
                case CONFIRM_RESULT:
                    confirmResultHandler(update);
                    break;
            }

        } else {
            UserWorkflow newUserWorkflow = new UserWorkflow();
            newUserWorkflow.setChatId(getChatId(update));
            newUserWorkflow.setStep(START);
            cacheService.add(getChatId(update), newUserWorkflow);
        }
        responseGenerator(update);
    }

    private void responseGenerator(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null) {
            if (isEmpty(userWorkflow.getErrorCode())) {
                Step nextStep = workFlowService.getNextStep(userWorkflow);
                if (nextStep != null) {
                    switch (nextStep) {
                        case DIRECTION:
                            execute(addInlineButtons(getChatId(update), "Make your choice:", getDirectionButtons()));
                            break;
                        case STAKE_USER:
                            execute(addReplyButtons(update, "Enter your stake username:", Collections.singletonList(Cancel)));
                            break;
                        case PD_USER:
                            execute(addReplyButtons(update, "Enter your PD username:", Collections.singletonList(Cancel)));
                            break;
                        case CURRENCY:
                            execute(addReplyButtonsWithCurrency(update, "Select currency from the list:", Collections.singletonList(Cancel)));
                            break;
                        case AMOUNT:
                            execute(addReplyButtons(update, "Enter amount:", Collections.singletonList(Cancel)));
                            break;
                        case CHECK_RESULT:
                            execute(addReplyButtons(update, "Check your result and Confirm or Cancel it:\n\n" + getResult(update), Arrays.asList(Confirm, Cancel)));
                            break;
                    }
                    userWorkflow.setStep(nextStep);
                }
            } else {
                switch (userWorkflow.getStep()) {
                    case DIRECTION:
                        execute(addInlineButtons(getChatId(update), workFlowService.getErrorResponse(userWorkflow.getStep(), userWorkflow.getErrorCode()), getDirectionButtons()));
                        break;
                    case CURRENCY:
                        execute(addReplyButtonsWithCurrency(update, workFlowService.getErrorResponse(userWorkflow.getStep(), userWorkflow.getErrorCode()), Collections.singletonList(Cancel)));
                        break;
                    case AMOUNT:
                    case STAKE_USER:
                    case PD_USER: {
                        execute(addReplyButtons(update, workFlowService.getErrorResponse(userWorkflow.getStep(), userWorkflow.getErrorCode()), Collections.singletonList(Cancel)));
                        break;
                    }
                }
                userWorkflow.setErrorCode(null);
            }
        }
    }

    private void stakeUserHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (isEmpty(getMessage(update))) {
            userWorkflow.setErrorCode(EMPTY_VALUE_ERROR);
            return;
        }
        User user = stakeService.getUserByName(getMessage(update));
        if (user != null) {
            userWorkflow.setStakeUserName(user.getName());
            userWorkflow.setStakeUserId(user.getId());
        } else {
            userWorkflow.setErrorCode(WRONG_USER_ERROR);
        }
    }

    private void currencyChoiceHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (Currency.getCurrencyList().contains(getMessage(update).substring(2))) {
            Currency currency = Currency.valueOf(getMessage(update).substring(2).toUpperCase());
            userWorkflow.setCurrency(currency);
        } else {
            userWorkflow.setErrorCode(WRONG_CURRENCY_ERROR);
        }

    }

    private void amountChoiceHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (!isDigit(getMessage(update))) {
            userWorkflow.setErrorCode(AMOUNT_FORMAT_ERROR);
        } else if (!isAmountAvailable(userWorkflow)) {
            userWorkflow.setErrorCode(AMOUNT_AVAILABILITY_ERROR);
        } else {
            userWorkflow.setAmount(getMessage(update));
        }
    }

    private boolean isAmountAvailable(UserWorkflow userWorkflow) {
        boolean result = false;
        if (userWorkflow != null) {
            BigDecimal amount = new BigDecimal(userWorkflow.getAmount());
            result = userWorkflow.getFrom().equals(STAKE) ? stakeService.isBalanceAvailable(userWorkflow.getCurrency(), amount) : pdService.isBalanceAvailable(userWorkflow.getCurrency(), amount);
        }
        return result;
    }

    private void pdUserHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (isEmpty(getMessage(update))) {
            userWorkflow.setErrorCode(EMPTY_VALUE_ERROR);
            return;
        }
        User user = stakeService.getUserByName(getMessage(update));
        if (user != null) {
            userWorkflow.setPdUserName(user.getName());
            userWorkflow.setPdUserId(user.getId());
        } else {
            userWorkflow.setErrorCode(WRONG_USER_ERROR);
        }
    }

    private void directionHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (Arrays.asList(PD, STAKE).contains(getMessage(update))) {
            userWorkflow.setFrom(getMessage(update));
            userWorkflow.setTo(PD.equals(getMessage(update)) ? STAKE : PD);
        } else {
            userWorkflow.setErrorCode(WRONG_DIRECTION_ERROR);
        }

    }

    private void checkResultHandler(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        log.info("Request for chatId {} is -> {}", getChatId(update), userWorkflow);
    }

    private void confirmResultHandler(Update update) throws Exception {
        cacheService.removeByChatId(getChatId(update));
        execute(addReplyButtons(update, "Thank you! Your request will be proceeded in the nearest time. \nHave a good day :)", Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
        log.info("Request for chatId {} was confirmed and send to handler", getChatId(update));
    }

    private void startProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Start processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
        cacheService.removeByChatId(chatId);
    }

    private void goToExchangeProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Go To Exchange processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        handleRequest(update);
    }

    private void cancelProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Cancel processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));

        cacheService.removeByChatId(chatId);
    }

    private void doneProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Done processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
        System.out.println("Success!");
    }

    private String getHelloMsg(Update update) {
        return MessageFormat.format("Hello {0}! I'm Your CryptoExchangeBot. Press “Go to Exchange” to start", update.getMessage().getFrom().getFirstName());
    }


    private SendMessage addReplyButtonsWithCurrency(Update update, String text, List<Actions> actionList) {
        return addReplyButtons(update, text, actionList, true);
    }

    private SendMessage addReplyButtons(Update update, String text, List<Actions> actionList) {
        return addReplyButtons(update, text, actionList, false);
    }

    private SendMessage addReplyButtons(Update update, String text, List<Actions> actionList, boolean isNeedCurrency) {
        String chatId = update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        return new SendMessage().setChatId(chatId).setText("<em>" + text + "</em>").setReplyMarkup(getReplyKeyboard(actionList, isNeedCurrency)).setParseMode(ParseMode.HTML);
    }

    private String getResult(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        return MessageFormat.format("From: {0} \n", userWorkflow.getFrom()) +
                MessageFormat.format("To: {0} \n", userWorkflow.getTo()) +
                MessageFormat.format("Stake username: {0} \n", userWorkflow.getStakeUserName()) +
                MessageFormat.format("PD username: {0} \n", userWorkflow.getPdUserName()) +
                MessageFormat.format("Currency: {0} \n", userWorkflow.getCurrency().getCode()) +
                MessageFormat.format("Amount: {0}", userWorkflow.getAmount());
    }

    private SendMessage addInlineButtons(String chatId, String text, List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfInlineButtons = new ArrayList<>();
        listOfInlineButtons.add(list);
        inlineKeyboardMarkup.setKeyboard(listOfInlineButtons);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }


    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}