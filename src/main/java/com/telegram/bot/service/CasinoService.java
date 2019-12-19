package com.telegram.bot.service;

import com.telegram.bot.model.casino.Balance;
import com.telegram.bot.model.enums.Currency;

import java.math.BigDecimal;

public interface CasinoService {

    boolean userExists(String userName);

    boolean isBalanceEnough(Currency currency, BigDecimal amount);

}
