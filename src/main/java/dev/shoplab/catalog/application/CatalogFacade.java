package dev.shoplab.catalog.application;

import dev.shoplab.catalog.api.CatalogApi;
import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.shared.errors.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CatalogFacade implements CatalogApi {
    private final ProductRepository productRepository;

    public CatalogFacade(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Optional<ProductView> findProduct(UUID productId) {
        return productRepository.findById(productId)
                .map(product -> new ProductView(
                        product.getId(),
                        product.getSku(),
                        product. getName(),
                        product.getPrice(),
                        product.getStock()
                ));
    }

    @Override
    @Transactional
    public void reserveStock(UUID productId, int quantity) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId))
                .reserve(quantity);
    }

    @Override
    @Transactional
    public void releaseStock(UUID productId, int quantity) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId))
                .release(quantity);
    }
}
