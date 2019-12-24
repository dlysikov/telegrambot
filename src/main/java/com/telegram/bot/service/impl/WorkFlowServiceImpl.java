package com.telegram.bot.service.impl;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.model.pojo.UserWorkflow;
import com.telegram.bot.service.WorkFlowService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.telegram.bot.model.enums.Step.*;
import static com.telegram.bot.utils.Constants.*;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    private final static Map<Step, Step> workFlowMap = new HashMap<>();
    private final static Map<Step, Map<String, String>> workFlowErrorsMap = new HashMap<>();

    static {
        workFlowMap.put(START, DIRECTION);
        workFlowMap.put(DIRECTION, DECISION_POINT);
        workFlowMap.put(STAKE_USER, DECISION_POINT);
        workFlowMap.put(PD_USER, DECISION_POINT);
        workFlowMap.put(CURRENCY, AMOUNT);
        workFlowMap.put(AMOUNT, CHECK_RESULT);
        workFlowMap.put(CHECK_RESULT, CONFIRM_RESULT);
    }

    @Override
    public Map<Step, Step> getWorkFlow() {
        return workFlowMap;
    }

    @Override
    public Step getNextStep(UserWorkflow userWorkflow) {
        Step currentStep = userWorkflow.getStep();
        Step nextStep = workFlowMap.get(currentStep);
        if (DECISION_POINT.equals(nextStep)) {
            switch (currentStep) {
                case DIRECTION:
                    nextStep = STAKE.equals(userWorkflow.getFrom()) ? STAKE_USER : PD_USER;
                    break;
                case STAKE_USER:
                    nextStep = userWorkflow.getPdUserId() != null ? CURRENCY : PD_USER;
                    break;
                case PD_USER:
                    nextStep = userWorkflow.getStakeUserId() != null ? CURRENCY : STAKE_USER;
                    break;
            }
        }
        return nextStep;
    }

    @Override
    public String getErrorResponse(Step step, String errorCode) {
        Map<String, String> errorsMap = workFlowErrorsMap.get(step);
        if (errorsMap != null) {
            return errorsMap.get(errorCode);
        }
        return null;
    }
}
