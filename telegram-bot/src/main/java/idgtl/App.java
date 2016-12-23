package idgtl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by d.asadullin on 12.10.2016.
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "alerting.xml");

        Bot bot = ctx.getBean(Bot.class);
        bot.start();

    }
}
