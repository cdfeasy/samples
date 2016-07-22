package facebookbot;

//import org.eclipse.jetty.server.*;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
//import org.eclipse.jetty.xml.XmlConfiguration;
//
//import java.net.URL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by d.asadullin on 20.07.2016.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@ImportResource("classpath:config.xml")
public class HttpServer {

    public static void main(String[] args) {
        SpringApplication.run(HttpServer.class, args);
    }


//    public static void main(String[] args) throws InterruptedException {
//        HttpServer server = new HttpServer();
//        server.start();
//      //  Thread.sleep(1000000);
//    }
//
//    protected void start() {
//        try {
//            URL serverConfig = this.getClass().getResource("/jetty.xml");
//            XmlConfiguration serverConfigurator = new XmlConfiguration(serverConfig);
//            Server server = (Server) serverConfigurator.configure();
//            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            context.setContextPath("/facebook");
//            server.setHandler(context);
//            ServletHolder jerseyServlet = context.addServlet(
//                    org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//            jerseyServlet.setInitOrder(0);
//            jerseyServlet.setInitParameter(
//                    "jersey.config.server.provider.classnames",
//                    FacebookConnector.class.getCanonicalName());
//
//           // server.setConnectors(new Connector[]{connector});
//            server.start();
//            server.join();
//        } catch (Exception e) {
//            //
//        }
//    }
}
