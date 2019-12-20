package com.telegram.bot.service.impl;

import com.telegram.bot.model.pojo.UserWorkflow;
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
    public void sendMail(UserWorkflow userWorkflow) throws MessagingException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        addRecipients(mailMessage);

        String text =  "Please be informed that an exchange request was proceeded with the following attributes: \n" +
                MessageFormat.format("From: {0} \n", userWorkflow.getFrom()) +
                MessageFormat.format("To: {0} \n", userWorkflow.getTo()) +
                MessageFormat.format("Stake userId: {0}\n", userWorkflow.getStakeUserId()) +
                MessageFormat.format("Stake username: {0}\n", userWorkflow.getStakeUserName()) +
                MessageFormat.format("PD userId: {0}\n", userWorkflow.getPdUserId()) +
                MessageFormat.format("PD username: {0}\n", userWorkflow.getPdUserName()) +
                MessageFormat.format("Currency: {0} \n", userWorkflow.getCurrency()) +
                MessageFormat.format("Amount: {0}\n\n", userWorkflow.getAmount()) +
                MessageFormat.format("Telegram UserName: {0}\n", userWorkflow.getTelegramUserName()) +
                MessageFormat.format("Telegram first name: {0}\n", userWorkflow.getTelegramUserFirstName()) +
                MessageFormat.format("Telegram last name: {0}\n", userWorkflow.getTelegramUserLastName());

        mailMessage.setSubject(MessageFormat.format("Exchange request from {0} to {1}", userWorkflow.getFrom(), userWorkflow.getTo()));
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    private void addRecipients(MimeMessage mailMessage) throws MessagingException {
        StringTokenizer stringTokenizer = new StringTokenizer(to.trim(), ";");
        while (stringTokenizer.hasMoreElements()) {
            mailMessage.addRecipients(Message.RecipientType.TO, stringTokenizer.nextToken());
        }
    }
}
