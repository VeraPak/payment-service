package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.async.AsyncSender;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.exception.errorhandle.OperationType;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.mapper.XPaymentAdapterMapper;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
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
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;

    public PaymentService(PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        AsyncSender<XPaymentAdapterRequestMessage> sender,
        XPaymentAdapterMapper xPaymentAdapterMapper) {

        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.sender = sender;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
    }

    public List<PaymentDto> search() {
        return paymentRepository.findAll().stream()
            .map(paymentMapper::toPaymentDto)
            .toList();
    }

    public PaymentDto findById(UUID id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toPaymentDto)
            .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", OperationType.FIND_BY_ID, id));
    }

    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter), pageable)
            .map(paymentMapper::toPaymentDto);
    }

    public PaymentDto create(CreatePaymentDto dto) {
        final Payment payment = paymentMapper.fromCreatePaymentDto(dto);
        final PaymentDto savedDto = paymentMapper.toPaymentDto(paymentRepository.save(payment));

        sender.send(xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(payment));

        return savedDto;
    }

    @Transactional
    public PaymentDto update(UUID id, PaymentDto dto) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", OperationType.UPDATE, id));

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
            .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", OperationType.DELETE, id));

        paymentRepository.delete(payment);
    }

    @Transactional
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", OperationType.UPDATE_STATUS, id));

        payment.setStatus(status);

        final Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }

    @Transactional
    public PaymentDto updateNote(UUID id, String note) {
        final Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", OperationType.UPDATE_NOTE, id));

        payment.setNote(note);

        final Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }
}