package com.example.dell.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final String orderId;

    public ProductNotFoundException(String orderId) {
        super("指定された商品が見つかりません");
        this.orderId = orderId;
    }
}
