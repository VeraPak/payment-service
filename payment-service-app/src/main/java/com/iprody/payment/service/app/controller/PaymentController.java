package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import com.iprody.payment.service.app.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentRepository.findById(guid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found"
                ));
    }

    @GetMapping()
    public List<Payment> getPayment() {
        return new ArrayList<>(paymentRepository.findAll());
    }
}
