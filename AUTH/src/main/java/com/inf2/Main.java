package com.inf2;

import com.inf2.dao.*;
import com.inf2.dao.impl.*;
import com.inf2.service.*;
import com.inf2.filter.AuthenticationFilter;

import com.inf2.service.auth.AuthService;
import com.inf2.service.auth.TokenService;
import com.inf2.service.domain.AdvisorService;
import com.inf2.service.domain.ClientService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

// --- CRITICAL IMPORT CHANGES ---
// 1. Use the HK2 AbstractBinder (Supports bindFactory properly)
import org.glassfish.hk2.utilities.binding.AbstractBinder;
// 2. Use the HK2 Factory interface
import org.glassfish.hk2.api.Factory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.inject.Singleton;
import jakarta.inject.Inject; // Needed for the Provider
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;


import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static HttpServer startServer() {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        final ResourceConfig rc = new ResourceConfig()
                .packages("com.inf2.resource", "com.inf2.filter")
                .register(AuthenticationFilter.class)
                .register(RolesAllowedDynamicFeature.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {

                        bind(emf).to(EntityManagerFactory.class);
                        bindFactory(EntityManagerFactoryProvider.class)
                                .to(EntityManager.class)
                                .in(org.glassfish.jersey.process.internal.RequestScoped.class);

                        bind(HelloDAOImpl.class).to(HelloDAO.class).in(Singleton.class);
                        bind(ClientDAOImpl.class).to(ClientDAO.class).in(Singleton.class);
                        bind(AdvisorDAOImpl.class).to(AdvisorDAO.class).in(Singleton.class);

                        bind(TokenService.class).to(TokenService.class).in(Singleton.class);
                        bind(HelloService.class).to(HelloService.class).in(Singleton.class);
                        bind(ClientService.class).to(ClientService.class).in(Singleton.class);
                        bind(AdvisorService.class).to(AdvisorService.class).in(Singleton.class);
                        bind(AuthService.class).to(AuthService.class).in(Singleton.class);
                    }
                });

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println("Api server started on " + BASE_URI);
        Thread.currentThread().join();
    }

    // --- HELPER CLASS ---
    // Ensure this class is 'static' so Main can access it
    public static class EntityManagerFactoryProvider implements Factory<EntityManager> {

        private final EntityManagerFactory emf;

        @Inject // <--- Injection works here now
        public EntityManagerFactoryProvider(EntityManagerFactory emf) {
            this.emf = emf;
        }

        @Override
        public EntityManager provide() {
            return emf.createEntityManager();
        }

        @Override
        public void dispose(EntityManager instance) {
            if (instance.isOpen()) {
                instance.close();
            }
        }
    }
}