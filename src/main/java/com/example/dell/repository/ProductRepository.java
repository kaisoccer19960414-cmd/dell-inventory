package com.example.dell.repository;

import com.example.dell.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    /** PRD-xxxx採番のために、現在の商品件数を数える */
    long count();
}