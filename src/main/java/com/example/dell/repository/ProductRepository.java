package com.example.dell.repository;

import com.example.dell.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    long count();

    Optional<Product> findByIdempotencyKey(String idempotencyKey);
}