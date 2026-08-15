package com.example.dell.controller;

import com.example.dell.dto.request.ConfirmRequest;
import com.example.dell.dto.request.ReleaseRequest;
import com.example.dell.dto.request.ReserveRequest;
import com.example.dell.dto.response.ReservationResponse;
import com.example.dell.dto.response.SuccessResponse;
import com.example.dell.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReserveRequest request) {
        Long reservationId = inventoryService.reserve(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ReservationResponse(reservationId));
    }

    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse> confirm(@Valid @RequestBody ConfirmRequest request) {
        inventoryService.confirm(request);
        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @PostMapping("/release")
    public ResponseEntity<SuccessResponse> release(@Valid @RequestBody ReleaseRequest request) {
        inventoryService.release(request);
        return ResponseEntity.ok(new SuccessResponse(true));
    }
}
