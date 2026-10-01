package dev.shoplab.catalog.application;

import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.catalog.domain.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListProductsUseCase {
    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> execute(int page, int size) {
        return productRepository.list(page, size);
    }
}
