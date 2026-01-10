package com.inf2.service;

import com.inf2.dao.RecipientDAO;
import com.inf2.dao.TransferDAO;
import com.inf2.dao.impl.RecipientDAOImpl;
import com.inf2.dao.impl.TransferDAOImpl;
import com.inf2.domain.Transfer;
import com.inf2.domain.TransferRecipient;
import com.inf2.domain.TransferStatus;
import com.inf2.dto.transfer.TransferCreateRequest;
import com.inf2.mapper.TransferMapper;
import com.inf2.messaging.TransferMessageProducer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Singleton
public class TransferService {

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private TransferDAO transferDAO;
    @Inject
    private RecipientDAO recipientDAO;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private TransferMessageProducer messageProducer;

    public Transfer createTransfer(TransferCreateRequest request, UUID clientId) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Valider que le destinataire existe et est validé
            TransferRecipient recipient = recipientDAO.findById(em, request.getRecipientId());
            if (recipient == null) {
                throw new RuntimeException("Destinataire introuvable");
            }
            if (!recipient.isValidated()) {
                throw new RuntimeException("Destinataire non validé");
            }
            if (!recipient.getClientId().equals(clientId)) {
                throw new RuntimeException("Destinataire n'appartient pas à ce client");
            }

            // On suppose que destinationAccountId vient de l'IBAN du recipient
            // Dans un vrai système, il faudrait résoudre l'IBAN vers un accountId
            UUID destinationAccountId = recipient.getRecipientAccountId(); // Simplifié

            // Créer le transfer
            Transfer transfer = transferMapper.toEntity(request, destinationAccountId, recipient.getId());
            transferDAO.save(em, transfer);

            tx.commit();

            // Envoyer le message JMS (hors transaction)
            try {
                messageProducer.sendTransferCommand(
                        transfer.getCorrelationId(),
                        transfer.getSourceAccountId(),
                        transfer.getDestinationAccountId(),
                        transfer.getAmount()
                );
            } catch (Exception e) {
                // Si l'envoi JMS échoue, marquer le transfer comme failed
                updateTransferStatus(transfer.getId(), TransferStatus.FAILED,
                        "Échec envoi commande: " + e.getMessage());
            }

            return transfer;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Échec création transfer", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Transfer findById(UUID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findById(em, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Transfer findByCorrelationId(UUID correlationId) {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findByCorrelationId(em, correlationId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Transfer> findByAccountId(UUID accountId) {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findByAccountId(em, accountId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Transfer> findSentTransfers(UUID sourceAccountId) {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findBySourceAccountId(em, sourceAccountId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Transfer> findReceivedTransfers(UUID destinationAccountId) {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findByDestinationAccountId(em, destinationAccountId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Transfer> findPendingTransfers() {
        EntityManager em = emf.createEntityManager();
        try {
            return transferDAO.findByStatus(em, TransferStatus.PENDING);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void updateTransferStatus(UUID id, TransferStatus status, String failureReason) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Transfer transfer = transferDAO.findById(em, id);
            if (transfer == null) {
                throw new RuntimeException("Transfer introuvable : " + id);
            }

            if (status == TransferStatus.COMPLETED) {
                transfer.markAsCompleted();
            } else if (status == TransferStatus.FAILED) {
                transfer.markAsFailed(failureReason);
            }

            transferDAO.update(em, transfer);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Échec mise à jour status transfer", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public void cancelTransfer(UUID id) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Transfer transfer = transferDAO.findById(em, id);
            if (transfer == null) {
                throw new RuntimeException("Transfer introuvable");
            }

            if (!transfer.isPending()) {
                throw new RuntimeException("Seuls les transfers en attente peuvent être annulés");
            }

            transfer.markAsFailed("Annulé par l'utilisateur");
            transferDAO.update(em, transfer);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
}