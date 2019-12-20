package com.telegram.bot.service.impl;

import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("primeDiceService")
public class PrimeDiceServiceImpl extends CasinoServiceImpl {

    @Value("${primedice.token}")
    private String token;

    @Value("${primedice.url}")
    private String url;

    @Value("${query.balance.check}")
    private String balanceQuery;

    @Value("${query.tips.check}")
    private String tipsQuery;


    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getBalanceQuery() {
        return this.balanceQuery;
    }

    @Override
    public String getTipsQuery() {
        return this.tipsQuery;
    }
}
