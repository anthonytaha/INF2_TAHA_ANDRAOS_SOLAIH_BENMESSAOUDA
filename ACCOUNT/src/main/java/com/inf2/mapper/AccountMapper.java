package com.inf2.mapper;

import com.inf2.domain.Account;
import com.inf2.dto.AccountCreateRequest;
import jakarta.inject.Singleton;

@Singleton
public class AccountMapper {
    public Account toEntity(AccountCreateRequest accountCreateRequest) {
        if (accountCreateRequest == null) return null;
        // construc. solde à 0 et génère l'ID
        return new Account(
                accountCreateRequest.getClientId(),
                accountCreateRequest.getType(),
                accountCreateRequest.getCurrency()
        );
    }
}
