package dev.shoplab.orders.web;

import dev.shoplab.orders.domain.Order;
import dev.shoplab.orders.domain.OrderItem;
import dev.shoplab.orders.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerEmail,
        String zipCode,
        BigDecimal shippingCost,
        BigDecimal total,
        OrderStatus status,
        String cancellationReason,
        List<OrderItemResponse> items
) {
    public record OrderItemResponse(UUID id, UUID productId, String sku, String productName, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
        static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(item.getId(), item.getProductId(), item.getSku(), item.getProductName(), item.getUnitPrice(), item.getQuantity(), item.subtotal());
        }
    }

    static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getZipCode(),
                order.getShippingCost(),
                order.getTotal(),
                order.getStatus(),
                order.getCancellationReason(),
                order.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
