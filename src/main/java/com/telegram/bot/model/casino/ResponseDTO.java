package com.telegram.bot.model.casino;

import java.util.List;

public class ResponseDTO {

    private Data data;

    private List<Error> errors;

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

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
                ", errors=" + errors +
                '}';
    }
}
