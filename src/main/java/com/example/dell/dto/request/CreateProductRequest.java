package com.example.dell.dto.request;

import com.example.dell.entity.CpuMaker;
import com.example.dell.entity.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int price;

    @Min(0)
    private int stock;

    @NotNull
    private ProductCategory category;

    /** LAPTOP/DESKTOPの場合のみ入力される。MONITOR/ACCESSORYではnullのまま送られてくる */
    private Integer ramGb;
    private Integer ssdGb;
    private CpuMaker cpuMaker;

    /** DESKTOPの場合のみ意味を持つ。LAPTOPやそれ以外ではnull */
    private Boolean hasGpu;

    /** ブラウザ側で生成される一意のキー。二重送信の検知に使う */
    private String idempotencyKey;
}