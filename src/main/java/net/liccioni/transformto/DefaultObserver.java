package net.liccioni.transformto;

import io.vavr.Value;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class DefaultObserver implements Observable {

    private final Map<Class<?>, Collection<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T> Subscription register(Class<T> eventType, Consumer<T> subscriber) {
        Consumer<Object> subscription = event -> subscriber.accept(eventType.cast(event));
        Collection<Consumer<Object>> subscriptions = subscribers.computeIfAbsent(eventType, _eventType -> new ArrayList<>());
        subscriptions.add(subscription);
        return () -> subscriptions.remove(subscription);
    }

    @Override
    public <T> void send(T event) {
        subscribers.entrySet().stream()
                .filter(entry -> canHandle(event.getClass(), entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(handler -> Try.runRunnable(() -> handler.accept(event)))
                .forEach(Value::getOrNull);
    }

    private boolean canHandle(Class<?> eventType, Class<?> handlerType) {
        return handlerType.isAssignableFrom(eventType);
    }
}
