package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentDto findById(UUID guid) {
        return paymentRepository.findById(guid)
            .map(paymentMapper::toPaymentDto).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Payment not found"
            ));
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(paymentMapper::toPaymentDto).toList();
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