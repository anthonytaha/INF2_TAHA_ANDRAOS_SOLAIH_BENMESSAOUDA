package com.inf2.dao;

import com.inf2.domain.Account;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public interface AccountDAO {
    Account save(EntityManager em, Account account);
    Account findById(EntityManager em, UUID id);
    List<Account> findByClientId(EntityManager em, UUID clientId);
    Account update(EntityManager em, Account account);
}
