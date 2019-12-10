package com.telegram.bot.utils;

public enum Buttons {

    Done("Done"),
    Cancel("Cancel"),
    GoExchange("Go Exchange"),
    ChangeLanguage("Change language"),
    HowToUse("How to Use bot");

    private String name;

    private Buttons(String buttonName) {
        this.name = buttonName;
    }

}
