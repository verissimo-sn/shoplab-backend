package dev.shoplab.orders.infraestructure.persistence;

import dev.shoplab.orders.application.port.OrderRepository;
import dev.shoplab.orders.domain.Order;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaOrderRepository implements OrderRepository {
    private final OrderPanacheRepository panacheRepository;

    public JpaOrderRepository(OrderPanacheRepository panacheRepository) {
        this.panacheRepository = panacheRepository;
    }

    @Override
    public Order save(Order order) {
       panacheRepository.persist(order);
       return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return panacheRepository.find("select distinct o from CustomerOrder o left join fetch o.items where o.id = ?1", id).singleResultOptional();
    }
}
