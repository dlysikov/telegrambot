package com.telegram.bot.controller;

import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.model.types.UserMode;
import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.WorkFlowService;
import com.telegram.bot.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.text.MessageFormat;
import java.util.*;

import static com.telegram.bot.model.enums.Actions.*;
import static com.telegram.bot.model.enums.Step.*;
import static com.telegram.bot.utils.CommonUtils.getHeaders;
import static com.telegram.bot.utils.CommonUtils.getReplyKeyboard;
import static com.telegram.bot.utils.Constants.PD;
import static com.telegram.bot.utils.Constants.STAKE;

@Component
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


    private Map<String, LinkedHashMap<String, String>> userParamsMap = new HashMap<>();
    private Map<String, Map<String, String>> userModeMap = new HashMap<>();

    private final String MODE = "isExchangeMode";
    private final String QUESTION = "countQuestion";
    private final String KEY = "key";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    String message = update.getMessage().getText();

                    switch (message) {
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
                            /*if (userModeMap.get(chatId) != null) {
                                if (userModeMap.get(chatId).get(MODE).equals(UserMode.YES.name())) {
                                    String countQuestion = userModeMap.get(chatId).get(QUESTION);
                                    switch (countQuestion) {
                                        case "FIRST_QUESTION": {
//                                            String response = userExists(message, env.getProperty("stake.token"), env.getProperty("stake.url"));

                                            if (userExists(message, env.getProperty("stake.token"), env.getProperty("stake.url"))) {
                                                execute(addReplyButtonsWithCurrency(update, "User exist!", Collections.singletonList(Cancel)));
                                                Map<String, String> bufMap = userModeMap.get(chatId);
                                                bufMap.put(KEY, chatId + message);

                                                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                                                map.put("stake user", message);
                                                userParamsMap.put(chatId + message, map);

                                                execute(addReplyButtonsWithCurrency(update, "Select currency from list", Collections.singletonList(Cancel)));
                                                Map<String, String> paramsMap = userModeMap.get(chatId);
                                                paramsMap.put(QUESTION, UserMode.SECOND_QUESTION.name());
                                            } else {
                                                execute(addReplyButtons(update, "User not found. Enter correct user:", Collections.singletonList(Cancel)));
                                            }
                                            break;
                                        }
                                        case "SECOND_QUESTION": {
                                            userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("currency", message.substring(2));

                                            execute(addReplyButtons(update, "Enter amount:", Collections.singletonList(Cancel)));
                                            Map<String, String> paramsMap = userModeMap.get(chatId);
                                            paramsMap.put(QUESTION, UserMode.THIRD_QUESTION.name());
                                            break;
                                        }
                                        case "THIRD_QUESTION": {
                                            userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("amount", message);

                                            execute(addReplyButtons(update, "Enter PD username:", Collections.singletonList(Cancel)));
                                            Map<String, String> paramsMap = userModeMap.get(chatId);
                                            paramsMap.put(QUESTION, UserMode.FOUR_QUESTION.name());
                                            break;
                                        }
                                        case "FOUR_QUESTION": {
//                                            String response = userExists(message, env.getProperty("primedice.token"), env.getProperty("primedice.url"));

                                            if (userExists(message, env.getProperty("primedice.token"), env.getProperty("primedice.url"))) {
                                                execute(addReplyButtons(update, "User exist!", Collections.singletonList(Cancel)));
                                                userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("PD user", message);

                                                InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Stake -> PD");
                                                buttonSnake.setCallbackData("Stake");
                                                InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Snake");
                                                buttonPD.setCallbackData("PD");
                                                List<InlineKeyboardButton> list = Arrays.asList(buttonSnake, buttonPD);
                                                execute(addInlineButtons(Long.valueOf(chatId), "Make your choice:", list));
                                            } else {
                                                execute(addReplyButtons(update, "User not found. Enter correct user:", Collections.singletonList(Cancel)));
                                            }
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            }*/
                            break;
                        }
                    }
                }
            } else if (update.hasCallbackQuery()) {
                handleRequest(update);
//                userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY)).put("from", update.getCallbackQuery().getData());
//                execute(addReplyButtons(update, "Answers are over. Lock at your results and click \"Done\" button!\n" + makeResultString(userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY))), Arrays.asList(Done, Cancel)));
            }
        } catch (Exception e) {
            log.error("Something went wrong: ", e);
        }
    }

    private void responseGenerator(UserWorkflow userWorkflow, Update update) {
        Step nextStep = workFlowService.getNextStep(userWorkflow.getStep());

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
                default:
                    startProcessing(update);
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
            Step nextStep = workFlowService.getNextStep(userWorkflow.getStep());
            switch (nextStep) {
                case DIRECTION:
                    InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Stake -> PD");
                    buttonSnake.setCallbackData(STAKE);
                    InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Stake");
                    buttonPD.setCallbackData(PD);
                    List<InlineKeyboardButton> list = Arrays.asList(buttonSnake, buttonPD);
                    execute(addInlineButtons(getChatId(update), "Make your choice:", list));
                    userWorkflow.setStep(DIRECTION);
                    break;
                case STAKE_USER:
                    execute(addReplyButtons(update, "Enter your stake username:", Collections.singletonList(Cancel)));
                    userWorkflow.setStep(STAKE_USER);
                    break;
                case PD_USER:
                    execute(addReplyButtons(update, "Enter your PD username:", Collections.singletonList(Cancel)));
                    userWorkflow.setStep(PD_USER);
                    break;
                case CURRENCY:
                    execute(addReplyButtonsWithCurrency(update, Collections.singletonList(Cancel)));
                    userWorkflow.setStep(CURRENCY);
                    break;
                case AMOUNT:
                    execute(addReplyButtons(update, "Enter amount:", Collections.singletonList(Cancel)));
                    userWorkflow.setStep(AMOUNT);
                    break;
            }
        }

    }

    private void stakeUserHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
