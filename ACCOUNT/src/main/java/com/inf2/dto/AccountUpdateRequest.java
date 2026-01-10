package com.inf2.dto;

import com.inf2.domain.AccountStatus;
import com.inf2.domain.AccountType;

public class AccountUpdateRequest {
    private AccountType type;
    private AccountStatus status;

    public AccountUpdateRequest() {}

    // getters et setters
    public AccountType getType() {
        return type;
    }
    public void setType(AccountType type) {
        this.type = type;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
