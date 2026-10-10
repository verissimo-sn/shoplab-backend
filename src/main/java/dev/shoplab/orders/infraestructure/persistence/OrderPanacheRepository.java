package dev.shoplab.orders.infraestructure.persistence;

import dev.shoplab.orders.domain.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class OrderPanacheRepository implements PanacheRepositoryBase<Order, UUID> {
}
