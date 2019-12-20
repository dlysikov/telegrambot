package com.telegram.bot.model.enums;

public enum Step {

    START("Start step"),
    DIRECTION("Direction step"),
    STAKE_USER("Stake User Name"),
    CURRENCY("Currency setting up step"),
    AMOUNT("Amount Choice"),
    PD_USER("PD User Name"),
    CHECK_RESULT("Check Result step"),
    CONFIRM_RESULT("Confirm result step"),
    DECISION_POINT("Decision point step")
    ;

    private String stepDesc;
    Step(String desc) {
        this.stepDesc = desc;
    }

    public String getStepDesc() {
        return stepDesc;
    }
}
