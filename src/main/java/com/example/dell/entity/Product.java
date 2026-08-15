package com.example.dell.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {

    /** Dellが採番する商品ID(例: PRD-000001)。他社DBとの名前空間衝突を防ぐため文字列にしている */
    @Id
    private String id;

    private String name;

    private int price;

    private int stock;

    @Version
    private int version;

    /**
     * 登録リクエストごとにブラウザ側で発行される一意のキー。
     * 通信の遅延で同じ登録ボタンを二度押ししても、二重登録を防ぐために使う。
     */
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    public Product(String id, String name, int price, int stock, String idempotencyKey) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.idempotencyKey = idempotencyKey;
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("在庫が不足しています");
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}