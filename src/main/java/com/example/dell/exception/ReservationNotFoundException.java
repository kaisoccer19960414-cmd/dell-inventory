package com.example.dell.exception;

import lombok.Getter;

@Getter
public class ReservationNotFoundException extends RuntimeException {

    private final String orderId;

    public ReservationNotFoundException(String orderId) {
        super("指定された予約が見つかりません");
        this.orderId = orderId;
    }
}
