package dev.shoplab.catalog.domain;

import dev.shoplab.shared.errors.BusinessRuleException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "catalog")
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Version
    private long version;

    @Column(name = "created_at",  nullable = false, updatable = false)
    private Instant createdAt;

    protected Product() {} // exigido pelo JPA

    private Product(String sku, String name, BigDecimal price, int stock) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdAt = Instant.now();
    }

    public static Product create(String sku, String name, BigDecimal price, int initialStock) {
        if(price.signum() <=0) {
            throw new BusinessRuleException("INVALID_PRICE", "Price must be positive");
        }

        if(initialStock < 0) {
            throw new BusinessRuleException("INVALID_STOCK", "Stock cannot be negative");
        }

        return new Product(sku, name, price, initialStock);
    }

    public void reserve(int quantity) {
        if(quantity <= 0) {
            throw new BusinessRuleException("INVALID_QUANTITY", "Quantity must be positive");
        }

        if(stock < quantity) {
            throw new BusinessRuleException("INSUFFICIENT_STOCK", String.format("Product %s has %d units, requested %d",  sku, stock, quantity));
        }

        stock -= quantity;
    }

    public void release(int quantity) {
        stock += quantity;
    }

    public UUID getId() {
        return id;
    }

    public String getSku() { return sku; }
    public String getName() {  return name; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
}
