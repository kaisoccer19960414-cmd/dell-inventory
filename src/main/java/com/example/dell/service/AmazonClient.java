package com.example.dell.service;

import com.example.dell.dto.external.ProductSyncRequest;
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
     */
    public void syncProduct(String productId, String name, int price, int stock) {
        try {
            amazonWebClient.post()
                    .uri("/products/sync")
                    .header("X-API-Key", syncApiKey)
                    .bodyValue(new ProductSyncRequest(productId, name, price, stock))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("Amazonへの商品同期に失敗しました productId={} message={}", productId, e.getMessage());
        }
    }
}