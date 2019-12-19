package com.telegram.bot.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("stakeService")
public class StakeServiceImpl extends CasinoServiceImpl {

    @Value("${stake.token}")
    private String token;

    @Value("${stake.url}")
    private String url;

    @Value("${query.balance.check}")
    private String balanceQuery;

    @Value("${query.tips.check}")
    private String tipsQuery;

}
