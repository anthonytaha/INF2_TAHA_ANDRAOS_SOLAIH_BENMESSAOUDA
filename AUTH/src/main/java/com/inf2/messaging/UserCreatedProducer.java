package com.inf2.messaging;

import com.inf2.domain.Client;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

@Stateless
public class UserCreatedProducer {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "jms/UserCreatedQueue")
    private Queue queue;

    public void sendUserCreatedEvent(Client user) {
        try (JMSContext ctx = factory.createContext()) {
            String payload = "UserCreated:" + user.getId();
            ctx.createProducer().send(queue, payload);
            System.out.println("Sent JMS message: " + payload);
        }
    }
}
