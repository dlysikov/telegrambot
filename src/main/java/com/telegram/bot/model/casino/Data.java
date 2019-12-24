package com.telegram.bot.model.casino;

public class Data {

    private User user;

    private SendTip sendTip;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SendTip getSendTip() {
        return sendTip;
    }

    public void setSendTip(SendTip sendTip) {
        this.sendTip = sendTip;
    }

    @Override
    public String toString() {
        return "Data{" +
                "user=" + user +
                ", sendTip=" + sendTip +
                '}';
    }
}
