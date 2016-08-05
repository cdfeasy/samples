package facebookbot.service;

import facebookbot.entity.Entry;
import facebookbot.entity.MessageResp;
import facebookbot.entity.Messaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by d.asadullin on 22.07.2016.
 */
@RestController()
@RequestMapping(value = "/facebook")
public class BotController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BotProcessor processor;
    @RequestMapping(value = "/send/test",method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody
    String greeting(@RequestParam Map<String,String> allRequestParams,@RequestBody(required = false) MessageResp body) {
        System.out.println(Collections.list(request.getHeaderNames()));
        System.out.println(body);
        if(body!=null&& body.getEntry()!=null){
            for(Entry entry:body.getEntry())
               if(entry.getMessaging()!=null) {
                   for(Messaging message:entry.getMessaging()) {
                       processor.process(message);
                   }
               }
            }
        return allRequestParams.get("hub.challenge");
    }
}
