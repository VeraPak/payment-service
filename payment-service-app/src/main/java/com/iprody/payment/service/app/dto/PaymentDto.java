package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID guid;

    private UUID inquiryRefId;

    private BigDecimal amount;

    private String currency;

    private UUID transactionRefId;

    private PaymentStatus status;

    private String note;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
