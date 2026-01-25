package com.inf2;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8085/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.inf2")
                .register(RolesAllowedDynamicFeature.class);


        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();

        System.out.println("Api server is starting on " + BASE_URI);
        System.out.println("Press Ctrl+C to stop the server.");

        Thread.currentThread().join();
        server.shutdownNow();
    }
}
