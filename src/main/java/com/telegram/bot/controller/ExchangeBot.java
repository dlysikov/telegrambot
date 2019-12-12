package com.telegram.bot.controller;

import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.model.types.UserMode;
import com.telegram.bot.service.CacheService;
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
                            stakeUserHandler(update);
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
                userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY)).put("from", update.getCallbackQuery().getData());
                execute(addReplyButtons(update, "Answers are over. Lock at your results and click \"Done\" button!\n" + makeResultString(userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY))), Arrays.asList(Done, Cancel)));
            }
        } catch (Exception e) {
            log.error("Something went wrong: ", e);
        }
    }

    private UserWorkflow getUserWorkflow(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        return cacheService.getUserWorkflow(chatId);
    }

    private void stakeUserHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null && STAKE_USER.equals(userWorkflow.getStep())) {
            String message = update.getMessage().getText();
            if (userExists(message, env.getProperty("stake.token"), env.getProperty("stake.url"))) {
                userWorkflow.setStakeUserName(message);
                userWorkflow.setStep(CURRENCY);
                execute(addReplyButtonsWithCurrency(update, Collections.singletonList(Cancel)));
            } else {
                execute(addReplyButtons(update, "User not found. Enter correct user:", Collections.singletonList(Cancel)));
            }
        } else {
            currencyChoiceHandler(update);
        }
    }

    private void currencyChoiceHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null && CURRENCY.equals(userWorkflow.getStep())) {
            String message = update.getMessage().getText();
            Currency currency = Currency.valueOf(message.substring(2).toUpperCase());
            userWorkflow.setCurrency(currency);
            userWorkflow.setStep(AMOUNT);
            execute(addReplyButtons(update, "Enter amount:", Collections.singletonList(Cancel)));
        } else {
            amountChoiceHandler(update);
        }
    }

    private void amountChoiceHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null && AMOUNT.equals(userWorkflow.getStep())) {
            String message = update.getMessage().getText();
            userWorkflow.setAmount(Long.valueOf(message));
            userWorkflow.setStep(PD_USER);
            execute(addReplyButtons(update, "Enter PD username:", Collections.singletonList(Cancel)));
        } else {
            pdUserHandler(update);
        }
    }

    private void pdUserHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        String chatId = update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        if (userWorkflow != null && PD_USER.equals(userWorkflow.getStep())) {
            String message = update.getMessage().getText();
            userWorkflow.setPdUserName(message);
            userWorkflow.setStep(DIRECTION);

            InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Stake -> PD");
            buttonSnake.setCallbackData(STAKE);
            InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Stake");
            buttonPD.setCallbackData(PD);
            List<InlineKeyboardButton> list = Arrays.asList(buttonSnake, buttonPD);
            execute(addInlineButtons(Long.valueOf(chatId), "Make your choice:", list));

        } else {
            directionHandler(update);
        }
    }

    private void directionHandler(Update update) throws Exception {
        UserWorkflow userWorkflow = getUserWorkflow(update);
        if (userWorkflow != null && DIRECTION.equals(userWorkflow.getStep())) {
            String message = update.getMessage().getText();
            userWorkflow.setFrom(message);
            userWorkflow.setTo(PD.equals(message) ? STAKE : PD);
            userWorkflow.setStep(HADLE_IS_DONE);
        } else {
            if (userWorkflow != null) {
                userWorkflow.setStep(NO_HANDLER);
                log.warn("There is no handler for that case");
            }
        }
    }

    private void startProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Start processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        execute(addReplyButtons(update, getHelloMsg(update), Arrays.asList(GoExchange, ChangeLanguage, HowToUse)));
        cacheService.removeByChatId(chatId);

        if (!userModeMap.containsKey(chatId)) {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(MODE, UserMode.NO.name());
            paramsMap.put(QUESTION, UserMode.NO.name());
            userModeMap.put(chatId, paramsMap);
        }

    }

    private void goToExchangeProcessing(Update update) throws Exception {
        String chatId = String.valueOf(update.getMessage().getChatId());
        log.info("Go To Exchange processing for [{}] [{}] with chatId [{}]", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName(), chatId);
        execute(addReplyButtons(update, "Enter your stake username:", Collections.singletonList(Cancel)));
        UserWorkflow userWorkflow = new UserWorkflow();
        userWorkflow.setChatId(chatId);
        userWorkflow.setStep(STAKE_USER);
        cacheService.add(chatId, userWorkflow);

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

    private SendMessage addInlineButtons(long chatId, String text, List<InlineKeyboardButton> list) {
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