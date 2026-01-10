package com.inf2.service;

import com.inf2.dao.AccountDAO;
import com.inf2.domain.Account;
import com.inf2.dto.AccountCreateRequest;
import com.inf2.dto.AccountUpdateRequest;
import com.inf2.mapper.AccountMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.UUID;

@Singleton
public class AccountService {

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private AccountDAO accountDAO;

    @Inject
    private AccountMapper accountMapper;

    public Account createAccount(AccountCreateRequest accountCreateRequest) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Account account = accountMapper.toEntity(accountCreateRequest);
            accountDAO.save(em, account);
            tx.commit();
            return account;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Echec création compte", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Account> findByClientId(UUID clientId) {
        EntityManager em = emf.createEntityManager();
        try {
            return accountDAO.findByClientId(em, clientId);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Account findById(UUID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return accountDAO.findById(em, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void updateAccount(UUID id, AccountUpdateRequest accountUpdateRequest) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            Account account = accountDAO.findById(em, id);

            if (account != null) {
                if (accountUpdateRequest.getType() != null) account.setType(accountUpdateRequest.getType());
                if (accountUpdateRequest.getStatus() != null) account.setStatus(accountUpdateRequest.getStatus());

                accountDAO.update(em, account);
                tx.commit();
            } else {
                throw new RuntimeException("Compte introuvable : " + id);
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void updateBalance(UUID id, java.math.BigDecimal amount, boolean isCredit) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            Account account = accountDAO.findById(em, id);

            if (account != null) {
                if (isCredit) {
                    account.credit(amount);
                } else {
                    account.debit(amount);
                }
                accountDAO.update(em, account);
                tx.commit();
            } else {
                throw new RuntimeException("Compte introuvable pour mise à jour du solde");
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    public void transferMoney(UUID fromAccountId, UUID toAccountId, java.math.BigDecimal amount) {
        EntityManager em = null;
        try {
            // 1. Start a new Transaction
            em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            // 2. Load both accounts using the SAME EntityManager
            // This ensures they are part of the same transaction context.
            Account sourceAccount = accountDAO.findById(em, fromAccountId);
            Account destAccount = accountDAO.findById(em, toAccountId);

            // 3. Validation
            if (sourceAccount == null) {
                throw new IllegalArgumentException("Source account not found: " + fromAccountId);
            }
            if (destAccount == null) {
                throw new IllegalArgumentException("Destination account not found: " + toAccountId);
            }
            if (sourceAccount.getId().equals(destAccount.getId())) {
                throw new IllegalArgumentException("Cannot transfer money to the same account.");
            }

            // 4. Business Logic (Check balance and update in-memory state)
            // Note: verify your Account entity has a 'getBalance()' method
            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds in source account");
            }

            // Use your existing helper methods
            sourceAccount.debit(amount);
            destAccount.credit(amount);

            // 5. Persist Changes
            accountDAO.update(em, sourceAccount);
            accountDAO.update(em, destAccount);

            // 6. Commit the Transaction (Atomic save)
            tx.commit();

        } catch (Exception e) {
            // 7. Rollback on ANY failure
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Re-throw to alert the caller (e.g., the JMS Listener)
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        } finally {
            // 8. Cleanup
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}