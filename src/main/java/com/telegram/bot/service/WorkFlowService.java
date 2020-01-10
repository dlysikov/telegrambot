package com.telegram.bot.service;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;

import java.util.Map;

public interface WorkFlowService {

    Step getNextStep(UserWorkflow userWorkflow);

}
