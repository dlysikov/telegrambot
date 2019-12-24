package com.telegram.bot.model.enums;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum Actions {

    Done(new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, UTF_8) + "Done"),
    Cancel(new String(new byte[]{(byte) 0xE2, (byte) 0x9D, (byte) 0x8C}, UTF_8) + "Cancel"),
    GoExchange(new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x9A, (byte) 0xBE}, UTF_8) + "Go Exchange"),
    ChangeLanguage(new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xAC, (byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xA7}, UTF_8) + "Change language"),
    HowToUse(new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8A}, UTF_8) + "How to Use bot"),
    Confirm(new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, UTF_8) + "Confirm"),
    Retry(new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, UTF_8) + "Retry"),;

    private String name;

    Actions(String action) {
            this.name = action;
    }

    public String getName() {
        return name;
    }
}
