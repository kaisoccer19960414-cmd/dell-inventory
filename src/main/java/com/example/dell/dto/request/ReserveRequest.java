package com.example.dell.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveRequest {

    @NotBlank
    private String orderId;

    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;
}