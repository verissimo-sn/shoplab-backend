package dev.shoplab.catalog.application;

import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.catalog.domain.Product;
import dev.shoplab.shared.errors.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetProductUseCase {
    private final ProductRepository productRepository;

    public GetProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
