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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                MapMessage mapMsg = (MapMessage) message;

                String command = mapMsg.getString("command");
                if (!"EXECUTE_TRANSFER".equals(command)) {
                    LOGGER.warning("Unknown command received: " + command);
                    return;
                }

                String correlationId = message.getJMSCorrelationID();

                String sourceId = mapMsg.getString("sourceAccountId");
                String destId = mapMsg.getString("destinationAccountId");
                BigDecimal amount = new BigDecimal(mapMsg.getString("amount"));

                LOGGER.info("Processing CorrelationID: " + correlationId);

                accountService.transferMoney(UUID.fromString(sourceId), UUID.fromString(destId), amount);

            } catch (JMSException e) {
                LOGGER.log(Level.SEVERE, "JMS Protocol Error", e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Business Logic Error", e);
                throw new RuntimeException(e);
            }
        }
    }
}