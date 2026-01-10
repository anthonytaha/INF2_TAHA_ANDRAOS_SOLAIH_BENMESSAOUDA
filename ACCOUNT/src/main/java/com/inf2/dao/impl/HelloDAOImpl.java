package com.inf2.dao.impl;

import com.inf2.dao.HelloDAO;
import com.inf2.domain.HelloMessage;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;

@Singleton
public class HelloDAOImpl implements HelloDAO {
    /**
     * Saves a HelloMessage entity to the database.
     * The transaction MUST be managed externally by the calling Service layer.
     * * @param em The EntityManager provided by the Service.
     * @param message The entity to save.
     * @return The persisted entity.
     */
    public HelloMessage save(EntityManager em, HelloMessage message) {
        em.persist(message);
        // The entity is now managed, and the return is optional for simple saves.
        return message;
    }
}
