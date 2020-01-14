package com.telegram.bot.controller;

import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bot")
public class BotController {

    @Autowired
    private CacheService cacheService;

    @GetMapping(value = {"/", ""})
    public String start() {
        return "home";
    }

    @GetMapping("/print")
    public String printMaps() {
        cacheService.printCache();
        return "home";
    }

    @GetMapping("/stop")
    public void stopService() throws InterruptedException {
        cacheService.lockToAdd();
        while (true) {
            if (cacheService.getCache().isEmpty()) {
                System.exit(0);
            }
            Thread.sleep(3000);
        }
    }

}
