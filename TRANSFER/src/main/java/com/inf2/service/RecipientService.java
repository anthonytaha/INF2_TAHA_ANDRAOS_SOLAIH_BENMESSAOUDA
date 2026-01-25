package com.inf2.service;


import com.inf2.dao.RecipientDAO;
import com.inf2.domain.RecipientStatus;
import com.inf2.domain.TransferRecipient;
import com.inf2.dto.RecipientCreateRequest;
import com.inf2.mapper.RecipientMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.UUID;

@Singleton
public class RecipientService {

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private RecipientDAO recipientDAO;

    @Inject
    private RecipientMapper recipientMapper;

    public TransferRecipient registerRecipient(RecipientCreateRequest request, UUID clientId) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Vérifier si l'IBAN existe déjà
            TransferRecipient existing = recipientDAO.findByAccountId(em, request.getRecipientAccountId());
            if (existing != null) {
                throw new RuntimeException("Un destinataire avec cet IBAN existe déjà");
            }

            TransferRecipient recipient = recipientMapper.toEntity(request, clientId);
            recipientDAO.save(em, recipient);

            tx.commit();
            return recipient;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Échec enregistrement destinataire", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public TransferRecipient findById(UUID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return recipientDAO.findById(em, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<TransferRecipient> findByClientId(UUID clientId) {
        EntityManager em = emf.createEntityManager();
        try {
            return recipientDAO.findByClientId(em, clientId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<TransferRecipient> findValidatedByClientId(UUID clientId) {
        EntityManager em = emf.createEntityManager();
        try {
            return recipientDAO.findValidatedByClientId(em, clientId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<TransferRecipient> findPendingRecipients() {
        EntityManager em = emf.createEntityManager();
        try {
            return recipientDAO.findByStatus(em, RecipientStatus.PENDING_VALIDATION);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void validateRecipient(UUID recipientId, UUID advisorId) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            TransferRecipient recipient = recipientDAO.findById(em, recipientId);
            if (recipient == null) {
                throw new RuntimeException("Destinataire introuvable");
            }

            if (!recipient.isPendingValidation()) {
                throw new RuntimeException("Le destinataire n'est pas en attente de validation");
            }

            recipient.validate(advisorId);
            recipientDAO.update(em, recipient);

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Échec validation destinataire", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public void rejectRecipient(UUID recipientId, UUID advisorId) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            TransferRecipient recipient = recipientDAO.findById(em, recipientId);
            if (recipient == null) {
                throw new RuntimeException("Destinataire introuvable");
            }

            if (!recipient.isPendingValidation()) {
                throw new RuntimeException("Le destinataire n'est pas en attente de validation");
            }

            recipient.reject(advisorId);
            recipientDAO.update(em, recipient);

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Échec rejet destinataire", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public void deleteRecipient(UUID id, UUID clientId) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            TransferRecipient recipient = recipientDAO.findById(em, id);
            if (recipient == null) {
                throw new RuntimeException("Destinataire introuvable");
            }

            if (!recipient.getClientId().equals(clientId)) {
                throw new RuntimeException("Destinataire n'appartient pas à ce client");
            }

            if (recipient.isValidated()) {
                throw new RuntimeException("Impossible de supprimer un destinataire validé");
            }

            recipientDAO.delete(em, id);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
}
