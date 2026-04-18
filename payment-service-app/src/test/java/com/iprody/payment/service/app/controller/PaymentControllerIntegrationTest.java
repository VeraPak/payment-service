package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.AbstractPostgresIntegrationTest;
import com.iprody.payment.service.app.TestJwtFactory;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldReturnAllPayments() throws Exception {
        // given - when
        mockMvc.perform(get("/api/payments")
                .with(TestJwtFactory.jwtWithRole("test-user", "reader")))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        // given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // when
        mockMvc.perform(get("/api/payments/{id}", id)
                .with(TestJwtFactory.jwtWithRole("test-user", "reader")))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        // given - when
        mockMvc.perform(get("/api/payments/search")
                .with(TestJwtFactory.jwtWithRole("test-user", "reader"))
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.id=='00000000-0000-0000-0000-000000000001')]").exists())
            .andExpect(jsonPath("$.content[?(@.id=='00000000-0000-0000-0000-000000000002')]").exists())
            .andExpect(jsonPath("$.content[?(@.id=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        // given
        PaymentDto dto = new PaymentDto();
        dto.setAmount(new BigDecimal("123.45"));
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);

        String json = objectMapper.writeValueAsString(dto);

        // when
        String response = mockMvc.perform(post("/api/payments")
                .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            // then
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.amount").value(123.45))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
        assertThat(saved.get().getCreatedAt()).isNotNull();
        assertThat(saved.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdatePayment() throws Exception {
        // given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PaymentDto dto = new PaymentDto();
        dto.setId(id);
        dto.setAmount(new BigDecimal("999.99"));
        dto.setCurrency("USD");
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setStatus(PaymentStatus.APPROVED);

        String json = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(put("/api/payments/{id}", id)
                .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.amount").value(999.99))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.inquiryRefId").exists())
            .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldUpdatePaymentStatus() throws Exception {
        // given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PaymentStatusUpdateDto statusUpdateDto = new PaymentStatusUpdateDto();
        statusUpdateDto.setStatus(PaymentStatus.DECLINED);

        String json = objectMapper.writeValueAsString(statusUpdateDto);

        // when
        mockMvc.perform(patch("/api/payments/{id}/status", id)
                .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    void shouldUpdatePaymentNote() throws Exception {
        // given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        PaymentNoteUpdateDto noteUpdateDto = new PaymentNoteUpdateDto();
        noteUpdateDto.setNote("test new note");

        String json = objectMapper.writeValueAsString(noteUpdateDto);

        // when
        mockMvc.perform(patch("/api/payments/{id}/note", id)
                .with(TestJwtFactory.jwtWithRole("test-user", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.note").value("test new note"));
    }

    @Test
    void shouldDeletePayment() throws Exception {
        // given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // when
        mockMvc.perform(delete("/api/payments/{id}", id)
                .with(TestJwtFactory.jwtWithRole("test-user", "admin")))
            // then
            .andExpect(status().isNoContent());

        Optional<Payment> deleted = paymentRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}