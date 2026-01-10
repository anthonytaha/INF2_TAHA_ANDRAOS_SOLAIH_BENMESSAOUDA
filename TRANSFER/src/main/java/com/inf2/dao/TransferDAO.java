package com.inf2.dao;

import com.inf2.domain.Transfer;
import com.inf2.domain.TransferStatus;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public interface TransferDAO {
    Transfer save(EntityManager em, Transfer transfer);
    Transfer findById(EntityManager em, UUID id);
    Transfer findByCorrelationId(EntityManager em, UUID correlationId);
    List<Transfer> findByAccountId(EntityManager em, UUID accountId);
    List<Transfer> findBySourceAccountId(EntityManager em, UUID sourceAccountId);
    List<Transfer> findByDestinationAccountId(EntityManager em, UUID destinationAccountId);
    List<Transfer> findByStatus(EntityManager em, TransferStatus status);
    Transfer update(EntityManager em, Transfer transfer);
}
