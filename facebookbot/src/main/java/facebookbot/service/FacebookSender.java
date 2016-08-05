package facebookbot.service;

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
}
