package com.inf2.service;

import com.inf2.dao.impl.HelloDAOImpl;
import com.inf2.domain.HelloMessage;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

@jakarta.inject.Singleton
public class HelloService {

    @Inject
    private EntityManagerFactory emf;

    // Inject the new DAO interface
    @Inject
    private HelloDAOImpl helloDAO;

    public String saveGreeting(String name) {
        String greeting = String.format("Hello, %s! Data saved via the Service and DAO Layers.", name != null ? name : "User");

        EntityManager em = null;

        try {
            // 1. Transaction Management (Service Responsibility)
            em = emf.createEntityManager();
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // 2. Business Logic
            HelloMessage msg = new HelloMessage(greeting);

            // 3. Delegation to Persistence (DAO Responsibility)
            helloDAO.save(em, msg); // Pass the EM to the DAO

            transaction.commit();

            return greeting;

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Service failed to save message via DAO.", e);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}