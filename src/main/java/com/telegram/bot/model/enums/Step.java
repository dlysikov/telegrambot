package com.telegram.bot.model.enums;

public enum Step {

    DIRECTION("Direction step"),
    STAKE_USER("Stake User Name"),
    CURRENCY("Currency setting up step"),
    AMOUNT("Amount Choice"),
    PD_USER("PD User Name");

    private String stepDesc;
    Step(String desc) {
        this.stepDesc = desc;
    }

    public String getStepDesc() {
        return stepDesc;
    }
}
