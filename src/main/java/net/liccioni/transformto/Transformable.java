package net.liccioni.transformto;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Transformable {

    <T> T transformTo(Class<T> target);

    static <T, R> Subscription register(Class<T> sourceType, Class<R> targetType, Function<T, R> converter) {
        Function<Class<T>, BiFunction<Class<R>, Function<T, R>, Subscription>> register = DefaultTransformer.getRegistry();
        return register.apply(sourceType).apply(targetType, converter);
    }

    static Transformable toTransformable(Object source) {

        return new Transformable() {
            @Override
            public <T> T transformTo(Class<T> target) {
                BiFunction<Object, Class<T>, T> adapter = DefaultTransformer.getTransformer();
                return adapter.apply(source, target);
            }
        };
    }
}
