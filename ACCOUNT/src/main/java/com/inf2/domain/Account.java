package com.inf2.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    // constructeur vide
    public Account() {
    }

    // constructeur
    public Account(UUID clientId, AccountType type, String currency) {
        this.clientId = clientId;
        this.type = type;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.active;
    }

    // getters
    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public AccountType getType() { return type; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public AccountStatus getStatus() { return status; }

    // setters

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    // Méthodes (debit et credit)
    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}