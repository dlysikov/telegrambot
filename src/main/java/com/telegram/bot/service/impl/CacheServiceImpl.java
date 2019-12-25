package com.telegram.bot.service.impl;

import com.telegram.bot.factory.TelegramCacheFactory;
import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CacheServiceImpl implements CacheService {

    private static Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

    @Autowired
    private TelegramCacheFactory telegramCacheFactory;


    @Override
    public UserWorkflow getUserWorkflow(String chatId) {
        return telegramCacheFactory.getWorkflowCache().get(chatId);
    }

    @Override
    public void removeByChatId(String chatId) {
        telegramCacheFactory.getWorkflowCache().remove(chatId);
    }

    @Override
    public void add(String chatId, UserWorkflow userWorkflow) {
        telegramCacheFactory.getWorkflowCache().put(chatId, userWorkflow);
    }

    @Override
    public void printCache() {
        if (!telegramCacheFactory.getWorkflowCache().isEmpty()) {
            telegramCacheFactory.getWorkflowCache().forEach((key, value) ->  log.info("ChatId: {}  ->  {}", key, value));
        } else {
            log.info("Empty workFlow Cache!");
        }
    }

    @Override
    public void resetUserWorkflow(UserWorkflow userWorkflow) {
        userWorkflow.setStep(Step.START);
        userWorkflow.setErrorMessage(null);
        userWorkflow.setPdUserName(null);
        userWorkflow.setPdUserId(null);
        userWorkflow.setStakeUserId(null);
        userWorkflow.setStakeUserName(null);
        userWorkflow.setAmount(null);
        userWorkflow.setCurrency(null);
    }
}
