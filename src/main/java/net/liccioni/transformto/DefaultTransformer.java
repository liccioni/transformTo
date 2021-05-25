package net.liccioni.transformto;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.control.Try;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class DefaultTransformer implements TransformerFactory {

    private final Map<Tuple2<Class<?>, Class<?>>, Function<Object, ?>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T, R> Subscription register(Class<T> sourceType, Class<R> targetType, Function<T, R> converter) {
        Function<Object, ?> subscription = event -> converter.apply(sourceType.cast(event));
        Tuple2<Class<?>, Class<?>> key = Tuple.of(sourceType, targetType);
        subscribers.put(key, subscription);
        return () -> subscribers.remove(key);
    }

    @Override
    public Transformable toTransformable(Object source) {
        return new Transformable() {
            @Override
            public <T> T transformTo(Class<T> target) {
                return DefaultTransformer.this.getTransformer(source, target);
            }
        };
    }

    private <T> T getTransformer(Object transformable, Class<T> target) {
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
}
