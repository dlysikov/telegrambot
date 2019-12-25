package com.telegram.bot.model.enums;

public enum PreDefinedErrors {

    EMPTY_VALUE_ERROR("Value can not be empty"),
    WRONG_DIRECTION_ERROR("Wrong direction error.\nPlease choose one from the following:"),
    AMOUNT_FORMAT_ERROR("Amount should contain only digits.\nPlease try again:"),
    AMOUNT_AVAILABILITY_ERROR("Sorry, at the moment we are not able to support exchange operation for such big amount.\nPlease enter another value or try later:"),
    WRONG_CURRENCY_ERROR("Wrong currency.\nPlease choose one from the list:"),
    WRONG_STAKE_USER_ERROR("User not found. Enter correct Stake user:"),
    WRONG_PD_USER_ERROR("User not found. Enter correct Primedice user:"),
    NO_AMOUNT_RECEIVED_ERROR("Sorry. We can't find any exchange from you with such userName, currency and amount.\nPlease double check all attributes you entered. You can Confirm it one more time or Cancel it and create one more with right attributes:");

    private String errorMessage;

    public String getMessage() {
        return errorMessage;
    }

    PreDefinedErrors(String message) {
        this.errorMessage = message;
    }
}
