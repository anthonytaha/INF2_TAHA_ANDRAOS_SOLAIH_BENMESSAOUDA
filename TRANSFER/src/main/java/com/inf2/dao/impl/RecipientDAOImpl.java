package com.inf2.dao.impl;

import com.inf2.dao.RecipientDAO;
import com.inf2.domain.RecipientStatus;
import com.inf2.domain.TransferRecipient;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
@Singleton
public class RecipientDAOImpl implements RecipientDAO {

    @Override
    public TransferRecipient save(EntityManager em, TransferRecipient recipient) {
        em.persist(recipient);
        return recipient;
    }

    @Override
    public TransferRecipient findById(EntityManager em, UUID id) {
        return em.find(TransferRecipient.class, id);
    }

    @Override
    public TransferRecipient findByAccountId(EntityManager em, UUID accountId) {
        List<TransferRecipient> results = em.createQuery(
                        "SELECT r FROM TransferRecipient r WHERE r.recipientAccountId = :accountId", TransferRecipient.class)
                .setParameter("accountId", accountId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<TransferRecipient> findByClientId(EntityManager em, UUID clientId) {
        return em.createQuery(
                        "SELECT r FROM TransferRecipient r WHERE r.clientId = :clientId " +
                                "ORDER BY r.createdAt DESC", TransferRecipient.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    @Override
    public List<TransferRecipient> findValidatedByClientId(EntityManager em, UUID clientId) {
        return em.createQuery(
                        "SELECT r FROM TransferRecipient r WHERE r.clientId = :clientId " +
                                "AND r.status = :status ORDER BY r.createdAt DESC", TransferRecipient.class)
                .setParameter("clientId", clientId)
                .setParameter("status", RecipientStatus.VALIDATED)
                .getResultList();
    }

    @Override
    public List<TransferRecipient> findByStatus(EntityManager em, RecipientStatus status) {
        return em.createQuery(
                        "SELECT r FROM TransferRecipient r WHERE r.status = :status " +
                                "ORDER BY r.createdAt DESC", TransferRecipient.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public TransferRecipient update(EntityManager em, TransferRecipient recipient) {
        return em.merge(recipient);
    }

    @Override
    public void delete(EntityManager em, UUID id) {
        TransferRecipient recipient = em.find(TransferRecipient.class, id);
        if (recipient != null) {
            em.remove(recipient);
        }
    }
}
