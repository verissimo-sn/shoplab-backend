package dev.shoplab.orders.application;

import dev.shoplab.orders.application.port.OrderRepository;
import dev.shoplab.orders.domain.Order;
import dev.shoplab.shared.errors.BusinessRuleException;
import dev.shoplab.shared.errors.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetOrderUseCase {
    private final OrderRepository orderRepository;

    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order execute(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}
