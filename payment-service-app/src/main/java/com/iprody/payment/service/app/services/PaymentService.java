package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment findById(UUID guid) {
        return paymentRepository.findById(guid).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Payment not found"
        ));
    }

    public List<Payment> findAll() {
        return this.paymentRepository.findAll();
    }

    @GetMapping
    public List<Payment> search(PaymentFilter filter) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository.findAll(spec);
    }

    @GetMapping
    public Page<Payment> searchPaged(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository.findAll(spec, pageable);
    }

}