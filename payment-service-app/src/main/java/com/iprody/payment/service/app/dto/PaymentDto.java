package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDto {

    private UUID id;

    private UUID inquiryRefId;

    private BigDecimal amount;

    private String currency;

    private UUID transactionRefId;

    private PaymentStatus status;

    private String note;

    private Instant createdAt;

    private Instant updatedAt;
}
