package com.example.dell.service;

import com.example.dell.dto.request.CreateProductRequest;
import com.example.dell.entity.Product;
import com.example.dell.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AmazonClient amazonClient;

    /**
     * 商品を登録し、Dellが採番した商品IDをAmazon側へ即座に同期する。
     * (登録した瞬間に自動でAmazonへプッシュされる。GitHubのプッシュに近いが、
     * 人間が別途操作するのではなく保存と同時に自動で行われる点が異なる)
     */
    @Transactional
    public Product register(CreateProductRequest request) {
        String productId = generateProductId();
        Product product = new Product(productId, request.getName(), request.getPrice(), request.getStock());
        productRepository.save(product);

        amazonClient.syncProduct(product.getId(), product.getName(), product.getPrice(), product.getStock());

        return product;
    }

    /**
     * PRD-000001 形式で採番する。相関ID(ORD-yyyyMMdd-連番)と異なり、
     * 商品マスタは発生頻度が低いため日付を含めないシンプルな連番にしている。
     */
    private String generateProductId() {
        long count = productRepository.count();
        long next = count + 1;
        return "PRD-" + String.format("%06d", next);
    }
}