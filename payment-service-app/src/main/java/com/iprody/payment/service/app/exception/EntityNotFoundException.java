package com.iprody.payment.service.app.exception;

import com.iprody.payment.service.app.exception.errorhandle.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    private final OperationType operation;
    private final UUID entityId;

    public EntityNotFoundException(String message, OperationType operation, UUID entityId) {
        super(message);
        this.operation = operation;
        this.entityId = entityId;
    }

    public OperationType getOperation() {
        return operation;
    }

    public UUID getEntityId() {
        return entityId;
    }
}