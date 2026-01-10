package com.inf2.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer_recipients")
public class TransferRecipient {

    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_id", nullable = false, length = 36)
    private UUID clientId;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false)
    private UUID recipientAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientStatus status;

    @Column(name = "validated_by", length = 36)
    private UUID validatedBy; // ID de l'advisor qui a validé

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructeur par défaut
    public TransferRecipient() {
        this.createdAt = LocalDateTime.now();
        this.status = RecipientStatus.PENDING_VALIDATION;
    }

    // Constructeur avec paramètres
    public TransferRecipient(UUID clientId, String recipientName, UUID recipientAccountId) {
        this();
        this.clientId = clientId;
        this.recipientName = recipientName;
        this.recipientAccountId = recipientAccountId;
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public UUID getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(UUID recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public RecipientStatus getStatus() {
        return status;
    }

    public void setStatus(RecipientStatus status) {
        this.status = status;
    }

    public UUID getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(UUID validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // Méthodes business
    public void validate(UUID advisorId) {
        this.status = RecipientStatus.VALIDATED;
        this.validatedBy = advisorId;
        this.validatedAt = LocalDateTime.now();
    }

    public void reject(UUID advisorId) {
        this.status = RecipientStatus.REJECTED;
        this.validatedBy = advisorId;
    }

    public boolean isValidated() {
        return this.status == RecipientStatus.VALIDATED;
    }

    public boolean isPendingValidation() {
        return this.status == RecipientStatus.PENDING_VALIDATION;
    }
}
