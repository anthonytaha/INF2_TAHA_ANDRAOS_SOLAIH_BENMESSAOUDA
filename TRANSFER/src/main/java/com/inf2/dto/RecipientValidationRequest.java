package com.inf2.dto;

import java.util.UUID;

public class RecipientValidationRequest {
    private UUID advisorId;

    public RecipientValidationRequest() {}

    public UUID getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(UUID advisorId) {
        this.advisorId = advisorId;
    }

}
