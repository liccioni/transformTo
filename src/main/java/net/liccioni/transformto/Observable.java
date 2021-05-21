package net.liccioni.transformto;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Observable<T> extends Function<Consumer<T>, Subscription> {

    default Subscription then(Consumer<T> sub) {
        return this.apply(sub);
    }

    static <T> Observable<T> register(Class<T> eventType) {
        Function<Class<T>, Observable<T>> registry = DefaultObserver.getRegistry();
        return registry.apply(eventType);
    }

    static <T> void send(T event) {
        DefaultObserver.getSender().accept(event);
    }
}