//        String message = update.getMessage().getText();
        if (userExists(getMessage(update), env.getProperty("stake.token"), env.getProperty("stake.url"))) {
            userWorkflow.setStakeUserName(getMessage(update));
//            userWorkflow.setStep(PD_USER);
//            execute(addReplyButtons(update, "Enter PD username:", Collections.singletonList(Cancel)));
        } else {
            execute(addReplyButtons(update, "User not found. Enter correct user:", Collections.singletonList(Cancel)));
        }
    }

    private void currencyChoiceHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
//        String message = update.getMessage().getText();
        Currency currency = Currency.valueOf(getMessage(update).substring(2).toUpperCase());
        userWorkflow.setCurrency(currency);
//        userWorkflow.setStep(AMOUNT);
//        execute(addReplyButtons(update, "Enter amount:", Collections.singletonList(Cancel)));
    }

    private void amountChoiceHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
//        String message = update.getMessage().getText();
        userWorkflow.setAmount(Long.valueOf(getMessage(update)));
//        userWorkflow.setStep(HADLE_IS_DONE);

    }

    private void pdUserHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
//        String message = update.getMessage().getText();
        userWorkflow.setPdUserName(getMessage(update));

//        userWorkflow.setStep(CURRENCY);
//        execute(addReplyButtonsWithCurrency(update, Collections.singletonList(Cancel)));

    }

    private void directionHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
//        String message = update.getMessage().getText();
        userWorkflow.setFrom(getMessage(update));
        userWorkflow.setTo(PD.equals(getMessage(update)) ? STAKE : PD);


//        execute(addReplyButtons(update, "Enter your stake username:", Collections.singletonList(Cancel)));
//        userWorkflow.setStep(STAKE_USER);
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


    private SendMessage addReplyButtonsWithCurrency(Update update, List<Actions> actionList) {
        return addReplyButtons(update, "Select currency from the list:", actionList, true);
    }

    private SendMessage addReplyButtons(Update update, String text, List<Actions> actionList) {
        return addReplyButtons(update, text, actionList, false);
    }

    private SendMessage addReplyButtons(Update update, String text, List<Actions> actionList, boolean isNeedCurrency) {
        String chatId = update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        return new SendMessage().setChatId(chatId).setText("<em>" + text + "</em>").setReplyMarkup(getReplyKeyboard(actionList, isNeedCurrency)).setParseMode(ParseMode.HTML);
    }

    private String makeResultString(Map<String, String> paramValuesMap) {
        StringBuffer result = new StringBuffer();
        paramValuesMap.forEach((key, value) -> result.append(key).append(": ").append(value).append("\n"));
        return result.toString();
    }

    private SendMessage addInlineButtons(String chatId, String text, List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfInlineButtons = new ArrayList<>();
        listOfInlineButtons.add(list);
        inlineKeyboardMarkup.setKeyboard(listOfInlineButtons);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    private boolean userExists(String userName, String token, String url) {
        String json = "{\"query\":\"{\\n  user  (name: \\\"" + userName + "\\\") \\n   {\\n    name\\n  }\\n   \\n}\"}";
        HttpEntity<String> request = new HttpEntity<>(json, getHeaders(token));
        return restTemplate.postForObject(url, request, String.class) != null;
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