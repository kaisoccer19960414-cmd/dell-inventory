package com.example.dell.service;

import com.example.dell.dto.request.CreateProductRequest;
import com.example.dell.entity.PcSpec;
import com.example.dell.entity.Product;
import com.example.dell.entity.ProductCategory;
import com.example.dell.repository.PcSpecRepository;
import com.example.dell.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PcSpecRepository pcSpecRepository;
    private final AmazonClient amazonClient;

    @Transactional
    public Product register(CreateProductRequest request) {
        if (request.getIdempotencyKey() != null) {
            var existing = productRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        String productId = generateProductId();
        Product product = new Product(
                productId, request.getName(), request.getPrice(), request.getStock(),
                request.getCategory(), request.getIdempotencyKey());
        productRepository.save(product);

        PcSpec pcSpec = null;
        boolean isPc = request.getCategory() == ProductCategory.LAPTOP
                || request.getCategory() == ProductCategory.DESKTOP;

        if (isPc && request.getRamGb() != null && request.getSsdGb() != null && request.getCpuMaker() != null) {
            pcSpec = new PcSpec(productId, request.getRamGb(), request.getSsdGb(),
                    request.getCpuMaker(), request.getHasGpu());
            pcSpecRepository.save(pcSpec);
        }

        amazonClient.syncProduct(product, pcSpec);

        return product;
    }

    private String generateProductId() {
        long count = productRepository.count();
        long next = count + 1;
        return "PRD-" + String.format("%06d", next);
    }

    /**
     * 商品を販売停止/再開する。物理削除はせず、is_activeフラグを切り替えるだけ。
     * 変更内容はAmazon側へも同期する。
     */
    @Transactional
    public void setActive(String productId, boolean active) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: " + productId));

        if (active) {
            product.activate();
        } else {
            product.deactivate();
        }
        productRepository.save(product);

        PcSpec pcSpec = pcSpecRepository.findById(productId).orElse(null);
        amazonClient.syncProduct(product, pcSpec);
    }
}