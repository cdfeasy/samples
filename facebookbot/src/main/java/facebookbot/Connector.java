package facebookbot;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.xml.XmlConfiguration;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URL;

/**
 * Created by d.asadullin on 20.07.2016.
 */
@Path("/send")
public class Connector {
    @PostConstruct
    public void init() {
    }

    @POST
    @Path("/test")
    public String sendMessagePostTest(String request, @Context final HttpServletResponse response) {
        try {
            System.out.println(request);
            response.setStatus(200);
            try {
                response.flushBuffer();
            }catch(Exception e){}
            return "ok";
        } catch (Exception ex) {
         //
        }
        return "ok";
    }

}
