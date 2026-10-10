package dev.shoplab.catalog.api;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CatalogApi {
    record ProductView(UUID id, String sku, String name, BigDecimal price, int availableStock){}

    Optional<ProductView> findProduct(UUID productId);
    void reserveStock(UUID productId, int quantity);
    void releaseStock(UUID productId, int quantity);

}
