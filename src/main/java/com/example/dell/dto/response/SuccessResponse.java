package com.example.dell.dto.response;

import lombok.Getter;

@Getter
public class SuccessResponse {

    private final boolean success;

    public SuccessResponse(boolean success) {
        this.success = success;
    }
}
