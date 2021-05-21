package net.liccioni.transformto;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.control.Try;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

enum DefaultTransformer {

    INSTANCE;

    private final Map<Tuple2<Class<?>, Class<?>>, Function<Object, ?>> subscribers = new ConcurrentHashMap<>();

    private <T, R> Subscription register(Class<T> sourceType, Class<R> targetType, Function<T, R> converter) {
        Function<Object, ?> subscription = event -> converter.apply(sourceType.cast(event));
        Tuple2<Class<?>, Class<?>> key = Tuple.of(sourceType, targetType);
        subscribers.put(key, subscription);
        return () -> subscribers.remove(key);
    }

    private <T, R> BiFunction<Class<R>, Function<T, R>, Subscription> register(Class<T> sourceType) {
        return (targetType, converter) -> register(sourceType, targetType, converter);
    }

    private <T> T transform(Object transformable, Class<T> target) {
        return subscribers.entrySet().stream()
                .filter(entry -> canHandle(transformable.getClass(), entry.getKey(), target))
                .map(Map.Entry::getValue)
                .map(handler -> Try.of(() -> handler.apply(transformable)))
                .map(Value::getOrNull)
                .filter(Objects::nonNull)
                .map(target::cast)
                .findFirst()
                .orElse(null);
    }

    private boolean canHandle(Class<?> transformableType, Tuple2<Class<?>, Class<?>> handlerTypes, Class<?> targetType) {
        Class<?> sourceType = handlerTypes._1();
        Class<?> returnType = handlerTypes._2();
        return sourceType.isAssignableFrom(transformableType) &&
                returnType.isAssignableFrom(targetType);
    }

    protected static <T> BiFunction<Object, Class<T>, T> getTransformer() {
        return INSTANCE::transform;
    }

    protected static <T, R> Function<Class<T>, BiFunction<Class<R>, Function<T, R>, Subscription>> getRegistry() {
        return INSTANCE::register;
    }

    void clear() {
        INSTANCE.subscribers.clear();
    }
}
