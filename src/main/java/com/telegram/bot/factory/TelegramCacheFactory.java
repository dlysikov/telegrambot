package com.telegram.bot.factory;

import com.telegram.bot.model.pojo.UserWorkflow;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class TelegramCacheFactory {

    private Map<String, UserWorkflow> workFlowMapCache;

    public Map<String, UserWorkflow> getWorkflowCache() {
        if (this.workFlowMapCache != null) {
            return this.workFlowMapCache;
        } else {
            this.workFlowMapCache = new HashMap<>();
            return this.workFlowMapCache;
        }
    }

}
