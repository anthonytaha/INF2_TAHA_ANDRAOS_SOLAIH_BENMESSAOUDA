package com.inf2.dao.impl;

import com.inf2.dao.AccountDAO;
import com.inf2.domain.Account;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public Account save(EntityManager em, Account account) {
        em.persist(account);
        return account;
    }

    @Override
    public Account findById(EntityManager em, UUID id) {
        return em.find(Account.class, id);

    }

    @Override
    public List<Account> findByClientId(EntityManager em, UUID clientId) {
        return em.createQuery("SELECT a FROM Account a WHERE a.clientId = :clientId", Account.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    @Override
    public Account update(EntityManager em, Account account) {
        return em.merge(account);
    }
}