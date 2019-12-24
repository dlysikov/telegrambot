package com.telegram.bot.service.impl;

import com.telegram.bot.model.pojo.UserWorkflow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("primeDiceService")
public class PrimeDiceServiceImpl extends CasinoServiceImpl {

    @Value("${primedice.token}")
    private String token;

    @Value("${primedice.url}")
    private String url;

    @Value("${primedice.chatId}")
    private String chatId;

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

    @Override
    public String getUserId(UserWorkflow userWorkflow) {
        return userWorkflow.getPdUserId();
    }

    @Override
    public String getChatId() {
        return this.chatId;
    }
}
