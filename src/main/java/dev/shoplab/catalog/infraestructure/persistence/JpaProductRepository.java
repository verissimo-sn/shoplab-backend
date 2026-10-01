package dev.shoplab.catalog.infraestructure.persistence;

import dev.shoplab.catalog.application.port.ProductRepository;
import dev.shoplab.catalog.domain.Product;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaProductRepository implements ProductRepository {
    private final ProductPanacheRepository panacheRepository;

    public JpaProductRepository(ProductPanacheRepository panacheRepository) {
        this.panacheRepository = panacheRepository;
    }

    @Override
    public Product save(Product product) {
        panacheRepository.persist(product);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return panacheRepository.findByIdOptional(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return panacheRepository.find("sku", sku).firstResultOptional();
    }

    @Override
    public List<Product> list(int page, int size) {
        return panacheRepository.findAll(Sort.by("name")).page(page, size).list();
    }
}
