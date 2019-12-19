package com.telegram.bot.service.impl;

import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.service.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.telegram.bot.utils.CommonUtils.getRequestForUserChecking;

@Service("primeDiceService")
public class PrimeDiceServiceImpl implements CasinoService {

    @Value("${primedice.token}")
    private String token;

    @Value("${primedice.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean userExists(String userName) {
        ResponseDTO responseDTO = restTemplate.postForObject(url, getRequestForUserChecking(userName, this.token), ResponseDTO.class);
        return responseDTO != null && responseDTO.getData() != null && responseDTO.getData().getUser() != null;
    }

}
