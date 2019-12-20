package com.telegram.bot.service.impl;

import com.telegram.bot.controller.ExchangeBot;
import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.casino.User;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CasinoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.telegram.bot.utils.CommonUtils.*;
import static com.telegram.bot.utils.Constants.STAKE;

@Service
public abstract class CasinoServiceImpl implements CasinoService {

    private static Logger log = LoggerFactory.getLogger(CasinoServiceImpl.class);

    private String token = null;

    private String url = null;

    private String balanceQuery = null;

    private String tipsQuery = null;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public User getUserByName(String userName) {
        User user;
                try {
                    ResponseDTO responseDTO = restTemplate.postForObject(url, getRequestForUserChecking(userName, this.token), ResponseDTO.class);
                    if ( responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null) {
                        user = responseDTO.getData().getUser();
                    } else {
                        user = null;
                    }
                } catch (Exception exception) {
                    log.error("We've got exception in getting user by name -> ", exception);
                    user = null;
                }
        return user;
    }

    @Override
    public boolean isBalanceAvailable(Currency currency, BigDecimal amount) {
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequest(this.balanceQuery, this.token), ResponseDTO.class);
        return checkBalance(currency, amount, responseDTO);
    }

    private boolean wasAmountRecieved(String userName) {
        boolean result = false;
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequest(this.tipsQuery, this.token), ResponseDTO.class);
        if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null) {
            result = responseDTO.getData().getUser().getTipList().stream()
                    .anyMatch(tip -> tip.getSendBy() != null && userName.equalsIgnoreCase(tip.getSendBy().getName()));
        }
        return result;
    }
}
