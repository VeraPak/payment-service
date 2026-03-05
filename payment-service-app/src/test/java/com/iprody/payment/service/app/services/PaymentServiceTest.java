package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private UUID id;
    private Payment payment;
    private PaymentDto paymentDto;

    static PaymentStatus[] statusProvider() {
        return new PaymentStatus[] {
            PaymentStatus.RECEIVED,
            PaymentStatus.PENDING,
            PaymentStatus.APPROVED,
            PaymentStatus.DECLINED,
            PaymentStatus.NOT_SENT
        };
    }

    static Stream<PaymentFilter> filterProvider() {
        PaymentFilter byCurrency = new PaymentFilter("USD", null, null, null, null, null);
        PaymentFilter byMinAmount = new PaymentFilter(null, new BigDecimal("100"), null, null, null, null);
        PaymentFilter byMaxAmount = new PaymentFilter(null, null, new BigDecimal("500"), null, null, null);
        PaymentFilter afterDate = new PaymentFilter(null, null, null, Instant.parse("2025-01-14T10:00:00Z"), null, null);
        PaymentFilter beforeDate = new PaymentFilter(null, null, null, null, Instant.parse("2025-01-15T10:00:00Z"), null);
        PaymentFilter byStatus = new PaymentFilter(null, null, null, null, null, PaymentStatus.APPROVED);
        return Stream.of(byCurrency, byMinAmount, byMaxAmount, afterDate, beforeDate, byStatus);
    }

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        payment = new Payment();
        payment.setId(id);
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(Instant.parse("2025-01-15T10:00:00Z"));
        payment.setUpdatedAt(Instant.parse("2025-01-15T10:00:00Z"));

        paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setInquiryRefId(payment.getInquiryRefId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setTransactionRefId(payment.getTransactionRefId());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setNote(payment.getNote());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());
    }

    @Test
    void findById_whenPaymentExists() {
        // given
        when(paymentRepository.findById(id)).thenReturn(Optional.ofNullable(payment));
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto mappedPaymentDto = paymentService.findById(id);

        // then
        assertThat(mappedPaymentDto).isEqualTo(paymentDto);

        verify(paymentRepository).findById(id);
        verify(paymentMapper).toPaymentDto(payment);
    }

    @Test
    void findById_whenPaymentMissing() {
        // given
        UUID id = UUID.randomUUID();
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> paymentService.findById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден");

        verify(paymentRepository).findById(id);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        // given
        payment.setStatus(status);
        paymentDto.setStatus(status);

        when(paymentRepository.findById(id)).thenReturn(Optional.ofNullable(payment));
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto mappedPaymentDto = paymentService.findById(id);

        // then
        assertThat(mappedPaymentDto.getStatus()).isEqualTo(status);

        verify(paymentRepository).findById(id);
        verify(paymentMapper).toPaymentDto(payment);
    }

    @ParameterizedTest
    @MethodSource("filterProvider")
    void searchPaged_shouldReturnCorrectPageData(PaymentFilter filter) {
        // given
        Pageable pageable = PageRequest.of(0, 25, Sort.by("amount").ascending());
        Page<Payment> page = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        Page<PaymentDto> result = paymentService.searchPaged(filter, pageable);

        // then
        assertThat(result.getContent()).containsExactly(paymentDto);

        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
        verify(paymentMapper).toPaymentDto(payment);
    }

    @Test
    void searchPagedTest() {
        // given
        PaymentFilter filter = new PaymentFilter(null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> page = new PageImpl<>(List.of(payment), pageable, 1);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        Page<PaymentDto> result = paymentService.searchPaged(filter, pageable);

        // then
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(25);

        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    void create_shouldReturnCreatedPayment() {
        // given
        CreatePaymentDto createDto = new CreatePaymentDto();
        createDto.setInquiryRefId(payment.getInquiryRefId());
        createDto.setAmount(payment.getAmount());
        createDto.setCurrency(payment.getCurrency());
        createDto.setTransactionRefId(payment.getTransactionRefId());
        createDto.setStatus(PaymentStatus.APPROVED);
        createDto.setNote(payment.getNote());

        when(paymentMapper.fromCreatePaymentDto(createDto)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto resultDto = paymentService.create(createDto);

        // then
        assertThat(resultDto).isEqualTo(payment);

        verify(paymentMapper).fromCreatePaymentDto(createDto);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentDto(payment);
    }

    /// ///////////

    @Test
    void update_shouldUpdateExistingPayment() {
        // given
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.update(id, paymentDto);

        // then
        assertThat(result).isEqualTo(paymentDto);
        assertThat(payment.getAmount()).isEqualTo(paymentDto.getAmount());
        assertThat(payment.getNote()).isEqualTo(paymentDto.getNote());
        assertThat(payment.getStatus()).isEqualTo(paymentDto.getStatus());

        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentDto(payment);
    }

    @Test
    void delete_shouldRemovePayment() {
        // given
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        // when
        paymentService.delete(id);

        // then
        verify(paymentRepository).findById(id);
        verify(paymentRepository).delete(payment);
    }

    @Test
    void delete_whenPaymentMissing_shouldThrowException() {
        // given
        UUID missingId = UUID.randomUUID();
        when(paymentRepository.findById(missingId)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> paymentService.delete(missingId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден");

        verify(paymentRepository).findById(missingId);
        verify(paymentRepository, never()).delete(any(Payment.class));
    }

    @Test
    void updateStatus_shouldChangePaymentStatus() {
        // given
        PaymentStatus newStatus = PaymentStatus.RECEIVED;
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.updateStatus(id, newStatus);

        // then
        assertThat(result).isEqualTo(paymentDto);
        assertThat(payment.getStatus()).isEqualTo(newStatus);
        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentDto(payment);
    }

    @Test
    void updateStatus_whenPaymentMissing_shouldThrowException() {
        // given
        UUID missingId = UUID.randomUUID();
        when(paymentRepository.findById(missingId)).thenReturn(Optional.empty());

        // when //then
        assertThatThrownBy(() -> paymentService.updateStatus(missingId, PaymentStatus.RECEIVED))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден");

        verify(paymentRepository).findById(missingId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void updateNote_shouldChangePaymentNote() {
        // given
        String newNote = "New note";
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.updateNote(id, newNote);

        // then
        assertThat(result).isEqualTo(paymentDto);
        assertThat(payment.getNote()).isEqualTo(newNote);
        verify(paymentRepository).findById(id);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentDto(payment);
    }
    @Test
    void updateNote_whenPaymentMissing_shouldThrowException() {
        // given
        UUID missingId = UUID.randomUUID();
        when(paymentRepository.findById(missingId)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> paymentService.updateNote(missingId, "New note"))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Платеж не найден");

        verify(paymentRepository).findById(missingId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

}