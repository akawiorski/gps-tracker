package org.net.gpstracer.application.domainevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DomainEventBroadcaster {
    private final Map<Class<? extends DomainEvent>, List<DomainEventListener<? extends DomainEvent>>> eventListeners;

    @Autowired
      public DomainEventBroadcaster(final List<DomainEventListener<? extends DomainEvent>> eventListeners) {
        this.eventListeners = eventListeners.stream()
                .collect(Collectors.groupingBy(DomainEventListener::getEventType));
    }

    public <T extends DomainEvent> void emmit(T domainEvent) {
        eventListeners
                .getOrDefault(domainEvent.getClass(), List.of())
                .forEach(listener -> ((DomainEventListener<T>) listener).onEvent(domainEvent));
    }
}
