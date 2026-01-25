package com.inf2.dto;

import com.inf2.domain.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountCreateRequest {
    private UUID clientId;
    private AccountType type;
    private String currency;
    private BigDecimal balance;

    public AccountCreateRequest() {}

    public UUID getClientId() {
        return clientId;
    }
    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
