package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Map<Long, Payment> payments = new HashMap<>();

    @PostConstruct
    public void init() {
        payments.put(1L, new Payment(1L, 1000));
        payments.put(2L, new Payment(2L, 2500));
        payments.put(3L, new Payment(3L, 500));
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable long id) {
        return payments.get(id);
    }

    @GetMapping()
    public List<Payment> getPayment() {
        return new ArrayList<>(payments.values());
    }

}
