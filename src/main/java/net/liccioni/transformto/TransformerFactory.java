package net.liccioni.transformto;

import java.util.function.Function;

public interface TransformerFactory {

    <T, R> Subscription register(Class<T> sourceType, Class<R> targetType, Function<T, R> converter);

    Transformable toTransformable(Object source);

    static TransformerFactory create() {
        return new DefaultTransformer();
    }
}
