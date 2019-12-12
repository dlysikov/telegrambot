package com.telegram.bot.utils;

import com.telegram.bot.model.enums.Actions;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Symbols;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-access-token", token);
        return httpHeaders;
    }

}
