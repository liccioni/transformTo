package net.liccioni.transformto;

import io.vavr.Value;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

enum DefaultObserver {

    INSTANCE;

    private final Map<Class<?>, Collection<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    private <T> Subscription on(Class<T> eventType, Consumer<T> subscriber) {
        Consumer<Object> subscription = event -> subscriber.accept(eventType.cast(event));
        Collection<Consumer<Object>> subscriptions = subscribers.computeIfAbsent(eventType, _eventType -> new ArrayList<>());
        subscriptions.add(subscription);
        return () -> subscriptions.remove(subscription);
    }

    private <T> Observable<T> on(Class<T> eventType) {
        return subscriber -> on(eventType, subscriber);
    }

    private <T> void send(T event) {
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

    protected static <T> Function<Class<T>, Observable<T>> getRegistry() {
        return INSTANCE::on;
    }

    protected static <T> Consumer<T> getSender() {
        return INSTANCE::send;
    }

    void clear() {
        INSTANCE.subscribers.clear();
    }
}
