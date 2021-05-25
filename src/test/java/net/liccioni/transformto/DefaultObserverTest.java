package net.liccioni.transformto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultObserverTest {

    @BeforeEach
    void setUp() {
//        DefaultObserver.INSTANCE.clear();
    }

    @Test
    void shouldHandleSubscription() {
        Observable observable = Observable.create();
        AtomicReference<String> actual = new AtomicReference<>();
        observable.register(String.class, actual::set);
        observable.send("hello!");
        assertThat(actual.get()).isEqualTo("hello!");
    }

    @Test
    void shouldNotHandleAfterRemovingSubscription() {
        Observable observable = Observable.create();
        AtomicReference<String> actual = new AtomicReference<>();
        Subscription subscription = observable.register(String.class, actual::set);
        subscription.remove();
        observable.send("hello!");
        assertThat(actual.get()).isNull();
    }

    @Test
    void shouldHandleManySubscriptions() {
        Observable observable = Observable.create();
        AtomicReference<String> actual1 = new AtomicReference<>();
        AtomicReference<String> actual2 = new AtomicReference<>();
        observable.register(String.class, s -> actual1.set(s + " actual1"));
        observable.register(String.class, s -> actual2.set(s + " actual2"));
        observable.send("hello!");
        assertThat(actual1.get()).isEqualTo("hello! actual1");
        assertThat(actual2.get()).isEqualTo("hello! actual2");
    }

    @Test
    void shouldHandleDifferentTypes() {
        Observable observable = Observable.create();
        AtomicReference<String> actualString = new AtomicReference<>();
        AtomicReference<Float> actualFloat = new AtomicReference<>();
        observable.register(String.class, actualString::set);
        observable.register(Float.class, actualFloat::set);
        observable.send("hello!");
        observable.send(42f);
        assertThat(actualString.get()).isEqualTo("hello!");
        assertThat(actualFloat.get()).isEqualTo(42f);
    }

    @Test
    void shouldHandleHierarchy() {
        Observable observable = Observable.create();
        List<Number> numbers = new ArrayList<>();
        observable.register(Number.class, numbers::add);
        observable.send(1);
        observable.send(2d);
        observable.send(3f);
        assertThat(numbers).containsExactly(1, 2d, 3f);
    }
}