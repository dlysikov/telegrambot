package com.telegram.bot.service.impl;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.service.WorkFlowService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.telegram.bot.model.enums.Step.*;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    private final static Map<Step, Step> workFlowMap = new HashMap<>();

    static {
        workFlowMap.put(START, DIRECTION);
        workFlowMap.put(DIRECTION, STAKE_USER);
        workFlowMap.put(STAKE_USER, PD_USER);
        workFlowMap.put(PD_USER, CURRENCY);
        workFlowMap.put(CURRENCY, AMOUNT);
    }

    @Override
    public Map<Step, Step> getWorkFlow() {
        return workFlowMap;
    }

    @Override
    public Step getNextStep(Step currentStep) {
        return workFlowMap.get(currentStep);
    }
}
