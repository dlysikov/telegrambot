package com.telegram.bot.service.impl;

import com.telegram.bot.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.StringTokenizer;

@Component
public class NotificationServiceImpl implements NotificationService {

    @Value("${send.to}")
    private String to;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMail() throws MessagingException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        StringTokenizer stringTokenizer = new StringTokenizer(to.trim(), ";");
        while (stringTokenizer.hasMoreElements()) {
            mailMessage.addRecipients(Message.RecipientType.TO, stringTokenizer.nextToken());
        }
        String text = MessageFormat.format("From: {0} \n", "Stake") +
                MessageFormat.format("To: {0} \n", "PrimeDice") +
                MessageFormat.format("Stake username: {0} \n", "Dima") +
                MessageFormat.format("PD username: {0} \n", "Pasha") +
                MessageFormat.format("Currency: {0} \n", "btc") +
                MessageFormat.format("Amount: {0}", "0.00154");

        mailMessage.setSubject("Exchange request with SUCCESS status");
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }
}
