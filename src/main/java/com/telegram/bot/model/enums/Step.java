package com.telegram.bot.model.enums;

public enum Step {

    START("Start step"),
    DIRECTION("Direction step"),
    STAKE_USER("Stake User Name"),
    CURRENCY("Currency setting up step"),
    AMOUNT("Amount Choice"),
    PD_USER("PD User Name"),
    NO_HANDLER("No Handler Step"),
    HADLE_IS_DONE("Handle is done");

    private String stepDesc;
    Step(String desc) {
        this.stepDesc = desc;
    }

    public String getStepDesc() {
        return stepDesc;
    }
}
