package com.inf2.messaging;

import jakarta.inject.Inject;
import jakarta.jms.*;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

public class JmsLifecycleManager implements ContainerLifecycleListener {

    @Inject
    private ConnectionFactory connectionFactory;

    @Inject
    private Queue queue; // The same queue injected into the Producer

    private Connection connection;

    @Override
    public void onStartup(Container container) {
        try {
            // 1. Create Connection to Broker
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 2. Create Consumer
            MessageConsumer consumer = session.createConsumer(queue);

            // 3. Attach YOUR Listener
            consumer.setMessageListener(new UserCreatedListener());

            // 4. Start Flow
            connection.start();
            System.out.println("JMS Listener started for UserCreatedQueue");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShutdown(Container container) {
        try {
            if (connection != null) connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReload(Container container) {}
}