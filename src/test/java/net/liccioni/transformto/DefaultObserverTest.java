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
        DefaultTransformer.INSTANCE.clear();
    }

    @Test
    void shouldHandleSubscription() {
        AtomicReference<String> actual = new AtomicReference<>();
        Observable.register(String.class).then(actual::set);
        Observable.send("hello!");
        assertThat(actual.get()).isEqualTo("hello!");
    }

    @Test
    void shouldNotHandleAfterRemovingSubscription() {
        AtomicReference<String> actual = new AtomicReference<>();
        Subscription subscription = Observable.register(String.class).then(actual::set);
        subscription.remove();
        Observable.send("hello!");
        assertThat(actual.get()).isNull();
    }

    @Test
    void shouldHandleManySubscriptions() {
        AtomicReference<String> actual1 = new AtomicReference<>();
        AtomicReference<String> actual2 = new AtomicReference<>();
        Observable.register(String.class).then(s -> actual1.set(s + " actual1"));
        Observable.register(String.class).then(s -> actual2.set(s + " actual2"));
        Observable.send("hello!");
        assertThat(actual1.get()).isEqualTo("hello! actual1");
        assertThat(actual2.get()).isEqualTo("hello! actual2");
    }

    @Test
    void shouldHandleDifferentTypes() {
        AtomicReference<String> actualString = new AtomicReference<>();
        AtomicReference<Float> actualFloat = new AtomicReference<>();
        Observable.register(String.class).then(actualString::set);
        Observable.register(Float.class).then(actualFloat::set);
        Observable.send("hello!");
        Observable.send(42f);
        assertThat(actualString.get()).isEqualTo("hello!");
        assertThat(actualFloat.get()).isEqualTo(42f);
    }

    @Test
    void shouldHandleHierarchy() {
        List<Number> numbers = new ArrayList<>();
        Observable.register(Number.class).then(numbers::add);
        Observable.send(1);
        Observable.send(2d);
        Observable.send(3f);
        assertThat(numbers).containsExactly(1, 2d, 3f);
    }
}