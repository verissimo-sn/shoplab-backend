package dev.shoplab.catalog.web;

import dev.shoplab.catalog.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String sku, String name, BigDecimal price, int stock ) {
    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getPrice(), product.getStock());
    }
}
