package com.telegram.bot.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        try {
//            telegramBotsApi.registerBot(bot);
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        }
    }

}
