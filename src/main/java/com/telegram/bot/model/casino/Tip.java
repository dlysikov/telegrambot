package com.telegram.bot.model.casino;

import java.math.BigDecimal;
import java.util.Date;

public class Tip {

    private User sendBy;
    private BigDecimal amount;
    private String currency;
    private Date createdAt;

    public User getSendBy() {
        return sendBy;
    }

    public void setSendBy(User sendBy) {
        this.sendBy = sendBy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Tip{" +
                "sendBy=" + sendBy +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
