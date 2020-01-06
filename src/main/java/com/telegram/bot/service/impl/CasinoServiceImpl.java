package com.telegram.bot.service.impl;

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
import java.util.Date;

import static com.telegram.bot.utils.CommonUtils.*;

@Service
public abstract class CasinoServiceImpl implements CasinoService {

    private static Logger log = LoggerFactory.getLogger(CasinoServiceImpl.class);

    public abstract String getToken();

    public abstract String getUrl();

    public abstract String getBalanceQuery();

    public abstract String getTipsQuery();

    public abstract String getUserId(UserWorkflow userWorkflow);

    public abstract String getChatId();

    private long SHIFT_ACTIVITY = 1000 * 60 * 5;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public User getUserByName(String userName) {
        User user;
        try {
            ResponseDTO responseDTO = restTemplate.postForObject(getUrl(), getRequestForUserChecking(userName, getToken()), ResponseDTO.class);
            if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null) {
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
        ResponseDTO responseDTO = restTemplate.postForObject(getUrl(), getRequest(getBalanceQuery(), getToken()), ResponseDTO.class);
        return checkBalance(currency, amount, responseDTO);
    }

    public boolean wasAmountReceived(UserWorkflow userWorkflow) {
        boolean result = false;
        ResponseDTO responseDTO = restTemplate.postForObject(getUrl(), getRequest(getTipsQuery(), getToken()), ResponseDTO.class);
        if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null) {
            BigDecimal amountForCheck = new BigDecimal(userWorkflow.getAmount());
            Date shiftedDate = new Date(userWorkflow.getOriginalTimestamp().getTime() - SHIFT_ACTIVITY);
            result = responseDTO.getData().getUser().getTipList().stream()
                    .anyMatch(tip -> tip.getSendBy() != null && getUserId(userWorkflow).equalsIgnoreCase(tip.getSendBy().getId())
                            && userWorkflow.getCurrency().getCode().equals(tip.getCurrency())
                            && amountForCheck.compareTo(tip.getAmount()) == 0
                            && tip.getCreatedAt().after(shiftedDate)
                    );

        }
        return result;
    }

    public ResponseDTO sendTips(UserWorkflow userWorkflow) {
        ResponseDTO responseDTO = restTemplate.postForObject(getUrl(), getSendTipRequest(getUserId(userWorkflow), userWorkflow.getAmountForExchange(), userWorkflow.getCurrency().getCode(), getChatId(), getToken()), ResponseDTO.class);
    return responseDTO;
    }

}
