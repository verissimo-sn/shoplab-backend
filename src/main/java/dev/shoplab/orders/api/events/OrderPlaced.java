package dev.shoplab.orders.api.events;

import dev.shoplab.shared.events.DomainEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPlaced(UUID orderId, String customerEmail, BigDecimal total, List<Item> items) implements DomainEvent {
    public record Item(UUID productId, int quantity) {}

    @Override
    public String aggregationId() {
        return orderId.toString();
    }
}
