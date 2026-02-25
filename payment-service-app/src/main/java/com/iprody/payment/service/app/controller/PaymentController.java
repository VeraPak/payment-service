package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{guid}")
    public PaymentDto findById(@PathVariable UUID guid) {
        return paymentService.findById(guid);
    }

    @GetMapping()
    public List<PaymentDto> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/search")
    public Page<PaymentDto> search(@ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "guid") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        System.out.println(filter);
        final Sort sort = switch (direction) {
            case "asc" -> Sort.by(sortBy).ascending();
            case "desc" -> Sort.by(sortBy).descending();
            default -> throw new IllegalArgumentException("Unexpected value : " + direction);
        };

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }

    @PostMapping
    public PaymentDto create(@RequestBody CreatePaymentDto dto) {
        return paymentService.create(dto);
    }
}