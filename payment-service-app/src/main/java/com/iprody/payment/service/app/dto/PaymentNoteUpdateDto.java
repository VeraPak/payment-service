package com.iprody.payment.service.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentNoteUpdateDto {
    @NotNull
    private String note;
}
