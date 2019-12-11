package com.telegram.bot.model.pojo;

import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Step;

import java.util.Date;

public class UserWorkflow {

    private String chatId;
    private Date originalTimestamp = new Date();
    private Step step;
    private String stakeUserName;
    private String pdUserName;
    private Long amount;
    private Currency currency;
    private String from;
    private String to;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getStakeUserName() {
        return stakeUserName;
    }

    public void setStakeUserName(String stakeUserName) {
        this.stakeUserName = stakeUserName;
    }

    public String getPdUserName() {
        return pdUserName;
    }

    public void setPdUserName(String pdUserName) {
        this.pdUserName = pdUserName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(Date originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "UserWorkflow{" +
                "chatId='" + chatId + '\'' +
                ", originalTimestamp=" + originalTimestamp +
                ", step=" + step +
                ", stakeUserName='" + stakeUserName + '\'' +
                ", pdUserName='" + pdUserName + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
