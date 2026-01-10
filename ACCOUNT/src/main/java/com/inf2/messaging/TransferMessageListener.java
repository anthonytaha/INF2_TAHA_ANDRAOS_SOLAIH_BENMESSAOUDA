package com.inf2.messaging;

import com.inf2.service.AccountService;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

// PATTERN 3: Point-to-Point Channel
// We bind specifically to a javax.jms.Queue
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/transferQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class TransferMessageListener implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(TransferMessageListener.class.getName());

    private final AccountService accountService;

    public TransferMessageListener(AccountService accountService) {
        this.accountService = accountService;
    }

    // PATTERN 4: Transactional Client
    // REQUIRED ensures this MDB runs inside a JTA transaction.
    // If onMessage throws RuntimeException, the container rolls back the message to the Queue.
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                MapMessage mapMsg = (MapMessage) message;

                // PATTERN 2: Command Message
                // We verify the "command" string implies an imperative instruction
                String command = mapMsg.getString("command");
                if (!"EXECUTE_TRANSFER".equals(command)) {
                    LOGGER.warning("Unknown command received: " + command);
                    return; // Ack message but don't process (or throw exception to DLQ)
                }

                // PATTERN 5: Correlation Identifier
                // strictly reading from the JMS Header (not the body)
                String correlationId = message.getJMSCorrelationID();

                // Extract payload
                String sourceId = mapMsg.getString("sourceAccountId");
                String destId = mapMsg.getString("destinationAccountId");
                BigDecimal amount = new BigDecimal(mapMsg.getString("amount"));

                LOGGER.info("Processing CorrelationID: " + correlationId);

                // Execute Business Logic (Database Updates)
                accountService.transferMoney(UUID.fromString(sourceId), UUID.fromString(destId), amount);

            } catch (JMSException e) {
                LOGGER.log(Level.SEVERE, "JMS Protocol Error", e);
                // PATTERN 4 (Retry): Throwing RuntimeException forces rollback & retry
                throw new RuntimeException(e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Business Logic Error", e);
                // PATTERN 4 (Retry): If DB fails, we throw to rollback message
                throw new RuntimeException(e);
            }
        }
    }
}