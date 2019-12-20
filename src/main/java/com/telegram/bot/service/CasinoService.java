package com.telegram.bot.service;

import com.telegram.bot.model.casino.Balance;
import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Currency;

import java.math.BigDecimal;

public interface CasinoService {

    User getUserByName(String userName);
    boolean isBalanceAvailable(Currency currency, BigDecimal amount);

}
