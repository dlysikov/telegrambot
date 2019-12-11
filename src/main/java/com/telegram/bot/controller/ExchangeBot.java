package com.telegram.bot.controller;

import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Symbols;
import com.telegram.bot.model.types.UserMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class ExchangeBot extends TelegramLongPollingBot {

    private static Logger log = LoggerFactory.getLogger(ExchangeBot.class);

    @Value("${botUserName}")
    private String botUserName;

    @Value("${token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;


    private Map<String, LinkedHashMap<String, String>> userParamsMap = new HashMap<>();
    private Map<String, Map<String, String>> userModeMap = new HashMap<>();

    private final String MODE = "isExchangeMode";
    private final String QUESTION = "countQuestion";
    private final String KEY = "key";

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String done = new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, "UTF-8") + "Done";
            String cancel = new String(new byte[]{(byte) 0xE2, (byte) 0x9D, (byte) 0x8C}, "UTF-8") + "Cancel";
            String goExchange = new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x9A, (byte) 0xBE}, "UTF-8") + "Go Exchange";
            String changeLanguage = new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xAC, (byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xA7}, "UTF-8") + "Change language";
            String howToUse = new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8A}, "UTF-8") + "How to Use bot";

            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    String message = update.getMessage().getText();
                    String chatId = String.valueOf(update.getMessage().getChatId());

                    String text = "Hello " + update.getMessage().getFrom().getFirstName() + ", im YourCryptoExchangeBot. Press “Go to Exchange” to start";

                    if (message.equals("/start")) {
                        System.out.println("Start to " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
                        execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));

                        if (!userModeMap.containsKey(chatId)) {
                            Map<String, String> paramsMap = new HashMap<>();
                            paramsMap.put(MODE, UserMode.NO.name());
                            paramsMap.put(QUESTION, UserMode.NO.name());
                            userModeMap.put(chatId, paramsMap);
                        }
                    } else if (message.equals(Actions.GoExchange.getName())) {
                        execute(addReplyButtons(update.getMessage().getChatId(), "Enter your stake username:", Arrays.asList(cancel), false));
                        Map<String, String> paramsMap;
                        if (userModeMap.get(chatId) != null) {
                            paramsMap = userModeMap.get(chatId);
                            paramsMap.put(MODE, UserMode.YES.name());
                            paramsMap.put(QUESTION, UserMode.FIRST_QUESTION.name());
                        } else {
                            paramsMap = new HashMap<>();
                            paramsMap.put(MODE, UserMode.YES.name());
                            paramsMap.put(QUESTION, UserMode.FIRST_QUESTION.name());
                            userModeMap.put(chatId, paramsMap);
                        }
                    } else if (message.equals("❌Cancel")) {
                        execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));

                        if (userModeMap.get(chatId) != null) {
                            if (userModeMap.get(chatId).get(KEY) != null) {
                                String key = userModeMap.get(chatId).get(KEY);
                                if (key != null && !key.equals("")) {
                                    userParamsMap.remove(key);
                                }
                                userModeMap.remove(chatId);
                            }
                            userModeMap.remove(chatId);
                        }
                    } else if (message.equals("✅Done")) {
                        execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));
                        System.out.println("Success!");
                        // Obrezat currency
                        userParamsMap.entrySet().stream().forEach(e -> {
                            System.out.println("Map for user: " + e.getKey());
                            e.getValue().entrySet().forEach(elem -> System.out.println(elem.getKey() + " -> " + elem.getValue()));
                        });
                        System.out.println();

                        userParamsMap.remove(userModeMap.get(chatId).get(KEY));
                        userModeMap.remove(chatId);
                    } else {
                        if (userModeMap.get(chatId) != null) {
                            if (userModeMap.get(chatId).get(MODE) == UserMode.YES.name()) {
                                String countQuestion = userModeMap.get(chatId).get(QUESTION);
                                switch (countQuestion) {
                                    case "FIRST_QUESTION": {
                                        String response = checkUser(message, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJmOTMzNmI1ZC02ZWMzLTQ1YWItYjBhNC0yNzE1NjRhZDk3NzgiLCJpYXQiOjE1NzQwODY5MzMsImV4cCI6MTU3OTI3MDkzM30.-0N0pHnDIeFGYHKHJB45gFoAk9FAlcpsH7jwtIzEZsg", "https://api.stake.com/graphql");

                                        if (!response.contains("null")) {
                                            execute(addReplyButtons(update.getMessage().getChatId(), "User exist!", Arrays.asList(cancel), true));
                                            Map<String, String> bufMap = userModeMap.get(chatId);
                                            bufMap.put(KEY, chatId + message);

                                            LinkedHashMap<String, String> map = new LinkedHashMap<>();
                                            map.put("stake user", message);
                                            userParamsMap.put(chatId + message, map);

                                            execute(addReplyButtons(update.getMessage().getChatId(), "Select currency from list", Arrays.asList(cancel), true));
                                            Map<String, String> paramsMap = userModeMap.get(chatId);
                                            paramsMap.put(QUESTION, UserMode.SECOND_QUESTION.name());
                                        } else {
                                            execute(addReplyButtons(update.getMessage().getChatId(), "User not found. Enter correct user:", Arrays.asList(cancel), false));
                                        }
                                        break;
                                    }
                                    case "SECOND_QUESTION": {
                                        userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("currency", message.substring(2));

                                        execute(addReplyButtons(update.getMessage().getChatId(), "Enter amount:", Arrays.asList(cancel), false));
                                        Map<String, String> paramsMap = userModeMap.get(chatId);
                                        paramsMap.put(QUESTION, UserMode.THIRD_QUESTION.name());
                                        break;
                                    }
                                    case "THIRD_QUESTION": {
                                        userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("amount", message);

                                        execute(addReplyButtons(update.getMessage().getChatId(), "Enter PD username:", Arrays.asList(cancel), false));
                                        Map<String, String> paramsMap = userModeMap.get(chatId);
                                        paramsMap.put(QUESTION, UserMode.FOUR_QUESTION.name());
                                        break;
                                    }
                                    case "FOUR_QUESTION": {
                                        String response = checkUser(message, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI4ZjhkYmFlMi02MTYzLTRkOGEtOGY0MC1iZjBhZGZiODYxNDAiLCJpYXQiOjE1NzQ0MTQ4MDcsImV4cCI6MTU3OTU5ODgwN30.pkgPJAGeeLcY1Jbf3r-jRI-USAi10ewLWv-_wp2ZjOg", "https://api.primedice.com/graphql");

                                        if (!response.contains("null")) {
                                            execute(addReplyButtons(update.getMessage().getChatId(), "User exist!", Arrays.asList(cancel), false));
                                            userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("PD user", message);

                                            InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Snake -> PD");
                                            buttonSnake.setCallbackData("snake");
                                            InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Snake");
                                            buttonPD.setCallbackData("PD");
                                            List<InlineKeyboardButton> list = Arrays.asList(buttonSnake, buttonPD);
                                            execute(addInlineButtons(Long.valueOf(chatId), "Make your choice:", list));
                                        } else {
                                            execute(addReplyButtons(update.getMessage().getChatId(), "User not found. Enter correct user:", Arrays.asList(cancel), false));
                                        }
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    /*switch (message) {
                        case "/start": {
                            System.out.println("Start to " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
                            execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));

                            if (!userModeMap.containsKey(chatId)) {
                                Map<String, String> paramsMap = new HashMap<>();
                                paramsMap.put(MODE, UserMode.NO.name());
                                paramsMap.put(QUESTION, UserMode.NO.name());
                                userModeMap.put(chatId, paramsMap);
                            }

                            break;
                        }
                        case "\uD83D\uDEBEGo Exchange": {
                            execute(addReplyButtons(update.getMessage().getChatId(), "Enter your stake username:", Arrays.asList(cancel), false));
                            Map<String, String> paramsMap;
                            if (userModeMap.get(chatId) != null) {
                                paramsMap = userModeMap.get(chatId);
                                paramsMap.put(MODE, UserMode.YES.name());
                                paramsMap.put(QUESTION, UserMode.FIRST_QUESTION.name());
                            } else {
                                paramsMap = new HashMap<>();
                                paramsMap.put(MODE, UserMode.YES.name());
                                paramsMap.put(QUESTION, UserMode.FIRST_QUESTION.name());
                                userModeMap.put(chatId, paramsMap);
                            }
                            break;
                        }
                        case "❌Cancel": {
                            execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));

                            if (userModeMap.get(chatId) != null) {
                                if (userModeMap.get(chatId).get(KEY) != null) {
                                    String key = userModeMap.get(chatId).get(KEY);
                                    if (key != null && !key.equals("")) {
                                        userParamsMap.remove(key);
                                    }
                                    userModeMap.remove(chatId);
                                }
                                userModeMap.remove(chatId);
                            }
                            break;
                        }
                        case "✅Done": {
                            execute(addReplyButtons(update.getMessage().getChatId(), text, Arrays.asList(goExchange, changeLanguage, howToUse), false));
                            System.out.println("Success!");
                            // Obrezat currency
                            userParamsMap.entrySet().stream().forEach(e -> {
                                System.out.println("Map for user: " + e.getKey());
                                e.getValue().entrySet().forEach(elem -> System.out.println(elem.getKey() + " -> " + elem.getValue()));
                            });
                            System.out.println();

                            userParamsMap.remove(userModeMap.get(chatId).get(KEY));
                            userModeMap.remove(chatId);

                            break;
                        }
                        default: {
                            if (userModeMap.get(chatId) != null) {
                                if (userModeMap.get(chatId).get(MODE) == UserMode.YES.name()) {
                                    String countQuestion = userModeMap.get(chatId).get(QUESTION);
                                    switch (countQuestion) {
                                        case "FIRST_QUESTION": {
                                            String response = checkUser(message, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJmOTMzNmI1ZC02ZWMzLTQ1YWItYjBhNC0yNzE1NjRhZDk3NzgiLCJpYXQiOjE1NzQwODY5MzMsImV4cCI6MTU3OTI3MDkzM30.-0N0pHnDIeFGYHKHJB45gFoAk9FAlcpsH7jwtIzEZsg", "https://api.stake.com/graphql");

                                            if (!response.contains("null")) {
                                                execute(addReplyButtons(update.getMessage().getChatId(), "User exist!", Arrays.asList(cancel), true));
                                                Map<String, String> bufMap = userModeMap.get(chatId);
                                                bufMap.put(KEY, chatId + message);

                                                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                                                map.put("stake user", message);
                                                userParamsMap.put(chatId + message, map);

                                                execute(addReplyButtons(update.getMessage().getChatId(), "Select currency from list", Arrays.asList(cancel), true));
                                                Map<String, String> paramsMap = userModeMap.get(chatId);
                                                paramsMap.put(QUESTION, UserMode.SECOND_QUESTION.name());
                                            } else {
                                                execute(addReplyButtons(update.getMessage().getChatId(), "User not found. Enter correct user:", Arrays.asList(cancel), false));
                                            }
                                            break;
                                        }
                                        case "SECOND_QUESTION": {
                                            userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("currency", message.substring(2));

                                            execute(addReplyButtons(update.getMessage().getChatId(), "Enter amount:", Arrays.asList(cancel), false));
                                            Map<String, String> paramsMap = userModeMap.get(chatId);
                                            paramsMap.put(QUESTION, UserMode.THIRD_QUESTION.name());
                                            break;
                                        }
                                        case "THIRD_QUESTION": {
                                            userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("amount", message);

                                            execute(addReplyButtons(update.getMessage().getChatId(), "Enter PD username:", Arrays.asList(cancel), false));
                                            Map<String, String> paramsMap = userModeMap.get(chatId);
                                            paramsMap.put(QUESTION, UserMode.FOUR_QUESTION.name());
                                            break;
                                        }
                                        case "FOUR_QUESTION": {
                                            String response = checkUser(message, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI4ZjhkYmFlMi02MTYzLTRkOGEtOGY0MC1iZjBhZGZiODYxNDAiLCJpYXQiOjE1NzQ0MTQ4MDcsImV4cCI6MTU3OTU5ODgwN30.pkgPJAGeeLcY1Jbf3r-jRI-USAi10ewLWv-_wp2ZjOg", "https://api.primedice.com/graphql");

                                            if (!response.contains("null")) {
                                                execute(addReplyButtons(update.getMessage().getChatId(), "User exist!", Arrays.asList(cancel), false));
                                                userParamsMap.get(userModeMap.get(chatId).get(KEY)).put("PD user", message);

                                                InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Snake -> PD");
                                                buttonSnake.setCallbackData("snake");
                                                InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Snake");
                                                buttonPD.setCallbackData("PD");
                                                List<InlineKeyboardButton> list = Arrays.asList(buttonSnake, buttonPD);
                                                execute(addInlineButtons(Long.valueOf(chatId), "Make your choice:", list));
                                            } else {
                                                execute(addReplyButtons(update.getMessage().getChatId(), "User not found. Enter correct user:", Arrays.asList(cancel), false));
                                            }
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }*/
                }
            } else if (update.hasCallbackQuery()) {
                userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY)).put("from", update.getCallbackQuery().getData());
                execute(addReplyButtons(update.getCallbackQuery().getMessage().getChatId(), "Answers is over. Lock at your results and click \"Done\" button!\n" + makeResultString(userParamsMap.get(userModeMap.get(String.valueOf(update.getCallbackQuery().getMessage().getChatId())).get(KEY))), Arrays.asList(done, cancel), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void printParamsMap() {
        if (!userParamsMap.isEmpty()) {
            userParamsMap.entrySet().stream().forEach(elem -> {
                System.out.println("User Key: " + elem.getKey());
                elem.getValue().entrySet().forEach(e -> System.out.println("    " + e.getKey() + " -> " + e.getValue()));
            });
        } else {
            System.out.println("Empty Params Map!");
        }
    }

    public void printModeMap() {
        if (!userModeMap.isEmpty()) {
            userModeMap.entrySet().stream().forEach(elem -> {
                System.out.println("Chat Id: " + elem.getKey());
                elem.getValue().entrySet().forEach(e -> System.out.println("    " + e.getKey() + " -> " + e.getValue()));
            });
        } else {
            System.out.println("Empty Mode Map!");
        }
    }

    private SendMessage addReplyButtons(long chatId, String text, List<String> listButtonNames, boolean isNeedCurrency) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardButtons = new KeyboardRow();
        listButtonNames.forEach(buttonName -> keyboardButtons.add(buttonName));
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        if (isNeedCurrency) {
            KeyboardRow keyboardButtonsCurrency = new KeyboardRow();
            Currency.getCurrencyList().forEach(currency -> keyboardButtonsCurrency.add(Symbols.MONEY + currency));
            keyboardRows.add(keyboardButtonsCurrency);
        }
        keyboardRows.add(keyboardButtons);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return new SendMessage().setChatId(chatId).setText("<em>" + text + "</em>").setReplyMarkup(replyKeyboardMarkup).setParseMode(ParseMode.HTML);
    }

    private String makeResultString(Map<String, String> paramValuesMap) {
        StringBuffer result = new StringBuffer();
        paramValuesMap.entrySet().forEach(elem -> result.append(elem.getKey() + ": " + elem.getValue() + "\n"));
        return result.toString();
    }

    private SendMessage addInlineButtons(long chatId, String text, List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> l = new ArrayList<>();
        l.add(list);
        inlineKeyboardMarkup.setKeyboard(l);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    private String checkUser(String userName, String token, String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-access-token", token);
        String json = "{\"query\":\"{\\n  user  (name: \\\"" + userName + "\\\") \\n   {\\n    name\\n  }\\n   \\n}\"}";
        HttpEntity<String> request = new HttpEntity<>(json, httpHeaders);
        return restTemplate.postForObject(url, request, String.class);
    }

}