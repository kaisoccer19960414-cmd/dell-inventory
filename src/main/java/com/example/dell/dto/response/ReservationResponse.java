package com.example.dell.dto.response;

import lombok.Getter;

@Getter
public class ReservationResponse {

    private final Long reservationId;

    public ReservationResponse(Long reservationId) {
        this.reservationId = reservationId;
    }
}
