package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public Optional<PaymentDto> findById(UUID guid) {
        return paymentRepository.findById(guid)
            .map(paymentMapper::toPaymentDto);
    }

    public List<PaymentDto> search(PaymentFilter filter) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter)).stream()
            .map(paymentMapper::toPaymentDto).toList();
    }

    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter), pageable)
            .map(paymentMapper::toPaymentDto);
    }

    public PaymentDto create(CreatePaymentDto dto) {
        return paymentMapper.toPaymentDto(paymentRepository.save(paymentMapper.fromCreatePaymentDto(dto)));
    }

}