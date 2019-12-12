package com.telegram.bot.service.impl;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.service.WorkFlowService;

import java.util.HashMap;
import java.util.Map;

public class WorkFlowServiceImpl implements WorkFlowService {

    private Map<Step, Step> workFlowMap = new HashMap<>();

    public WorkFlowServiceImpl() {

    }
}
