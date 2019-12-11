package com.telegram.bot.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Currency {

    BTC("btc"),
    ETH("eth"),
    LTC("ltc"),
    DOGE("doge"),
    BCH("bch"),
    XRP("xrp");

    private String code;

    Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static List<String> getCurrencyList() {
        List<String> currencyList = new ArrayList<>();
        Arrays.asList(Currency.values()).forEach(currency -> currencyList.add(currency.getCode()));
        return currencyList;
    }
}
