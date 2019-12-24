package com.telegram.bot.model.casino;

import java.util.List;

public class Error {
    private List<String> path;
    private String message;
    private String errorType;
    private Data data;

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Error{" +
                "path=" + path +
                ", message='" + message + '\'' +
                ", errorType='" + errorType + '\'' +
                ", data=" + data +
                '}';
    }
}
