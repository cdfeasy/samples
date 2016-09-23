package storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import storage.services.CassandraClient;
import storage.services.StorageService;

/**
 * Created by d.asadullin on 13.09.2016.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(App.class);
        System.out.println(System.getProperties());
        ConfigurableApplicationContext run;
        if(args.length==1){
            logger.info("Start storage with conf from args "+args[0]);
            System.getProperties().put("spring.config.location",args[0].replace("--DStorageConfig=","").replace("-DStorageConfig=","").replace("StorageConfig=",""));
            run = SpringApplication.run(App.class, args);
        } else if(System.getProperty("StorageConfig")!=null){
            logger.info("Start storage with conf "+System.getProperty("StorageConfig"));
            System.getProperties().put("spring.config.location",System.getProperty("StorageConfig").trim());
            run = SpringApplication.run(App.class, args);
        }else{
            logger.info("Start storage with classpathconf");
            System.getProperties().put("spring.config.location","classpath:config.yaml");
            run = SpringApplication.run(App.class, args);
        }
        CassandraClient client=run.getBean(CassandraClient.class);
        client.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("stopping storage");
                run.stop();
                logger.info("storage stopped");
            }
        });


        //  ConfigurableApplicationContext run = SpringApplication.run(HttpServer.class, args);
        // FacebookSender bean = run.getBean(FacebookSender.class);
        // bean.send("Olol","884690841635895");
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
