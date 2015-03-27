package cdf.sample;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

/**
 * Created by d.asadullin on 28.01.2015.
 */
public class SpringRabbit {
    public static void main(final String... args) throws Exception {
        ConnectionFactory cf = new CachingConnectionFactory("sbox8.i-free.dev",5672);
        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(cf);
        Queue queue1 = new Queue("myQueue1");
        Queue queue2 = new Queue("myQueue2");
        admin.declareQueue(queue1);
        admin.declareQueue(queue2);
        TopicExchange exchange = new TopicExchange("myExchange");
        admin.declareExchange(exchange);
        admin.declareBinding(
                BindingBuilder.bind(queue1).to(exchange).with("foo.*"));
        admin.declareBinding(
                BindingBuilder.bind(queue2).to(exchange).with("bar.*"));

        // set up the listener1 and container
        SimpleMessageListenerContainer container1 =
                new SimpleMessageListenerContainer(cf);
        SimpleMessageListenerContainer container2 =
                new SimpleMessageListenerContainer(cf);
        Object listener1 = new Object() {
            public void handleMessage(String foo) {
                System.out.println(foo);
            }
        };
        Object listener2 = new Object() {
            public void handleMessage(String foo) {
                System.out.println(foo);
            }
        };
        MessageListenerAdapter adapter1 = new MessageListenerAdapter(listener1);
        container1.setMessageListener(adapter1);
        container1.setQueueNames("myQueue1");
        container1.start();
        MessageListenerAdapter adapter2 = new MessageListenerAdapter(listener2);
        container2.setMessageListener(adapter2);
        container2.setQueueNames("myQueue2");
        container2.start();

        // send something
        RabbitTemplate template = new RabbitTemplate(cf);
        template.convertAndSend("myExchange", "foo.bar", "Hello1, world!");
        template.convertAndSend("myExchange", "bar.foo", "Hello2, world!");
        Thread.sleep(1000);
        container1.stop();
        container2.stop();
        container1.shutdown();
        container2.shutdown();

    }
}
