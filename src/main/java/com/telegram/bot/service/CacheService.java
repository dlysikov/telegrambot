package com.telegram.bot.service;

import com.telegram.bot.model.pojo.UserWorkflow;

public interface CacheService {

    UserWorkflow getUserWorkflow(String chatId);
    void removeByChatId(String chatId);
    void add(String chatId, UserWorkflow userWorkflow);
    void printCache();

}
