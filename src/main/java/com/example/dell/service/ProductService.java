package com.example.dell.service;

import com.example.dell.dto.request.CreateProductRequest;
import com.example.dell.entity.Product;
import com.example.dell.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AmazonClient amazonClient;

    @Transactional
    public Product register(CreateProductRequest request) {
        if (request.getIdempotencyKey() != null) {
            var existing = productRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        String productId = generateProductId();
        Product product = new Product(
                productId, request.getName(), request.getPrice(), request.getStock(), request.getIdempotencyKey());
        productRepository.save(product);

        amazonClient.syncProduct(product.getId(), product.getName(), product.getPrice(), product.getStock());

        return product;
    }

    private String generateProductId() {
        long count = productRepository.count();
        long next = count + 1;
        return "PRD-" + String.format("%06d", next);
    }
}