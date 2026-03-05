package com.iprody.payment.service.app.exception.errorhandle;

import java.time.Instant;
import java.util.UUID;

public class ErrorResponseDto {
    private final String errorMessage;
    private final Instant timestamp;
    private final OperationType operation;
    private final UUID entityId;

    public ErrorResponseDto(String errorMessage, OperationType operation, UUID entityId) {
        this.errorMessage = errorMessage;
        this.timestamp = Instant.now();
        this.operation = operation;
        this.entityId = entityId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OperationType getOperation() {
        return operation;
    }

    public UUID getEntityId() {
        return entityId;
    }
}