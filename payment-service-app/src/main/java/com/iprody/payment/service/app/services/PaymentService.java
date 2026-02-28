package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<PaymentDto> search() {
        return paymentRepository.findAll().stream()
            .map(paymentMapper::toPaymentDto).toList();
    }

    public PaymentDto findById(UUID id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toPaymentDto)
            .orElseThrow(() -> new EntityNotFoundException("Payment with id " + id + " not found"));
    }

    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter), pageable)
            .map(paymentMapper::toPaymentDto);
    }

    public PaymentDto create(CreatePaymentDto dto) {
        return paymentMapper.toPaymentDto(paymentRepository.save(paymentMapper.fromCreatePaymentDto(dto)));
    }

    @Transactional
    public PaymentDto update(UUID id, PaymentDto dto) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment with id " + id + " not found"));

        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setStatus(dto.getStatus());
        payment.setNote(dto.getNote());
        payment.setTransactionRefId(dto.getTransactionRefId());
        payment.setInquiryRefId(dto.getInquiryRefId());

        final Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(updatedPayment);

    }

    public void delete(UUID id) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment with id " + id + " not found"));
        paymentRepository.delete(payment);
    }

    @Transactional
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment with id " + id + " not found"));

        payment.setStatus(status);

        final Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }

    @Transactional
    public PaymentDto updateNote(UUID id, String note) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment with id " + id + " not found"));

        payment.setNote(note);

        final Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }

}