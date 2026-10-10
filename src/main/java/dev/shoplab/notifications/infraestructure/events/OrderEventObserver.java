package dev.shoplab.notifications.infraestructure.events;

import dev.shoplab.orders.api.events.OrderPlaced;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

import java.util.logging.Logger;

@ApplicationScoped
public class OrderEventObserver {
    private final static Logger LOG = Logger.getLogger(OrderEventObserver.class.getName());

    void onOrderPlaced(@Observes(during = TransactionPhase.AFTER_SUCCESS)OrderPlaced event) {
        LOG.info(String.format("Sending 'order received' e-mail to %s for order(%s)", event.customerEmail(), event.orderId()));
    }
}
