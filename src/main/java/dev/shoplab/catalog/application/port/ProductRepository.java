package dev.shoplab.catalog.application.port;

import dev.shoplab.catalog.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    List<Product> list(int page, int size);
}
