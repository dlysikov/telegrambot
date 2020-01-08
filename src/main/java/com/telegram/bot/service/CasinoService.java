package com.telegram.bot.service;

import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.pojo.UserWorkflow;

import java.math.BigDecimal;

public interface CasinoService {

    User getUserByName(String userName);
    boolean isBalanceAvailable(Currency currency, BigDecimal amount);
    boolean wasAmountReceived(UserWorkflow userWorkflow);
    ResponseDTO sendTips(UserWorkflow userWorkflow);
    ResponseDTO sendTipsBack(UserWorkflow userWorkflow);
}
