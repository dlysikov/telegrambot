package com.telegram.bot.service;

import com.telegram.bot.model.enums.Step;

import java.util.Map;

public interface WorkFlowService {

    Map<Step, Step> getWorkFlow();
    Step getNextStep(Step currentStep);

}
