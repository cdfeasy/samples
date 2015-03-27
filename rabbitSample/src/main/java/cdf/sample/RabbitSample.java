package cdf.sample;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by d.asadullin on 22.01.2015.
 */
public class RabbitSample {
    private Connection connection;
    Channel channel;
    public RabbitSample(){
        AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties minBasic = bob.build();
        AMQP.BasicProperties minPersistentBasic = bob.deliveryMode(2).build();
        AMQP.BasicProperties persistentBasic
                = bob.priority(0).contentType("application/octet-stream").build();
        AMQP.BasicProperties persistentTextPlain = bob.contentType("text/plain").build();
    }

    public void connect() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
      //  factory.setUsername(userName);
       // factory.setPassword(password);
       // factory.setVirtualHost(virtualHost);
        factory.setHost("sbox8.i-free.dev");
        factory.setPort(5672);
        connection = factory.newConnection();
    }

    public void send() throws IOException {
        channel = connection.createChannel();
        channel.exchangeDeclare("rabbitTest", "direct", false);
        channel.queueDeclare("rabbitTest", true, false, false, null);
        channel.queueBind("rabbitTest", "rabbitTest", "");
        byte[] messageBodyBytes = "Hello, world!".getBytes();
        channel.basicPublish("rabbitTest", "", null, messageBodyBytes);

    }

    public void receive() throws IOException {
        GetResponse response;
        while ((response= channel.basicGet("rabbitTest", true))!=null) {
                AMQP.BasicProperties props = response.getProps();
                byte[] body = response.getBody();
                long deliveryTag = response.getEnvelope().getDeliveryTag();
                System.out.println(new String(body));
        }
        channel.close();
        connection.close();
    }
}
