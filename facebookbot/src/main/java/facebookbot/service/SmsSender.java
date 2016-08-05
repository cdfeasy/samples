package facebookbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.befree.common.message.impl.TextBody;
import ru.befree.common.message.proto.MessageBody;
import ru.befree.messaging.bus.Sender;
import ru.befree.messaging.j2ee.interfaces.SenderRemote;
import ru.befree.messaging.message.impl.MessagingNode;
import ru.befree.messaging.message.impl.OutboundMessage;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by d.asadullin on 14.07.2016.
 */
//@Scope("singleton")
//@Component
public class SmsSender {
    @Autowired
    @Value("${connect.url}")
    String url;
    @Autowired
    @Value("${connect.node.id}")
    Integer nodeId;
    @Autowired
    @Value("${connect.node.name}")
    String nodeName;

    Sender sender;
    MessagingNode node;

    @PostConstruct
    public void init() throws NamingException {
        Properties p = new Properties();
        p.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        p.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        p.setProperty("java.naming.provider.url", url);
        InitialContext ctx = new InitialContext(p);
        sender = (Sender) ctx.lookup(SenderRemote.REMOTE_JNDI_NAME);
        node = new MessagingNode(nodeId, nodeName);
//        OutboundMessage message = new OutboundMessage(node, "test", "+79817532172");
//        MessageBody body = new TextBody("пыщь", StandardCharsets.UTF_8.name());
//        message.setBody(body);
//        sender.send(message);
    }

    public boolean send(String msisdn, String text) {
        try {
            OutboundMessage message = new OutboundMessage(node, "Citibank", msisdn);
            MessageBody body = null;
            body = new TextBody(text, StandardCharsets.UTF_8.name());
            message.setBody(body);
            sender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
