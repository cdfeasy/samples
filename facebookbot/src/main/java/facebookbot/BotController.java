package facebookbot;

import facebookbot.entity.MessageResp;
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
    @RequestMapping(value = "/send/test",method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody
    String greeting(@RequestParam Map<String,String> allRequestParams,@RequestBody(required = false) MessageResp body) {
        System.out.println(Collections.list(request.getHeaderNames()));
        System.out.println(body);
        if(allRequestParams!=null){
            for(Map.Entry entry:allRequestParams.entrySet()){
                System.out.println(entry.getKey()+"/"+entry.getValue());
            }
        }
        return allRequestParams.get("hub.challenge");
    }
}
