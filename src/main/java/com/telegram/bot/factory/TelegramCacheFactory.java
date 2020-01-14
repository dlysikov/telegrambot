package com.telegram.bot.factory;

import com.telegram.bot.model.pojo.UserWorkflow;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TelegramCacheFactory {

    private Map<String, UserWorkflow> workFlowMapCache;

    public Map<String, UserWorkflow> getWorkflowCache() {
        if (this.workFlowMapCache != null) {
            return this.workFlowMapCache;
        } else {
            this.workFlowMapCache = new ConcurrentHashMap<>();
            return this.workFlowMapCache;
        }
    }

}
