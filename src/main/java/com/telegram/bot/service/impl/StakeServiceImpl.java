package com.telegram.bot.service.impl;

import com.telegram.bot.model.casino.Account;
import com.telegram.bot.model.casino.Balance;
import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.service.CasinoService;
import org.graalvm.compiler.loop.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static com.telegram.bot.utils.CommonUtils.getBalanceRequest;
import static com.telegram.bot.utils.CommonUtils.getRequestForUserChecking;

@Service("stakeService")
public class StakeServiceImpl implements CasinoService {

    @Value("${stake.token}")
    private String token;

    @Value("${stake.url}")
    private String url;

    @Value("${query.balance.check}")
    private String balanceQuery;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean userExists(String userName) {
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequestForUserChecking(userName, this.token), ResponseDTO.class);
        return responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null;
    }

    @Override
    public boolean isBalanceEnough(Currency currency, BigDecimal amount) {
        boolean result = false;
        ResponseDTO responseDTO = restTemplate.postForObject(url, getBalanceRequest(balanceQuery, this.token), ResponseDTO.class);
        if (responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null ) {
            Account account = responseDTO.getData().getUser().getBalances().stream()
                    .filter(balance -> balance.getAvailable() != null && balance.getAvailable().getCurrency().equals(currency.getCode()))
                    .findFirst().map(Balance::getAvailable).orElse(new Account());
            if (account.getAmount() != null && account.getAmount().compareTo(amount) > 0) {
                result = true;
            }
        }
        return result;
    }
}
