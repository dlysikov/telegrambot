package com.telegram.bot.service.impl;

import com.telegram.bot.model.enums.Step;
import com.telegram.bot.service.WorkFlowService;
import com.telegram.bot.utils.Constants;
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
        workFlowMap.put(DIRECTION, STAKE_USER);
        workFlowMap.put(STAKE_USER, PD_USER);
        workFlowMap.put(PD_USER, CURRENCY);
        workFlowMap.put(CURRENCY, AMOUNT);
        workFlowMap.put(AMOUNT, CHECK_RESULT);
        workFlowMap.put(CHECK_RESULT, CHECK_RESULT);


        Map<String, String> amountErrors = new HashMap<>();
        amountErrors.put(AMOUNT_FORMAT_ERROR, "Amount should contain only digits.\nPlease try again:");
        amountErrors.put(AMOUNT_AVAILABILITY_ERROR, "Sorry, at the moment we are not able to support exchange operation fot such big amount.\nPlease enter another value or try later:");
        workFlowErrorsMap.put(AMOUNT, amountErrors);

        Map<String, String> directionErrors = new HashMap<>();
        directionErrors.put(WRONG_DIRECTION_ERROR, "Wrong direction error.\nPlease choose one from the following:");
        workFlowErrorsMap.put(DIRECTION, directionErrors);

        Map<String, String> currencyErrors = new HashMap<>();
        currencyErrors.put(WRONG_CURRENCY_ERROR, "Wrong currency.\nPlease choose one from the list:");
        workFlowErrorsMap.put(CURRENCY, currencyErrors);

        Map<String, String> stakeUserErrors = new HashMap<>();
        stakeUserErrors.put(WRONG_USER_ERROR, "User not found. Enter correct Stake user:");
        workFlowErrorsMap.put(STAKE_USER, stakeUserErrors);

        Map<String, String> pdUserErrors = new HashMap<>();
        pdUserErrors.put(WRONG_USER_ERROR, "User not found. Enter correct PrimeDice user:");
        workFlowErrorsMap.put(PD_USER, pdUserErrors);
    }

    @Override
    public Map<Step, Step> getWorkFlow() {
        return workFlowMap;
    }

    @Override
    public Step getNextStep(Step currentStep) {
        return workFlowMap.get(currentStep);
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
