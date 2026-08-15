package com.example.dell.exception;

import com.example.dell.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException e) {
        ErrorResponse body = new ErrorResponse("OUT_OF_STOCK", e.getMessage(), e.getOrderId());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException e) {
        ErrorResponse body = new ErrorResponse("PRODUCT_NOT_FOUND", e.getMessage(), e.getOrderId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException e) {
        ErrorResponse body = new ErrorResponse("RESERVATION_NOT_FOUND", e.getMessage(), e.getOrderId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ReleaseFailedException.class)
    public ResponseEntity<ErrorResponse> handleReleaseFailed(ReleaseFailedException e) {
        ErrorResponse body = new ErrorResponse("RELEASE_FAILED", e.getMessage(), e.getOrderId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        ErrorResponse body = new ErrorResponse("INVALID_REQUEST", "リクエスト内容が不正です", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
