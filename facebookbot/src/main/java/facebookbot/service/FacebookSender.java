package facebookbot.service;

import com.google.gson.Gson;
import facebookbot.entity.menu.SendMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by d.asadullin on 25.07.2016.
 */
@Component
public class FacebookSender {
    @Autowired
    @Value("${facebook.sender.access_token}")
    String token;

    public void send(String message,String recipient){
            RestTemplate restTemplate = new RestTemplate();
            String object=String.format("{\n" +
                    "  \"recipient\":{\n" +
                    "    \"id\":\"%s\"\n" +
                    "  },\n" +
                    "  \"message\":{\n" +
                    "    \"text\":\"%s\"\n" +
                    "  }\n" +
                    "}",recipient,message);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type","application/json; charset=utf-8");
            HttpEntity<String> entity = new HttpEntity<String>(object, headers);
            ResponseEntity<String> exchange = restTemplate.exchange("https://graph.facebook.com/v2.6/me/messages?access_token={access_token}", HttpMethod.POST, entity, String.class,token);
    }

    public void sendMenu(String message,String recipient,Map<String,String> buttons){
        RestTemplate restTemplate = new RestTemplate();

        SendMessage.SendBuilder builder= new SendMessage.SendBuilder(recipient).withText(message);
        for(Map.Entry<String,String> entry:buttons.entrySet()){
            builder.withQuick(entry.getKey(),entry.getValue());
        }
        Gson gson=new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(builder.build()), headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://graph.facebook.com/v2.6/me/messages?access_token={access_token}", HttpMethod.POST, entity, String.class,token);
    }
}
