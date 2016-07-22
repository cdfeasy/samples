package facebookbot;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by d.asadullin on 22.07.2016.
 */
public class facebookbot {
    @Test
    public void tes(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", "EAAEM03dlGIABAJSppCpiyJoyfvy2ICOsLl2xIH1mBZAkctAju5AC7euvdDsZBI9ZCKtvv7jIj9K7aNjq1GvpGxJqfPgB3u2E1lJioTOp32FCUAnOqLKz2Bs4xMIuGxMk70NpRZBs6JZA5VYFrrkXF94kFehUbRNVvrki4wjw6FgZDZD");
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v2.6/me/messages")
//                .queryParam("access_token", msisdn)
//                .queryParam("email", email)
//                .queryParam("clientVersion", clientVersion)
//                .queryParam("clientType", clientType)
//                .queryParam("issuerName", issuerName)
//                .queryParam("applicationName", applicationName);

        RestTemplate restTemplate = new RestTemplate();
        String object="{\n" +
                "  \"recipient\":{\n" +
                "    \"id\":\"884690841635895\"\n" +
                "  },\n" +
                "  \"message\":{\n" +
                "    \"text\":\"hello, world!\"\n" +
                "  }\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");

        HttpEntity<String> entity = new HttpEntity<String>(object, headers);

        ResponseEntity<String> exchange = restTemplate.exchange("https://graph.facebook.com/v2.6/me/messages?access_token={access_token}", HttpMethod.POST, entity, String.class,"EAAEM03dlGIABAJSppCpiyJoyfvy2ICOsLl2xIH1mBZAkctAju5AC7euvdDsZBI9ZCKtvv7jIj9K7aNjq1GvpGxJqfPgB3u2E1lJioTOp32FCUAnOqLKz2Bs4xMIuGxMk70NpRZBs6JZA5VYFrrkXF94kFehUbRNVvrki4wjw6FgZDZD");


      //  String s = restTemplate.postForObject("https://graph.facebook.com/v2.6/me/messages?access_token=EAAEM03dlGIABAJSppCpiyJoyfvy2ICOsLl2xIH1mBZAkctAju5AC7euvdDsZBI9ZCKtvv7jIj9K7aNjq1GvpGxJqfPgB3u2E1lJioTOp32FCUAnOqLKz2Bs4xMIuGxMk70NpRZBs6JZA5VYFrrkXF94kFehUbRNVvrki4wjw6FgZDZD", object,String.class,params);
        System.out.println(exchange.toString());


    }
}
