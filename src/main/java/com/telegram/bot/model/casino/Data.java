package com.telegram.bot.model.casino;

public class Data {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Data{" +
                "user=" + user +
                '}';
    }
}
