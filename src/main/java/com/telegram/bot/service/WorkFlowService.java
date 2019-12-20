package com.telegram.bot.service;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;

import java.util.Map;

public interface WorkFlowService {

    Map<Step, Step> getWorkFlow();
    Step getNextStep(UserWorkflow userWorkflow);
    String getErrorResponse(Step step, String errorCode);

}
