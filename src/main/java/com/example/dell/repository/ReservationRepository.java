package com.example.dell.repository;

import com.example.dell.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * confirm/release時に、Amazon側から渡されたorderId+reservationIdの組で
     * 予約記録を特定するために使用
     */
    Optional<Reservation> findByIdAndOrderId(Long id, String orderId);
}
