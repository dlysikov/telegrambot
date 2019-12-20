package com.telegram.bot.service;

import com.telegram.bot.model.pojo.UserWorkflow;

import javax.mail.MessagingException;

public interface NotificationService {

    void sendMail(UserWorkflow userWorkflow) throws MessagingException;

}
