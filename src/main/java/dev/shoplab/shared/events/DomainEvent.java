package dev.shoplab.shared.events;

public interface DomainEvent {
    /** Identificador do agregado; vira a chave da mensagem no Kafka. */
    String aggregationId();
}
