package org.net.gpstracer.application.domainevent;

public interface DomainEventListener<T extends DomainEvent> {
    Class<T> getEventType();
    void onEvent(T event);
}
