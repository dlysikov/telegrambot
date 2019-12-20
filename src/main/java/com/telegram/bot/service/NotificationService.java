package com.telegram.bot.service;

import javax.mail.MessagingException;

public interface NotificationService {

    void sendMail() throws MessagingException;

}
