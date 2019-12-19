package com.telegram.bot.model.casino;

public class Balance {

    private Account available;

    public Account getAvailable() {
        return available;
    }

    public void setAvailable(Account available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "available=" + available +
                '}';
    }
}
