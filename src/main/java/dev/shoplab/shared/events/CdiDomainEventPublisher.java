package dev.shoplab.shared.events;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;

@ApplicationScoped
public class CdiDomainEventPublisher implements DomainEventPublisher {
    private final Event<DomainEvent> events;

    public CdiDomainEventPublisher(Event<DomainEvent> events) {
        this.events = events;
    }

    public void publish(DomainEvent event) {
        events.fire(event);
    }
}
