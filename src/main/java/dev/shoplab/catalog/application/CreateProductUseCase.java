package dev.shoplab.catalog.application;

import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.catalog.domain.Product;
import dev.shoplab.shared.errors.BusinessRuleException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class CreateProductUseCase {
    public record Command(String sku, String name, BigDecimal price, int initialStock ) {}

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(Command cmd) {
        productRepository.findBySku(cmd.sku()).ifPresent(
            existingProduct -> {
                throw  new BusinessRuleException("SKU_ALREADY_EXISTS", "SKU already exists: " + cmd.sku());
            });
        return productRepository.save(Product.create(
            cmd.sku(),  cmd.name(), cmd.price(), cmd.initialStock()
        ));
    }
}
