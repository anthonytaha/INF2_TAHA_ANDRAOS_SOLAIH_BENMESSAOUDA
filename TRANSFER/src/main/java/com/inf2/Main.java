package com.inf2;

import com.inf2.dao.RecipientDAO;
import com.inf2.dao.TransferDAO;
import com.inf2.dao.impl.*;
import com.inf2.domain.Transfer;
import com.inf2.mapper.RecipientMapper;
import com.inf2.mapper.TransferMapper;
import com.inf2.messaging.TransferMessageProducer;
import com.inf2.service.*;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.jms.JMSException;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.net.URI;

public class Main {
    // The base URI where the JAX-RS application is deployed
    public static final String BASE_URI = "http://localhost:8084/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        final ResourceConfig rc = new ResourceConfig()
                .packages("com.inf2.dao")
                .packages("com.inf2.dao.impl")
                .packages("com.inf2.domain")
                .packages("com.inf2.resource")
                .packages("com.inf2.service")
                .packages("com.inf2.filter")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
                        bind(cf).to(jakarta.jms.ConnectionFactory.class);

                        Queue transferQueue = new ActiveMQQueue("TransferQueue");
                        bind(transferQueue).to(jakarta.jms.Queue.class);

                        // Producer
                        bind(TransferMessageProducer.class).to(TransferMessageProducer.class);

                        bind(emf).to(EntityManagerFactory.class).in(jakarta.inject.Singleton.class);
                        bind(RecipientDAOImpl.class).to(RecipientDAO.class).in(jakarta.inject.Singleton.class);
                        bind(TransferDAOImpl.class).to(TransferDAO.class).in(jakarta.inject.Singleton.class);
                        bind(RecipientService.class).to(RecipientService.class).in(jakarta.inject.Singleton.class);
                        bind(TransferService.class).to(TransferService.class).in(jakarta.inject.Singleton.class);
                        bind(RecipientMapper.class).to(RecipientMapper.class).in(jakarta.inject.Singleton.class);
                        bind(TransferMapper.class).to(TransferMapper.class).in(jakarta.inject.Singleton.class);
                        bind(TransferMessageProducer.class).to(TransferMessageProducer.class).in(jakarta.inject.Singleton.class);
                    }
                });
//resources are auto-binded so no need to bind them here

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Exception {
        // Start the server with the corrected configuration
        final HttpServer server = startServer();

        System.out.println("Api server is starting on " + BASE_URI);
        System.out.println("Press Ctrl+C to stop the server.");

        // Keep the main thread running until the application is shut down
        Thread.currentThread().join();

        // Clean up on exit
        server.shutdownNow();
    }
}