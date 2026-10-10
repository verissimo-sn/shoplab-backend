package dev.shoplab.shared.events;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
