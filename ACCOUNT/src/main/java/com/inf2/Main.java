package com.inf2;

import com.inf2.dao.AccountDAO;
import com.inf2.dao.impl.AccountDAOImpl;
import com.inf2.filter.AuthenticationFilter;
import com.inf2.mapper.AccountMapper;
import com.inf2.messaging.JmsListenerManager;
import com.inf2.service.AccountService;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import java.net.URI;

public class Main {
    // The base URI where the JAX-RS application is deployed
    public static final String BASE_URI = "http://localhost:8083/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        final ResourceConfig rc = new ResourceConfig()
                .packages("com.inf2.dao")
                .packages("com.inf2.dao.impl")
                .packages("com.inf2.domain")
                .packages("com.inf2.resource")
                .packages("com.inf2.service")
                .packages("com.inf2.dto")
                .packages("com.inf2.mapper")
                .packages("com.inf2.filter")
                .register(AuthenticationFilter.class)
                .register(RolesAllowedDynamicFeature.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {

                        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
                        bind(cf).to(jakarta.jms.ConnectionFactory.class);

                        Queue transferQueue = new ActiveMQQueue("TransferQueue");
                        bind(transferQueue).to(jakarta.jms.Queue.class);

                        bind(emf).to(EntityManagerFactory.class).in(jakarta.inject.Singleton.class);
                        bind(AccountDAOImpl.class).to(AccountDAO.class).in(jakarta.inject.Singleton.class);
                        bind(AccountService.class).to(AccountService.class).in(jakarta.inject.Singleton.class);
                        bind(AccountMapper.class).to(AccountMapper.class).in(jakarta.inject.Singleton.class);

                        bind(JmsListenerManager.class).to(ContainerLifecycleListener.class).in(jakarta.inject.Singleton.class);
                    }
                });


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