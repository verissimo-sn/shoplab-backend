package dev.shoplab.catalog.infraestructure.persistence;

import dev.shoplab.catalog.domain.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProductPanacheRepository implements PanacheRepositoryBase<Product, UUID> { }
