package net.liccioni.transformto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTransformerTest {

    @Test
    void shouldReturnNullIfNoAdapter() {
        TransformerFactory factory = TransformerFactory.create();
        Transformable aNumber = factory.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        assertThat(actual).isNull();
    }

    @Test
    void shouldTransformStringToFloat() {
        TransformerFactory factory = TransformerFactory.create();
        factory.register(String.class, Float.class, Float::parseFloat);
        Transformable aNumber = factory.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        Float expected = 42f;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldOverrideTransformer() {
        TransformerFactory factory = TransformerFactory.create();
        factory.register(String.class, Float.class, Float::parseFloat);
        factory.register(String.class, Float.class, string -> 42f);
        Transformable aNumber = factory.toTransformable("123.456");
        Float actual = aNumber.transformTo(Float.class);
        Float expected = 42f;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldTransformDifferentTargets() {
        TransformerFactory factory = TransformerFactory.create();
        factory.register(String.class, Float.class, Float::parseFloat);
        factory.register(String.class, Integer.class, Integer::parseInt);
        Transformable aNumber = factory.toTransformable("42");
        Float actualFloat = aNumber.transformTo(Float.class);
        Float expectedFloat = 42f;
        assertThat(actualFloat).isEqualTo(expectedFloat);
        Integer actualInteger = aNumber.transformTo(Integer.class);
        Integer expectedInteger = 42;
        assertThat(actualInteger).isEqualTo(expectedInteger);
    }

    @Test
    void shouldReturnNullAfterUnsubscribe() {
        TransformerFactory factory = TransformerFactory.create();
        Subscription subscription = factory.register(String.class, Float.class, Float::parseFloat);
        Transformable aNumber = factory.toTransformable("42");
        Float actual = aNumber.transformTo(Float.class);
        assertThat(actual).isNotNull();
        subscription.remove();
        Float actualAfterRemove = aNumber.transformTo(Float.class);
        assertThat(actualAfterRemove).isNull();
    }
}