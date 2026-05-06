package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.async.MessageHandler;
import com.iprody.payment.service.app.async.XPaymentAdapterResponseMessage;
import com.iprody.payment.service.app.async.XPaymentAdapterStatus;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.exception.errorhandle.OperationType;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class XPaymentAdapterMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentRepository repository;

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("Received message with id {} and status {}", message.getMessageId(), message.getStatus());

        final Payment payment = repository.findById(message.getPaymentGuid())
            .orElseThrow(() -> new EntityNotFoundException(
            "Платеж не найден",
            OperationType.FIND_BY_ID,
            message.getPaymentGuid()
        ));

        payment.setAmount(message.getAmount());
        payment.setCurrency(message.getCurrency());
        payment.setTransactionRefId(message.getTransactionRefId());
        payment.setStatus(mapStatus(message.getStatus()));

        repository.save(payment);
    }

    private PaymentStatus mapStatus(XPaymentAdapterStatus status) {
        return switch (status) {
            case PROCESSING -> PaymentStatus.PENDING;
            case CANCELED -> PaymentStatus.DECLINED;
            case SUCCEEDED -> PaymentStatus.APPROVED;
        };
    }
}
