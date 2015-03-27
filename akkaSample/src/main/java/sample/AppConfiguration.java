package sample;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.inject.Named;

import static main.SpringExtension.SpringExtProvider;

/**
 * The application configuration.
 */
@Service
class AppConfiguration {

    // the application context is needed to initialize the Akka Spring Extension
    @Autowired
    private ApplicationContext applicationContext;

//    @Autowired
//    @Named(value = "config")
//    private String config;

    /**
     * Actor system singleton for this application.
     */
    public ActorSystem actorSystem(String config) {
        ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.parseString(config));
        SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }
}
