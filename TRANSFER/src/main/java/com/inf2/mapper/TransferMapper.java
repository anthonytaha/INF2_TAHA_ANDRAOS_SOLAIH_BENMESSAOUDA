package com.inf2.mapper;

import com.inf2.domain.Transfer;
import com.inf2.dto.TransferCreateRequest;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class TransferMapper {

    public Transfer toEntity(TransferCreateRequest request, UUID destinationAccountId, UUID recipientId) {
        if (request == null) return null;

        return new Transfer(
                request.getSourceAccountId(),
                destinationAccountId,
                recipientId,
                request.getAmount(),
                request.getDescription()
        );
    }
}
