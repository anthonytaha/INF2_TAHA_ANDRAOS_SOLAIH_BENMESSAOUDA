package com.inf2.messaging;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

public class UserCreatedListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                System.out.println("Received JMS message: " + ((TextMessage) message).getText());
            } else {
                System.out.println("Received non-text message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}