package com.example.dell.repository;

import com.example.dell.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    long count();

    Optional<Product> findByIdempotencyKey(String idempotencyKey);

    /** 商品管理画面: 販売中の商品のみ(初期画面用) */
    List<Product> findByIsActiveTrue();

    /** 商品管理画面: 販売停止中の商品のみ */
    List<Product> findByIsActiveFalse();
}