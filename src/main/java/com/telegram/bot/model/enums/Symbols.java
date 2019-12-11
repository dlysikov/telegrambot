package com.telegram.bot.model.enums;


import static java.nio.charset.StandardCharsets.UTF_8;

public final class Symbols {

    public static final String MONEY = new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x92, (byte) 0xB6}, UTF_8);
    public static final String DONE = new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, UTF_8) ;
    public static final String CANCEL = new String(new byte[]{(byte) 0xE2, (byte) 0x9D, (byte) 0x8C}, UTF_8) ;
    public static final String FLAG = new String(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xAC, (byte) 0xF0, (byte) 0x9F, (byte) 0x87, (byte) 0xA7}, UTF_8);
}
