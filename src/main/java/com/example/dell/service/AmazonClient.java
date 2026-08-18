package com.example.dell.service;

import com.example.dell.dto.external.ProductSyncRequest;
import com.example.dell.entity.PcSpec;
import com.example.dell.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonClient {

    private final WebClient amazonWebClient;

    @Value("${amazon.sync-api-key}")
    private String syncApiKey;

    /**
     * 商品情報をAmazon側へ同期する(Dellが能動的にプッシュする)。
     * 人間のログインではなくAPIキーで本人確認する(サーバー間通信用の認証)。
     * 商品登録という比較的軽い操作のため、同期失敗はログに残すのみとし、
     * Dell側の商品登録自体は失敗させない(注文のような厳密な補償は行わない)。
     *
     * pcSpecはLAPTOP/DESKTOPの場合のみ渡され、MONITOR/ACCESSORYではnullになる。
     */
    public void syncProduct(Product product, PcSpec pcSpec) {
        try {
            ProductSyncRequest request = new ProductSyncRequest(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategory(),
                    product.isActive(),
                    pcSpec != null ? pcSpec.getRamGb() : null,
                    pcSpec != null ? pcSpec.getSsdGb() : null,
                    pcSpec != null ? pcSpec.getCpuMaker() : null,
                    pcSpec != null ? pcSpec.getHasGpu() : null
            );

            amazonWebClient.post()
                    .uri("/products/sync")
                    .header("X-API-Key", syncApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("Amazonへの商品同期に失敗しました productId={} message={}", product.getId(), e.getMessage());
        }
    }
}