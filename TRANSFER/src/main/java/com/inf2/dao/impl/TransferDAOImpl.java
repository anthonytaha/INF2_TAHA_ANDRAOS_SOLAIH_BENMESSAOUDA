package com.inf2.dao.impl;

import com.inf2.dao.TransferDAO;

import com.inf2.dao.TransferDAO;
import com.inf2.domain.Transfer;
import com.inf2.domain.TransferStatus;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
@Singleton
public class TransferDAOImpl implements TransferDAO {


    @Override
    public Transfer save(EntityManager em, Transfer transfer) {
        em.persist(transfer);
        return transfer;
    }

    @Override
    public Transfer findById(EntityManager em, UUID id) {
        return em.find(Transfer.class, id);
    }

    @Override
    public Transfer findByCorrelationId(EntityManager em, UUID correlationId) {
        List<Transfer> results = em.createQuery(
                        "SELECT t FROM Transfer t WHERE t.correlationId = :correlationId", Transfer.class)
                .setParameter("correlationId", correlationId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Transfer> findByAccountId(EntityManager em, UUID accountId) {
        return em.createQuery(
                        "SELECT t FROM Transfer t WHERE t.sourceAccountId = :accountId " +
                                "OR t.destinationAccountId = :accountId ORDER BY t.createdAt DESC", Transfer.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Transfer> findBySourceAccountId(EntityManager em, UUID sourceAccountId) {
        return em.createQuery(
                        "SELECT t FROM Transfer t WHERE t.sourceAccountId = :sourceAccountId " +
                                "ORDER BY t.createdAt DESC", Transfer.class)
                .setParameter("sourceAccountId", sourceAccountId)
                .getResultList();
    }

    @Override
    public List<Transfer> findByDestinationAccountId(EntityManager em, UUID destinationAccountId) {
        return em.createQuery(
                        "SELECT t FROM Transfer t WHERE t.destinationAccountId = :destinationAccountId " +
                                "ORDER BY t.createdAt DESC", Transfer.class)
                .setParameter("destinationAccountId", destinationAccountId)
                .getResultList();
    }

    @Override
    public List<Transfer> findByStatus(EntityManager em, TransferStatus status) {
        return em.createQuery(
                        "SELECT t FROM Transfer t WHERE t.status = :status ORDER BY t.createdAt DESC", Transfer.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Transfer update(EntityManager em, Transfer transfer) {
        return em.merge(transfer);
    }
}