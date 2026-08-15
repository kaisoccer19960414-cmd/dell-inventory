package com.example.dell.service;

import com.example.dell.dto.request.ConfirmRequest;
import com.example.dell.dto.request.ReleaseRequest;
import com.example.dell.dto.request.ReserveRequest;
import com.example.dell.entity.Product;
import com.example.dell.entity.Reservation;
import com.example.dell.entity.ReservationStatus;
import com.example.dell.exception.OutOfStockException;
import com.example.dell.exception.ProductNotFoundException;
import com.example.dell.exception.ReleaseFailedException;
import com.example.dell.exception.ReservationNotFoundException;
import com.example.dell.repository.ProductRepository;
import com.example.dell.repository.ReservationRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 在庫仮確保。
     * Amazon側から見ると「この注文のために在庫を押さえてほしい」という依頼。
     */
    @Transactional
    public Long reserve(ReserveRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getOrderId()));

        try {
            product.decreaseStock(request.getQuantity());
            productRepository.saveAndFlush(product);
        } catch (IllegalStateException | OptimisticLockException e) {
            // 在庫不足、または他の注文との同時確保競合はどちらも「今は確保できない」として扱う
            throw new OutOfStockException(request.getOrderId());
        }

        Reservation reservation = new Reservation(
                request.getOrderId(), request.getProductId(), request.getQuantity());
        reservationRepository.save(reservation);

        return reservation.getId();
    }

    /**
     * 在庫確定。決済が成功した後にAmazon側から呼ばれる想定。
     */
    @Transactional
    public void confirm(ConfirmRequest request) {
        Reservation reservation = reservationRepository
                .findByIdAndOrderId(request.getReservationId(), request.getOrderId())
                .orElseThrow(() -> new ReservationNotFoundException(request.getOrderId()));

        reservation.confirm();
        reservationRepository.save(reservation);
    }

    /**
     * 在庫解放。決済が失敗した後にAmazon側から呼ばれる補償処理。
     * 仮確保していた分の在庫を実際に戻す。
     */
    @Transactional
    public void release(ReleaseRequest request) {
        Reservation reservation = reservationRepository
                .findByIdAndOrderId(request.getReservationId(), request.getOrderId())
                .orElseThrow(() -> new ReservationNotFoundException(request.getOrderId()));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            // CONFIRMED済みや既にRELEASED済みのものを二重解放させない
            throw new ReleaseFailedException(request.getOrderId());
        }

        Product product = productRepository.findById(reservation.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getOrderId()));

        product.increaseStock(reservation.getQuantity());
        productRepository.saveAndFlush(product);

        reservation.release();
        reservationRepository.save(reservation);
    }
}
