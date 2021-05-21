package net.liccioni.transformto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTransformerTest {

    @BeforeEach
    void setUp() {
        DefaultTransformer.INSTANCE.clear();
    }

    @Test
    void shouldReturnNullIfNoAdapter() {
        Transformable aNumber = Transformable.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        assertThat(actual).isNull();
    }

    @Test
    void shouldTransformStringToFloat() {
        Transformable.register(String.class, Float.class, Float::parseFloat);
        Transformable aNumber = Transformable.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        Float expected = 42f;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldOverrideTransformer() {
        Transformable.register(String.class, Float.class, Float::parseFloat);
        Transformable.register(String.class, Float.class, string -> 42f);
        Transformable aNumber = Transformable.toTransformable("123.456");
        Float actual = aNumber.transformTo(Float.class);
        Float expected = 42f;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldTransformDifferentTargets() {
        Transformable.register(String.class, Float.class, Float::parseFloat);
        Transformable.register(String.class, Integer.class, Integer::parseInt);
        Transformable aNumber = Transformable.toTransformable("42");
        Float actualFloat = aNumber.transformTo(Float.class);
        Float expectedFloat = 42f;
        assertThat(actualFloat).isEqualTo(expectedFloat);
        Integer actualInteger = aNumber.transformTo(Integer.class);
        Integer expectedInteger = 42;
        assertThat(actualInteger).isEqualTo(expectedInteger);
    }

    @Test
    void shouldReturnNullAfterUnsubscribe() {
        Subscription subscription = Transformable.register(String.class, Float.class, Float::parseFloat);
        Transformable aNumber = Transformable.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        assertThat(actual).isNotNull();
        subscription.remove();
        Float actualAfterRemove = aNumber.transformTo(Float.class);
        assertThat(actualAfterRemove).isNull();
    }
}