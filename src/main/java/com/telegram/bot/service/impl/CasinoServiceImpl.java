package com.telegram.bot.service.impl;

import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.telegram.bot.utils.CommonUtils.*;
import static com.telegram.bot.utils.Constants.STAKE;

@Service
public abstract class CasinoServiceImpl implements CasinoService {

    private String token = null;

    private String url = null;

    private String balanceQuery = null;

    private String tipsQuery = null;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean userExists(String userName) {
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequestForUserChecking(userName, this.token), ResponseDTO.class);
        return responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null;
    }

    @Override
    public boolean isBalanceAvailable(Currency currency, BigDecimal amount) {
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequest(this.balanceQuery, this.token), ResponseDTO.class);
        return checkBalance(currency, amount, responseDTO);
    }

    private boolean wasAmountRecieved(UserWorkflow userWorkflow) {
        boolean result = false;
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequest(this.tipsQuery, this.token), ResponseDTO.class);
        if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null) {
            String userName = userWorkflow.getFrom().equals(STAKE) ? userWorkflow.getStakeUserName() : userWorkflow.getPdUserName();
            result = responseDTO.getData().getUser().getTipList().stream()
                    .anyMatch(tip -> tip.getSendBy() != null && userName.equalsIgnoreCase(tip.getSendBy().getName()));
        }
        return result;
    }
}
