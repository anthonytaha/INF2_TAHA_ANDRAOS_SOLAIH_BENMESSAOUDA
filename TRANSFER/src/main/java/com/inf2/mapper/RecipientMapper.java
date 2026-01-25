package com.inf2.mapper;


import com.inf2.domain.TransferRecipient;
import com.inf2.dto.RecipientCreateRequest;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class RecipientMapper {

    public TransferRecipient toEntity(RecipientCreateRequest request, UUID clientId) {
        if (request == null) return null;

        TransferRecipient recipient = new TransferRecipient(
                clientId,
                request.getRecipientName(),
                request.getRecipientAccountId()
        );

        return recipient;
    }
}
