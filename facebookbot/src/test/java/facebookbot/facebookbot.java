package facebookbot;

import facebookbot.service.BotProcessor;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by d.asadullin on 22.07.2016.
 */
public class facebookbot {
    @Test
    public void tes() throws UnsupportedEncodingException {
       // Map<String, String> params = new HashMap<String, String>();
      //  params.put("access_token", "EAAEM03dlGIABAJSppCpiyJoyfvy2ICOsLl2xIH1mBZAkctAju5AC7euvdDsZBI9ZCKtvv7jIj9K7aNjq1GvpGxJqfPgB3u2E1lJioTOp32FCUAnOqLKz2Bs4xMIuGxMk70NpRZBs6JZA5VYFrrkXF94kFehUbRNVvrki4wjw6FgZDZD");
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v2.6/me/messages")
//                .queryParam("access_token", msisdn)
//                .queryParam("email", email)
//                .queryParam("clientVersion", clientVersion)
//                .queryParam("clientType", clientType)
//                .queryParam("issuerName", issuerName)
//                .queryParam("applicationName", applicationName);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        RestTemplate restTemplate = new RestTemplate();
        String object=String.format("{\n" +
                "  \"recipient\":{\n" +
                "    \"id\":\"884690841635895\"\n" +
                "  },\n" +
                " message: {\n" +
                        "\"attachment\":{\n" +
                        "      \"type\":\"template\",\n" +
                        "      \"payload\":{\n" +
                        "        \"template_type\":\"button\",\n" +
                        "        \"text\":\"What do you want to do next?\",\n" +
                        "        \"buttons\":[\n" +
                        "          {\n" +
                        "            \"type\":\"web_url\",\n" +
                        "            \"url\":\"https://petersapparel.parseapp.com\",\n" +
                        "            \"title\":\"Show Website\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"type\":\"postback\",\n" +
                        "            \"title\":\"Start Chatting\",\n" +
                        "            \"payload\":\"USER_DEFINED_PAYLOAD\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }"+
                        "  }"+
                "  }"+
                "  }");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<String>(object, headers);
        ResponseEntity exchange=null;
        try {
            exchange = restTemplate.exchange("https://graph.facebook.com/v2.6/me/messages?access_token={access_token}", HttpMethod.POST, entity, String.class, "EAAEM03dlGIABAJANenZBNFlPtEZBZCjexOLXUZBTZAZAAY56I1uEO6SPAUeJZCH9EejtZCY0GR4izQIGxZBbPjsD8CTmBeGxxqJPdeVP2dPNJyEkO9ZBmSSn92QvK1r2G8o2r7djD9ZCyABHEZAmsU6XzzIjxxDtj3G4HhzemVpNssvYZCwZDZD");
            System.out.println(exchange.toString());
        }catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }

      //  String s = restTemplate.postForObject("https://graph.facebook.com/v2.6/me/messages?access_token=EAAEM03dlGIABAJSppCpiyJoyfvy2ICOsLl2xIH1mBZAkctAju5AC7euvdDsZBI9ZCKtvv7jIj9K7aNjq1GvpGxJqfPgB3u2E1lJioTOp32FCUAnOqLKz2Bs4xMIuGxMk70NpRZBs6JZA5VYFrrkXF94kFehUbRNVvrki4wjw6FgZDZD", object,String.class,params);



    }
}
