package com.inf2.dto;

import java.util.UUID;

public class RecipientCreateRequest {
    private String recipientName;
    private UUID recipientAccountId;

    public RecipientCreateRequest() {}

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
}
