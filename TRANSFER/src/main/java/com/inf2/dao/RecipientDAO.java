package com.inf2.dao;


import com.inf2.domain.RecipientStatus;
import com.inf2.domain.TransferRecipient;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public interface RecipientDAO {
    TransferRecipient save(EntityManager em, TransferRecipient recipient);
    TransferRecipient findById(EntityManager em, UUID id);
    TransferRecipient findByAccountId(EntityManager em, UUID accountId);
    List<TransferRecipient> findByClientId(EntityManager em, UUID clientId);
    List<TransferRecipient> findValidatedByClientId(EntityManager em, UUID clientId);
    List<TransferRecipient> findByStatus(EntityManager em, RecipientStatus status);
    TransferRecipient update(EntityManager em, TransferRecipient recipient);
    void delete(EntityManager em, UUID id);
}