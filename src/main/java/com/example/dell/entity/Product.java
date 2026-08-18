package com.example.dell.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    /**
     * 販売停止フラグ。falseの間は一覧・検索・購入から除外される。
     * 物理削除はしない(注文履歴・予約記録が商品IDを参照しているため)。
     */
    @Column(name = "is_active")
    private boolean isActive;

    /** 販売開始日。商品登録時に一度だけセットし、以降は変更しない(再開しても更新しない)。 */
    @Column(name = "activated_at")
    private LocalDate activatedAt;

    /**
     * 販売停止日。deactivate()のたびに最新の停止日で上書きする。
     * 運用上は停止した商品を再開することは基本的にない前提のため、
     * 「再開→再停止」で上書きされるケースは特別扱いしない。
     */
    @Column(name = "deactivated_at")
    private LocalDate deactivatedAt;

    @Version
    private int version;

    /**
     * 登録リクエストごとにブラウザ側で発行される一意のキー。
     * 通信の遅延で同じ登録ボタンを二度押ししても、二重登録を防ぐために使う。
     */
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    public Product(String id, String name, int price, int stock, ProductCategory category, String idempotencyKey) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.idempotencyKey = idempotencyKey;
        this.isActive = true;
        this.activatedAt = LocalDate.now();
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

    public void deactivate() {
        this.isActive = false;
        this.deactivatedAt = LocalDate.now();
    }

    public void activate() {
        this.isActive = true;
    }
}