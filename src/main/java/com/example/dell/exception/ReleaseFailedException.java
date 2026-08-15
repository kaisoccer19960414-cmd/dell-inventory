package com.example.dell.exception;

import lombok.Getter;

@Getter
public class ReleaseFailedException extends RuntimeException {

    private final String orderId;

    public ReleaseFailedException(String orderId) {
        super("在庫の解放に失敗しました");
        this.orderId = orderId;
    }
}
