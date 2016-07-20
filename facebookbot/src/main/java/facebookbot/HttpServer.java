package facebookbot;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.xml.XmlConfiguration;

import java.net.URL;

/**
 * Created by d.asadullin on 20.07.2016.
 */
public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        HttpServer server=new HttpServer();
        server.start();
        Thread.sleep(1000000);
    }

    protected void start()  {
        try {
            URL serverConfig = this.getClass().getResource("/jetty.xml");
            XmlConfiguration serverConfigurator = new XmlConfiguration(serverConfig);

            Server jettyServer = (Server) serverConfigurator.configure();
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/facebook");
            jettyServer.setHandler(context);
            ServletHolder jerseyServlet = context.addServlet(
                    org.glassfish.jersey.servlet.ServletContainer.class, "/*");
            jerseyServlet.setInitOrder(0);
            jerseyServlet.setInitParameter(
                    "jersey.config.server.provider.classnames",
                    Connector.class.getCanonicalName());
            jettyServer.start();
        } catch (Exception e) {
           //
        }
    }
}
