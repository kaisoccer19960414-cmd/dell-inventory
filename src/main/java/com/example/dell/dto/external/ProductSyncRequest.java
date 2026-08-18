package com.example.dell.dto.external;

import com.example.dell.entity.CpuMaker;
import com.example.dell.entity.ProductCategory;

public record ProductSyncRequest(
        String productId,
        String name,
        int price,
        int stock,
        ProductCategory category,
        boolean active,
        Integer ramGb,
        Integer ssdGb,
        CpuMaker cpuMaker,
        Boolean hasGpu
) {
}