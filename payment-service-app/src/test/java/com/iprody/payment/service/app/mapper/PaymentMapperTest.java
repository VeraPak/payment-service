package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMapperTest {

    PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapToDto(){
        // given
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency("USD");
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(Instant.parse("2025-01-15T10:00:00Z"));
        payment.setUpdatedAt(Instant.parse("2025-01-15T10:00:00Z"));

        // when
        PaymentDto dto = paymentMapper.toPaymentDto(payment);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(payment.getId());
        assertThat(dto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(dto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(dto.getInquiryRefId()).isEqualTo(payment.getInquiryRefId());

        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(dto.getCreatedAt()).isEqualTo(payment.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt());
    }

    @Test
    void shouldMapToEntity(){
        // given
        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setTransactionRefId(UUID.randomUUID());
        dto.setCurrency("EUR");
        dto.setAmount(new BigDecimal("999.99"));
        dto.setStatus(PaymentStatus.PENDING);
        dto.setNote("ref456");

        // when
        Payment payment = paymentMapper.fromCreatePaymentDto(dto);

        // then
        assertThat(payment).isNotNull();
        assertThat(payment.getInquiryRefId()).isEqualTo(dto.getInquiryRefId());
        assertThat(payment.getTransactionRefId()).isEqualTo(dto.getTransactionRefId());
        assertThat(payment.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(payment.getAmount()).isEqualTo(dto.getAmount());
        assertThat(payment.getStatus()).isEqualTo(dto.getStatus());
        assertThat(payment.getNote()).isEqualTo(dto.getNote());
    }
}
