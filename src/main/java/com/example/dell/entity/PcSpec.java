package com.example.dell.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LAPTOP/DESKTOPカテゴリの商品だけが持つスペック情報。
 * MONITOR/ACCESSORYの商品には、この行自体が存在しない。
 * hasGpuはDESKTOPの場合のみ意味を持ち、LAPTOPの行ではnullのままにする。
 */
@Entity
@Table(name = "pc_specs")
@Getter
@NoArgsConstructor
public class PcSpec {

    /** productsテーブルのidをそのまま主キー兼外部キーとして使う(1商品につき1行) */
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "ram_gb")
    private int ramGb;

    @Column(name = "ssd_gb")
    private int ssdGb;

    @Enumerated(EnumType.STRING)
    @Column(name = "cpu_maker")
    private CpuMaker cpuMaker;

    /** DESKTOPのみ使用。LAPTOPの場合はnull */
    @Column(name = "has_gpu")
    private Boolean hasGpu;

    public PcSpec(String productId, int ramGb, int ssdGb, CpuMaker cpuMaker, Boolean hasGpu) {
        this.productId = productId;
        this.ramGb = ramGb;
        this.ssdGb = ssdGb;
        this.cpuMaker = cpuMaker;
        this.hasGpu = hasGpu;
    }
}