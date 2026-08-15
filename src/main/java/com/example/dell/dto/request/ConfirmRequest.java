package com.example.dell.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmRequest {

    @NotBlank
    private String orderId;

    @NotNull
    private Long reservationId;
}
