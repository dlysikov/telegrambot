package com.telegram.bot.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

}
