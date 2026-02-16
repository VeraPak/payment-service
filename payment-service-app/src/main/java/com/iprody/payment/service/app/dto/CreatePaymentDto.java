package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDto {

    private UUID inquiryRefId;

    private BigDecimal amount;

    private String currency;

    private UUID transactionRefId;

    private PaymentStatus status;

    private String note;
}
