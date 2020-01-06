package com.telegram.bot.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Currency {

    BTC("btc", "0.000025"),
    ETH("eth", "0.00013"),
    LTC("ltc", "0.0022"),
    DOGE("doge", "275"),
    BCH("bch", "0.0011"),
    XRP("xrp", "0.15");

    private String code;
    private String minAmount;

    Currency(String code, String minAmount) {
        this.code = code;
        this.minAmount = minAmount;
    }

    public String getCode() {
        return code;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public static List<String> getCurrencyList() {
        List<String> currencyList = new ArrayList<>();
        Arrays.asList(Currency.values()).forEach(currency -> currencyList.add(currency.getCode()));
        return currencyList;
    }
}
