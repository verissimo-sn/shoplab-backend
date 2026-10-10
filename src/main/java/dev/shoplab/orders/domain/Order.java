package dev.shoplab.orders.domain;

import dev.shoplab.shared.errors.BusinessRuleException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity(name = "CustomerOrder") // "Order" é palavra reservada em JPQL
@Table(name = "customer_orders", schema = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;

    @Column(name = "shipping_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Order() {}

    private Order(String customerEmail, String zipCode, BigDecimal shippingCost) {
        this.id = UUID.randomUUID();
        this.customerEmail = customerEmail;
        this.zipCode = zipCode;
        this.shippingCost = shippingCost;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public static Order place(String customerEmail, String zipCode, BigDecimal shippingCost, List<OrderItem.Draft> drafts) {
        if (drafts.isEmpty()) {
            throw new BusinessRuleException("EMPTY_ORDER", "Order must have at least one item");
        }
        var order = new Order(customerEmail, zipCode, shippingCost);
        drafts.forEach(d -> order.items.add(OrderItem.create(order, d)));
        order.total = order.items.stream()
                .map(OrderItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(shippingCost);
        return order;
    }

    public void markStockReserved() { transitionTo(OrderStatus.STOCK_RESERVED); }

    public void confirm() { transitionTo(OrderStatus.CONFIRMED); }

    public void cancel(String reason) {
        transitionTo(OrderStatus.CANCELLED);
        this.cancellationReason = reason;
    }

    private void transitionTo(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    "Order %s cannot go from %s to %s".formatted(id, status, target));
        }
        status = target;
    }

    public UUID getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public String getZipCode() { return zipCode; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public String getCancellationReason() { return cancellationReason; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
