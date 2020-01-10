package com.telegram.bot.utils;

import com.telegram.bot.model.casino.Account;
import com.telegram.bot.model.casino.Balance;
import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Symbols;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.telegram.bot.utils.Constants.PD;
import static com.telegram.bot.utils.Constants.STAKE;

public final class CommonUtils {

    public static ReplyKeyboardMarkup getReplyKeyboard(List<Actions> actionList, boolean isNeedCurrency) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardButtons = new KeyboardRow();
        actionList.forEach(action -> keyboardButtons.add(action.getName()));
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
        return replyKeyboardMarkup;
    }

    public static List<InlineKeyboardButton> getDirectionButtons () {
        InlineKeyboardButton buttonSnake = new InlineKeyboardButton("Stake -> PD");
        buttonSnake.setCallbackData(STAKE);
        InlineKeyboardButton buttonPD = new InlineKeyboardButton("PD -> Stake");
        buttonPD.setCallbackData(PD);
        return Arrays.asList(buttonSnake, buttonPD);
    }

    public static HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-access-token", token);
        return httpHeaders;
    }

    public static boolean isDigit(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static HttpEntity<String> getRequestForUserChecking(String userName, String token) {
        String query = "{\"query\":\"{ user (name:\\\"" + userName + "\\\") { name id }}\"}";
        return new HttpEntity<>(query, getHeaders(token));
    }

    public static HttpEntity<String> getSendTipRequest(String userId, String amount, String currency, String chatId, String token) {
        String query = "{\"query\":\"mutation SendTipMutation { sendTip( userId: \\\"" + userId + "\\\"  amount:" + amount + " currency: " + currency +" isPublic: false chatId: \\\"" + chatId + "\\\" ) { id amount currency user { id name} sendBy { id name } }}\"}";
        return new HttpEntity<>(query, getHeaders(token));
    }

    public static HttpEntity<String> getRequest(String query, String token) {
        return new HttpEntity<>(query, getHeaders(token));
    }

    public static boolean checkBalance(Currency currency, BigDecimal amount, ResponseDTO responseDTO) {
        boolean result = false;
        if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null ) {
            Account account = responseDTO.getData().getUser().getBalances().stream()
                    .filter(balance -> balance.getAvailable() != null && balance.getAvailable().getCurrency().equals(currency.getCode()))
                    .findFirst().map(Balance::getAvailable).orElse(new Account());
            if (account.getAmount() != null && account.getAmount().compareTo(amount) > 0) {
                result = true;
            }
        }
        return result;
    }

    public static SendMessage addInlineButtons(String chatId, String text, List<InlineKeyboardButton> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfInlineButtons = new ArrayList<>();
        listOfInlineButtons.add(list);
        inlineKeyboardMarkup.setKeyboard(listOfInlineButtons);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    public static String getMessage(Update update) {
        return update.hasMessage() ? update.getMessage().getText() : update.getCallbackQuery().getData();
    }

    public static String getChatId(Update update) {
        return update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
    }

    public static String getHelloMsg(Update update) {
        return MessageFormat.format("Hello {0}! I'm Your CryptoExchangeBot. Press “Go to Exchange” to start", update.getMessage().getFrom().getFirstName());
    }

    public static SendMessage addReplyButtonsWithCurrency(Update update, String text, List<Actions> actionList) {
        return addReplyButtons(update, text, actionList, true);
    }

    public static SendMessage addReplyButtons(Update update, String text, List<Actions> actionList) {
        return addReplyButtons(update, text, actionList, false);
    }

    public static SendMessage addReplyButtons(Update update, String text, List<Actions> actionList, boolean isNeedCurrency) {
        String chatId = update.getMessage() != null ? String.valueOf(update.getMessage().getChatId()) : String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        return new SendMessage().setChatId(chatId).setText("<em>" + text + "</em>").setReplyMarkup(getReplyKeyboard(actionList, isNeedCurrency)).setParseMode(ParseMode.HTML);
    }

}
