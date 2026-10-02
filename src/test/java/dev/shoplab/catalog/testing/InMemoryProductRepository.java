package dev.shoplab.catalog.testing;

import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.catalog.domain.Product;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> data = new HashMap<>();

    @Override
    public Product save(Product product) {
        data.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return data.values().stream().filter(p -> p.getSku().equals(sku)).findFirst();
    }

    @Override
    public List<Product> list(int page, int size) {
        // mesmo contrato do adapter real: ordenado por nome e paginado
        return data.values().stream()
                .sorted(Comparator.comparing(Product::getName))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    /** Atalho para montar cenários: cria e já salva um produto. */
    public Product given(String sku, String name, String price, int stock) {
        return save(Product.create(sku, name, new java.math.BigDecimal(price), stock));
    }
}
