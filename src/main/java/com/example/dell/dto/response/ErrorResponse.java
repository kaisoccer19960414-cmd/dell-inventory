package com.example.dell.dto.response;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String errorCode;
    private final String message;
    private final String orderId;

    public ErrorResponse(String errorCode, String message, String orderId) {
        this.errorCode = errorCode;
        this.message = message;
        this.orderId = orderId;
    }
}
