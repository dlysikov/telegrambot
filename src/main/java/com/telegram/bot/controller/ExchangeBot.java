package com.telegram.bot.controller;

import com.telegram.bot.exception.NoAddingIsAllowedException;
import com.telegram.bot.handler.ExchangeHandler;
import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.WorkFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

import static com.telegram.bot.model.enums.Actions.*;
import static com.telegram.bot.utils.CommonUtils.*;
import static org.thymeleaf.util.StringUtils.isEmpty;

@Component
public class ExchangeBot extends TelegramLongPollingBot {

    private static Logger log = LoggerFactory.getLogger(ExchangeBot.class);

    @Value("${botUserName}")
    private String botUserName;

    @Value("${token}")
    private String token;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private ExchangeHandler handler;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() || update.hasCallbackQuery()) {
                switch (getMessage(update)) {
                    case "/start": {
                        startProcessing(update);
                        break;
                    }
               /*     case "\uD83D\uDEBEGo Exchange": {
                        goToExchangeProcessing(update);
                        break;
                    }*/
                    case "❌Cancel": {
                        cancelProcessing(update);
                        break;
                    }
                    case "✅Done": {
                        doneProcessing(update);
                        break;
                    }
                    case "✅Retry": {
                        goToRetryProcessing(update);
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

    private void handleRequest(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null) {
            switch (userWorkflow.getStep()) {
                case DIRECTION:
                    handler.directionHandler(update);
                    break;
                case STAKE_USER:
                    handler.stakeUserHandler(update);
                    break;
                case PD_USER:
                    handler.pdUserHandler(update);
                    break;
                case AMOUNT:
                    handler.amountChoiceHandler(update);
                    break;
                case CURRENCY:
                    handler.currencyChoiceHandler(update);
                    break;
                case CHECK_RESULT:
                    handler.checkResultHandler(update);
                    break;
            }

        } else {
            try{
                handler.addNewUserWorkflow(update);
            } catch (NoAddingIsAllowedException exception) {
                execute(addReplyButtons(update, "Sorry. The service is temporary unavailable. Please try later.", Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
            }
        }
        responseGenerator(update);
    }

    private void responseGenerator(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null) {
            if (isEmpty(userWorkflow.getErrorMessage())) {
                Step nextStep = workFlowService.getNextStep(userWorkflow);
                if (nextStep != null) {
                    switch (nextStep) {
                        case DIRECTION:
                            execute(addButtons(getChatId(update), "Make your choice below:", getDirectionButtons()));
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
                            execute(addReplyButtons(update, getCheckResultMsg(userWorkflow) + getResult(update), Arrays.asList(Confirm, Cancel)));
                            break;
                        case CONFIRM_RESULT:
                            execute(addReplyButtons(update, "Thank you! Your request will be proceeded in the nearest time. \nHave a good day :)", Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
                            cacheService.removeByChatId(getChatId(update));
                            break;
                    }
                    userWorkflow.setStep(nextStep);
                }
            } else {
                switch (userWorkflow.getStep()) {
                    case DIRECTION:
                        execute(addButtons(getChatId(update), userWorkflow.getErrorMessage(), getDirectionButtons()));
                        break;
                    case CURRENCY:
                        execute(addReplyButtonsWithCurrency(update, userWorkflow.getErrorMessage(), Collections.singletonList(Cancel)));
                        break;
                    case AMOUNT:
                    case STAKE_USER:
                    case PD_USER: {
                        execute(addReplyButtons(update, userWorkflow.getErrorMessage(), Collections.singletonList(Cancel)));
                        break;
                    }
                    case CHECK_RESULT:
                        execute(addReplyButtons(update, userWorkflow.getErrorMessage(), Arrays.asList(Confirm, Retry, Cancel)));
                        break;
                }
                userWorkflow.setErrorMessage(null);
            }
        }
    }

    private void startProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Start processing for chatId [{}] with firstName [{}] and lastName [{}]", chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName());
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
    }

   /* private void goToExchangeProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Go To Exchange processing for chatId [{}] with firstName [{}] and lastName [{}]", chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName());
        handleRequest(update);
    }*/

    private void goToRetryProcessing(Update update) throws Exception {
        cacheService.resetUserWorkflow(getUserWorkflow(update));
        handleRequest(update);
    }

    private void cancelProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Cancel processing for chatId [{}] with firstName [{}] and lastName [{}]", chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName());
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
        cacheService.removeByChatId(chatId);
    }

    private void doneProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Done processing for chatId [{}] with firstName [{}] and lastName [{}]", chatId, update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName());
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
    }

    private String getResult(Update update) {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        return MessageFormat.format("From: {0} \n", userWorkflow.getFrom()) +
                MessageFormat.format("To: {0} \n", userWorkflow.getTo()) +
                MessageFormat.format("Stake username: {0} \n", userWorkflow.getStakeUserName()) +
                MessageFormat.format("PD username: {0} \n", userWorkflow.getPdUserName()) +
                MessageFormat.format("Currency: {0} \n", userWorkflow.getCurrency().getCode()) +
                MessageFormat.format("Amount: {0} \n", userWorkflow.getAmount()) +
                MessageFormat.format("Amount that will be received: {0}", userWorkflow.getAmountForExchange());
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