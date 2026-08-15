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

    public Product(String id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * 在庫を減らす。不足時はIllegalStateExceptionを投げる。
     * (楽観ロックによる同時アクセス競合の検知はJPAの@Versionが担う)
     */
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("在庫が不足しています");
        }
        this.stock -= quantity;
    }

    /**
     * 在庫を戻す(release時に使用)
     */
    public void increaseStock(int quantity) {
        this.stock += quantity;
    }
}