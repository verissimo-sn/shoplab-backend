package dev.shoplab.orders.domain;

public enum OrderStatus {
    PENDING, STOCK_RESERVED, CONFIRMED, CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING -> target == STOCK_RESERVED || target == CANCELLED;
            case STOCK_RESERVED -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED, CANCELLED -> false;
        };
    }
}
