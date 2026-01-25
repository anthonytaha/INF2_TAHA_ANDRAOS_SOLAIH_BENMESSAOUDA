package com.inf2.messaging;

import com.inf2.domain.Client;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

public class UserCreatedProducer {

    @Inject
    private ConnectionFactory factory;

    @Inject
    private Queue queue;

    public void sendUserCreatedEvent(Client user) {
        try (JMSContext ctx = factory.createContext()) {
            String separator = ";";
            String fullName = user.getFirstName() + " " + user.getLastName();

            String payload = user.getId() + separator + System.currentTimeMillis() + separator
                    + user.getEmail() + separator
                    + fullName;
            ctx.createProducer().send(queue, payload);
            System.out.println("Sent JMS message: " + payload);
        } catch (Exception e) {
            System.err.println("Failed to send JMS message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}