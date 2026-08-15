package com.example.dell.dto.external;

public record ProductSyncRequest(String productId, String name, int price, int stock) {
}