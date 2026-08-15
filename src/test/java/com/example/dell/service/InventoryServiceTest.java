package com.example.dell.service;

import com.example.dell.dto.request.ConfirmRequest;
import com.example.dell.dto.request.ReleaseRequest;
import com.example.dell.dto.request.ReserveRequest;
import com.example.dell.entity.Product;
import com.example.dell.entity.Reservation;
import com.example.dell.exception.OutOfStockException;
import com.example.dell.exception.ProductNotFoundException;
import com.example.dell.exception.ReleaseFailedException;
import com.example.dell.exception.ReservationNotFoundException;
import com.example.dell.repository.ProductRepository;
import com.example.dell.repository.ReservationRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * InventoryService(Dell)の単体テスト。
 * 楽観ロック競合の扱いと、二重解放を防ぐ状態チェックを重点的に確認する。
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private static final String ORDER_ID = "ORD-20260813-0001";
    private static final String PRODUCT_ID = "PRD-000001";
    private static final Long RESERVATION_ID = 1L;

    // ---------- reserve ----------

    @Test
    void reserve_在庫が十分あれば予約できて在庫が減る() {
        Product product = new Product(PRODUCT_ID, "テスト商品", 10000, 5, null);
        ReserveRequest request = reserveRequest(2);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        inventoryService.reserve(request);

        assertThat(product.getStock()).isEqualTo(3);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void reserve_在庫不足ならOutOfStockExceptionになる() {
        Product product = new Product(PRODUCT_ID, "テスト商品", 10000, 1, null);
        ReserveRequest request = reserveRequest(5);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> inventoryService.reserve(request))
                .isInstanceOf(OutOfStockException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserve_楽観ロック競合が起きたらOutOfStockExceptionとして扱う() {
        Product product = new Product(PRODUCT_ID, "テスト商品", 10000, 5, null);
        ReserveRequest request = reserveRequest(2);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.saveAndFlush(any(Product.class)))
                .thenThrow(new OptimisticLockException("他の注文と競合しました"));

        // 他の注文の同時アクセスによる競合も、Amazon側から見れば「在庫不足」と同じ扱いにする設計
        assertThatThrownBy(() -> inventoryService.reserve(request))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void reserve_存在しない商品ならProductNotFoundExceptionになる() {
        ReserveRequest request = reserveRequest(1);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.reserve(request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ---------- confirm ----------

    @Test
    void confirm_予約が見つかれば確定状態になる() {
        Reservation reservation = new Reservation(ORDER_ID, PRODUCT_ID, 1);
        ConfirmRequest request = new ConfirmRequest();
        request.setOrderId(ORDER_ID);
        request.setReservationId(RESERVATION_ID);

        when(reservationRepository.findByIdAndOrderId(RESERVATION_ID, ORDER_ID))
                .thenReturn(Optional.of(reservation));

        inventoryService.confirm(request);

        assertThat(reservation.getStatus().name()).isEqualTo("CONFIRMED");
        verify(reservationRepository).save(reservation);
    }

    @Test
    void confirm_予約が見つからなければReservationNotFoundExceptionになる() {
        ConfirmRequest request = new ConfirmRequest();
        request.setOrderId(ORDER_ID);
        request.setReservationId(RESERVATION_ID);

        when(reservationRepository.findByIdAndOrderId(RESERVATION_ID, ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.confirm(request))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    // ---------- release ----------

    @Test
    void release_RESERVED状態なら在庫が戻り解放できる() {
        Reservation reservation = new Reservation(ORDER_ID, PRODUCT_ID, 2);
        Product product = new Product(PRODUCT_ID, "テスト商品", 10000, 3, null);
        ReleaseRequest request = releaseRequest();

        when(reservationRepository.findByIdAndOrderId(RESERVATION_ID, ORDER_ID))
                .thenReturn(Optional.of(reservation));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        inventoryService.release(request);

        assertThat(product.getStock()).isEqualTo(5);
        assertThat(reservation.getStatus().name()).isEqualTo("RELEASED");
    }

    @Test
    void release_既にCONFIRMED済みなら二重解放させずReleaseFailedExceptionになる() {
        Reservation reservation = new Reservation(ORDER_ID, PRODUCT_ID, 2);
        reservation.confirm(); // 先に決済確定済みの状態にしておく
        ReleaseRequest request = releaseRequest();

        when(reservationRepository.findByIdAndOrderId(RESERVATION_ID, ORDER_ID))
                .thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> inventoryService.release(request))
                .isInstanceOf(ReleaseFailedException.class);

        // 状態チェックで弾かれるので、在庫操作(Product検索)自体が走らないことも確認する
        verify(productRepository, never()).findById(anyString());
    }

    @Test
    void release_予約が見つからなければReservationNotFoundExceptionになる() {
        ReleaseRequest request = releaseRequest();

        when(reservationRepository.findByIdAndOrderId(RESERVATION_ID, ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.release(request))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    // ---------- helpers ----------

    private ReserveRequest reserveRequest(int quantity) {
        ReserveRequest request = new ReserveRequest();
        request.setOrderId(ORDER_ID);
        request.setProductId(PRODUCT_ID);
        request.setQuantity(quantity);
        return request;
    }

    private ReleaseRequest releaseRequest() {
        ReleaseRequest request = new ReleaseRequest();
        request.setOrderId(ORDER_ID);
        request.setReservationId(RESERVATION_ID);
        return request;
    }
}