package com.inf2.dao.impl;

import com.inf2.dao.HelloDAO;
import com.inf2.domain.HelloMessage;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@Singleton
public class HelloDAOImpl implements HelloDAO {
    /**
     * Saves a HelloMessage entity to the database.
     * The transaction MUST be managed externally by the calling Service layer.
     * * @param em The EntityManager provided by the Service.
     * @param message The entity to save.
     * @return The persisted entity.
     */
    @Inject
    private Provider<EntityManager> emProvider;
    public HelloMessage save(HelloMessage message) {
        // 2. Get the EntityManager for this specific request
        EntityManager em = emProvider.get();
        EntityTransaction transaction = em.getTransaction();

        try {
            // 3. Transaction Management
            // We still handle boundaries because we aren't using a full JTA container
            transaction.begin();

            em.persist(message);
            transaction.commit();
            // The entity is now managed, and the return is optional for simple saves.
            return message;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Service failed to save message via DAO.", e);
        }

    }
}
