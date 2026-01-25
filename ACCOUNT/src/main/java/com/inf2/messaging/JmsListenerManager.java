package com.inf2.messaging;

import com.inf2.service.AccountService;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

public class JmsListenerManager implements ContainerLifecycleListener {

    @Inject
    private ConnectionFactory connectionFactory;

    @Inject
    private Queue transferQueue;

    @Inject
    private AccountService accountService;

    private Connection connection;

    @Override
    public void onStartup(Container container) {
        try {
            System.out.println("Starting JMS Listener...");

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createConsumer(transferQueue);

            TransferMessageListener listener = new TransferMessageListener(accountService);
            consumer.setMessageListener(listener);

            connection.start();
            System.out.println("JMS Listener is active and listening.");

        } catch (JMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start JMS Listener", e);
        }
    }

    @Override
    public void onShutdown(Container container) {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("JMS Connection closed.");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReload(Container container) { /* Not needed */ }
}