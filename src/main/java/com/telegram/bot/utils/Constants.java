package com.telegram.bot.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class Constants {

    public static final String DONE = new String(new byte[]{(byte) 0xE2, (byte) 0x9C, (byte) 0x85}, UTF_8) + "Done";

}
