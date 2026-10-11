package dev.shoplab.orders.application;

import dev.shoplab.catalog.api.CatalogApi;
import dev.shoplab.orders.api.events.OrderPlaced;
import dev.shoplab.orders.application.port.OrderRepository;
import dev.shoplab.orders.domain.Order;
import dev.shoplab.orders.domain.OrderItem;
import dev.shoplab.shared.errors.BusinessRuleException;
import dev.shoplab.shared.events.DomainEventPublisher;
import dev.shoplab.shipping.api.ShippingApi;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CreateOrderUseCase {
    public record Command(String customerEmail, String zipCode, List<Item> items){
        public  record Item(UUID productId, int quantity){}
    };

    private final OrderRepository orderRepository;
    private final CatalogApi catalogApi;
    private final DomainEventPublisher event;
    private final ShippingApi shippingApi;

    public CreateOrderUseCase(OrderRepository orderRepository, CatalogApi catalogApi, DomainEventPublisher event, ShippingApi shippingApi) {
        this.orderRepository = orderRepository;
        this.catalogApi = catalogApi;
        this.event = event;
        this.shippingApi = shippingApi;
    }

    public Order execute(Command cmd) {
       var quote = shippingApi.quote(cmd.zipCode());
       return QuarkusTransaction.requiringNew().call(
               () -> createOrder(cmd, quote.cost())
       );
    }

    private Order createOrder(Command cmd, BigDecimal shippingCost) {
        var drafts = cmd.items().stream().map(i -> {
            var product = catalogApi.findProduct(i.productId())
                    .orElseThrow(() -> new BusinessRuleException("PRODUCT_NOT_FOUND", "Product not found: " + i.productId()));
            catalogApi.reserveStock(product.id(), i.quantity());
            return new OrderItem.Draft(product.id(), product.sku(), product.name(),  product.price(), i.quantity());
        }).toList();
        var order = Order.place(cmd.customerEmail(), cmd.zipCode(), shippingCost, drafts);
        order.markStockReserved();
        orderRepository.save(order);

        var orderItems = cmd.items().stream().map(i -> new OrderPlaced.Item(i.productId(), i.quantity())).toList();
        event.publish(new OrderPlaced(order.getId(), order.getCustomerEmail(), order.getTotal(), orderItems));
        return order;
    }
}
