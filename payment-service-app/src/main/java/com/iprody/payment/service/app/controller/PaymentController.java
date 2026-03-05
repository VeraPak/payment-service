package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.exception.errorhandle.ErrorResponseDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    @GetMapping()
    public List<PaymentDto> getAll() {
        return paymentService.search();
    }

    @GetMapping("/{id}")
    public PaymentDto findById(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    @GetMapping("/search")
    public Page<PaymentDto> search(@ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "id") String sortBy,
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentDto create(@RequestBody CreatePaymentDto dto) {
        return paymentService.create(dto);
    }

    @PutMapping("/{id}")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return paymentService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public PaymentDto updateStatus(@PathVariable UUID id, @RequestBody @Valid PaymentStatusUpdateDto dto) {
        return paymentService.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    public PaymentDto updateNote(@PathVariable UUID id, @RequestBody @Valid PaymentNoteUpdateDto dto) {
        return paymentService.updateNote(id, dto.getNote());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponseDto(
            ex.getMessage(),
            ex.getOperation(),
            ex.getEntityId()
        );
    }
}