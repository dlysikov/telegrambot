package com.telegram.bot.service;

import com.telegram.bot.exception.NoAddingIsAllowedException;
import com.telegram.bot.model.pojo.UserWorkflow;

import java.util.Map;

public interface CacheService {

    Map<String, UserWorkflow> getCache();
    UserWorkflow getUserWorkflow(String chatId);
    void removeByChatId(String chatId);
    void add(String chatId, UserWorkflow userWorkflow) throws NoAddingIsAllowedException;
    void printCache();
    void resetUserWorkflow(UserWorkflow userWorkflow);
    void lockToAdd();
    void unlockToAdd();

}
