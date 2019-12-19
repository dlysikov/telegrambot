package com.telegram.bot.model.casino;

import java.math.BigDecimal;

public class Account {
    private String currency;
    private BigDecimal amount;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
