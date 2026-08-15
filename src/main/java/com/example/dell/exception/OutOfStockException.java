package com.example.dell.exception;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {

    private final String orderId;

    public OutOfStockException(String orderId) {
        super("在庫が不足しています");
        this.orderId = orderId;
    }
}
