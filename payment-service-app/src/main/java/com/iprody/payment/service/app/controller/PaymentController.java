package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getAll() {
        return paymentService.search();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto findById(@PathVariable UUID id) {
        log.info("GET payment id {}", id);

        return paymentService.findById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> search(@ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /search page {} size {}", page, size);

        final Sort sort = switch (direction) {
            case "asc" -> Sort.by(sortBy).ascending();
            case "desc" -> Sort.by(sortBy).descending();
            default -> throw new IllegalArgumentException("Unexpected value : " + direction);
        };

        final Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody CreatePaymentDto dto) {
        return paymentService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return paymentService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateStatus(@PathVariable UUID id, @RequestBody PaymentStatusUpdateDto dto) {
        return paymentService.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateNote(@PathVariable UUID id, @RequestBody PaymentNoteUpdateDto dto) {
        return paymentService.updateNote(id, dto.getNote());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }
}