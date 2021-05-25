package net.liccioni.transformto;

import java.util.function.Consumer;

public interface Observable {

    <T> Subscription register(Class<T> eventType, Consumer<T> subscriber);

    <T> void send(T event);

    static <T> Observable create() {
        return new DefaultObserver();
    }
}
