package com.telegram.bot.model.pojo;

import com.telegram.bot.model.enums.Currency;
import com.telegram.bot.model.enums.Step;

import java.util.Date;

public class UserWorkflow {

    private String chatId;
    private Date originalTimestamp = new Date();
    private Step step;
    private String stakeUserId;
    private String stakeUserName;
    private String pdUserId;
    private String pdUserName;
    private String amount;
    private Currency currency;
    private String from;
    private String to;
    private String errorCode;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStakeUserId() {
        return stakeUserId;
    }

    public void setStakeUserId(String stakeUserId) {
        this.stakeUserId = stakeUserId;
    }

    public String getPdUserId() {
        return pdUserId;
    }

    public void setPdUserId(String pdUserId) {
        this.pdUserId = pdUserId;
    }

    @Override
    public String toString() {
        return "UserWorkflow{" +
                "chatId='" + chatId + '\'' +
                ", originalTimestamp=" + originalTimestamp +
                ", step=" + step +
                ", stakeUserId='" + stakeUserId + '\'' +
                ", stakeUserName='" + stakeUserName + '\'' +
                ", pdUserId='" + pdUserId + '\'' +
                ", pdUserName='" + pdUserName + '\'' +
                ", amount='" + amount + '\'' +
                ", currency=" + currency +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
