package com.inf2.domain;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private UUID id;

    @Column(name = "source_account_id", nullable = false, length = 36)
    private UUID sourceAccountId;

    @Column(name = "destination_account_id", nullable = false, length = 36)
    private UUID destinationAccountId;

    @Column(name = "recipient_id", nullable = false, length = 36)
    private UUID recipientId;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "correlation_id", length = 36)
    private UUID correlationId;

    @Column(name = "execution_date")
    private LocalDateTime executionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String description;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    // Constructeur par défaut
    public Transfer() {
        this.createdAt = LocalDateTime.now();
        this.status = TransferStatus.PENDING;
        this.correlationId = UUID.randomUUID();
    }

    // Constructeur avec paramètres
    public Transfer(UUID sourceAccountId, UUID destinationAccountId,
                    UUID recipientId, BigDecimal amount, String description) {
        this();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.description = description;
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    // Méthodes business
    public void markAsCompleted() {
        this.status = TransferStatus.COMPLETED;
        this.executionDate = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = TransferStatus.FAILED;
        this.failureReason = reason;
    }

    public boolean isPending() {
        return this.status == TransferStatus.PENDING;
    }
}
