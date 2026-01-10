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

            // 1. Create Connection
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 2. Create Consumer
            MessageConsumer consumer = session.createConsumer(transferQueue);

            // 3. Link the Listener (Injecting the Service manually or via DI)
            // Since we injected AccountService above, we can pass it to the listener
            TransferMessageListener listener = new TransferMessageListener(accountService);
            consumer.setMessageListener(listener);

            // 4. Start Connection
            connection.start();
            System.out.println("✅ JMS Listener is active and listening.");

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