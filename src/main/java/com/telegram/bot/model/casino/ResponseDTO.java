package com.telegram.bot.model.casino;

public class ResponseDTO {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "data=" + data +
                '}';
    }
}
