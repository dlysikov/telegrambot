package com.telegram.bot.controller;

import com.telegram.bot.model.casino.ResponseDTO;
import com.telegram.bot.model.objects.fromcasino.Message;
import com.telegram.bot.model.objects.fromcasino.Response;
import com.telegram.bot.service.CacheService;
import com.telegram.bot.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.telegram.bot.utils.CommonUtils.getHeaders;

@Controller
@RequestMapping("/bot")
public class BotController {

//    private static final Logger LOGGER = Logger.getLogger(BotController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheService cacheService;

    @GetMapping(value = {"/", ""})
    public String start() {
        return "home";
    }

    @Autowired
    private NotificationService notificationService;

//    @Scheduled(cron = "${cron.request.period}")
    public void sendRequest() {
//        LOGGER.debug("-------------------------------------Send request start...-----------------------------------------------" + new Date());
//        LOGGER.debug("");
        System.out.println("-------------------------------------Send request start...-----------------------------------------------" + new Date());
        System.out.println();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String json = "{\"query\":\"query PublicChats(\\n\\t$includeHistory: Boolean = true\\n\\t$limit: Int\\n\\t$offset: Int\\n) {\\n\\tpublicChats {\\n\\t\\tid \\n\\t\\tname\\n\\t\\tisPublic\\n\\t\\tmessageList(limit: $limit, offset:$offset) @include(if: $includeHistory) {\\n\\t\\t\\t...MessageFragment\\n\\t\\t}\\n\\t}\\n}\\nfragment MessageFragment on ChatMessage {\\n\\t\\n\\tdata {\\n\\t\\n\\n\\t\\t\\n\\t\\t... on ChatMessageDataTip {\\n\\t\\t\\ttip {\\n\\t\\t\\t\\tid\\n\\t\\t\\t\\tamount\\n\\t\\t\\t\\tcurrency\\n\\t\\t\\t\\tsender: sendBy {\\n\\t\\t\\t\\t\\tid\\n\\t\\t\\t\\t\\tname\\n\\t\\t\\t\\t}\\n\\t\\t\\t\\treceiver: user {\\n\\t\\t\\t\\t\\tid\\n\\t\\t\\t\\t\\tname\\n\\t\\t\\t\\t}\\n\\t\\t\\t}\\n\\t\\t}\\n\\n\\t}\\n\\tcreatedAt\\n\\tuser {\\n\\t\\tid\\n\\t\\tname\\n\\t}\\n}\\n\"}";
        HttpEntity<String> request = new HttpEntity<>(json, httpHeaders);
        Response response = restTemplate.postForObject("https://api.stake.com/graphql", request, Response.class);

        response.getData().getPublicChats().stream().forEach(e -> {
            List<Message> withTip = e.getMessageList().stream().filter(message -> message.getData().getTip() != null).collect(Collectors.toList());
            withTip.stream().filter(filter -> filter.getData().getTip().getSender().getName().equals("Dinabot")).forEach(elem -> {
                System.out.println("Id: " + elem.getData().getTip().getId());
                System.out.println("Amount: " + elem.getData().getTip().getAmount());
                System.out.println("Currency: " + elem.getData().getTip().getCurrency());
                System.out.println("Sender:");
                System.out.println("   - id: " + elem.getData().getTip().getSender().getId());
                System.out.println("   - name: " + elem.getData().getTip().getSender().getName());
                System.out.println("Receiver:");
                System.out.println("   - id: " + elem.getData().getTip().getReceiver().getId());
                System.out.println("   - name: " + elem.getData().getTip().getReceiver().getName());
                System.out.println("Create at: " + elem.getCreatedAt());
                System.out.println();
//
//                LOGGER.debug("Id: " + elem.getData().getTip().getId());
//                LOGGER.debug("Amount: " + elem.getData().getTip().getAmount());
//                LOGGER.debug("Currency: " + elem.getData().getTip().getCurrency());
//                LOGGER.debug("Sender:");
//                LOGGER.debug("   - id: " + elem.getData().getTip().getSender().getId());
//                LOGGER.debug("   - name: " + elem.getData().getTip().getSender().getName());
//                LOGGER.debug("Receiver:");
//                LOGGER.debug("   - id: " + elem.getData().getTip().getReceiver().getId());
//                LOGGER.debug("   - name: " + elem.getData().getTip().getReceiver().getName());
//                LOGGER.debug("Create at: " + elem.getCreatedAt());
//                LOGGER.debug("");
            });
        });

    }

    @GetMapping("/print")
    public String printMaps() {
        cacheService.printCache();
        return "home";
    }

    @GetMapping("/send")
    public String sendTest() throws MessagingException {
        notificationService.sendMail();
        return "home";
    }

    @GetMapping("/check")
    public String checkUser(@RequestParam(name = "name") String name) {
        String json = "{\"query\":\"{  \\n  user {\\n    name id\\n    balances  {\\n     \\n      available {\\n       \\n       currency\\n     \\n        amount\\n      }\\n     \\n    }\\n  }\\n}\"}";
        HttpEntity<String> request = new HttpEntity<>(json, getHeaders("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJmOTMzNmI1ZC02ZWMzLTQ1YWItYjBhNC0yNzE1NjRhZDk3NzgiLCJpYXQiOjE1NzYwNTY4OTgsImV4cCI6MTU4MTI0MDg5OH0.EiL6egoLRLVO0bkZYobCZ6H13qPn5UookhNpmxazO4I"));
        ResponseDTO responseDTO = restTemplate.postForObject("https://api.stake.com/graphql", request, ResponseDTO.class) ;
        System.out.println(responseDTO);
        return "home";
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception e) {
        e.printStackTrace();
    }


}
