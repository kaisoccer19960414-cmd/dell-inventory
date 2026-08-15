package com.example.dell.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReleaseRequest {

    @NotBlank
    private String orderId;

    @NotNull
    private Long reservationId;
}
