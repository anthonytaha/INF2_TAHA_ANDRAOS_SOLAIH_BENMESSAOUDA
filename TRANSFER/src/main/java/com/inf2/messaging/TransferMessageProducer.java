package com.inf2.messaging;


import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import jakarta.jms.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

@Singleton
public class TransferMessageProducer {

    private static final Logger LOGGER = Logger.getLogger(TransferMessageProducer.class.getName());

    @Inject
    private ConnectionFactory connectionFactory;

    @Inject
    private Queue transferQueue;

    public void sendTransferCommand(UUID correlationId, UUID sourceAccountId,
                                    UUID destinationAccountId, BigDecimal amount) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            producer = session.createProducer(transferQueue);

            // Mode PERSISTENT pour garantir la livraison
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // Créer le message
            MapMessage message = session.createMapMessage();
            message.setString("command", "EXECUTE_TRANSFER");
            message.setString("correlationId", correlationId.toString());
            message.setString("sourceAccountId", sourceAccountId.toString());
            message.setString("destinationAccountId", destinationAccountId.toString());
            message.setString("amount", amount.toString());
            message.setLong("timestamp", System.currentTimeMillis());

            // Correlation ID dans le header JMS
            message.setJMSCorrelationID(correlationId.toString());

            // Envoyer
            producer.send(message);
            session.commit();

            LOGGER.log(Level.INFO,
                    "Commande transfer envoyée - CorrelationId: {0}, Source: {1}, Destination: {2}, Montant: {3}",
                    new Object[]{correlationId, sourceAccountId, destinationAccountId, amount});

        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Échec envoi commande transfer", e);

            if (session != null) {
                try {
                    session.rollback();
                } catch (JMSException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Échec rollback session", rollbackEx);
                }
            }

            throw new RuntimeException("Échec envoi commande transfer", e);
        } finally {
            closeQuietly(producer);
            closeQuietly(session);
            closeQuietly(connection);
        }
    }

    private void closeQuietly(MessageProducer producer) {
        if (producer != null) {
            try {
                producer.close();
            } catch (JMSException e) {
                LOGGER.log(Level.WARNING, "Échec fermeture producer", e);
            }
        }
    }

    private void closeQuietly(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                LOGGER.log(Level.WARNING, "Échec fermeture session", e);
            }
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOGGER.log(Level.WARNING, "Échec fermeture connection", e);
            }
        }
    }
}
