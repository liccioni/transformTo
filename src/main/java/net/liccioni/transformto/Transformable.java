package net.liccioni.transformto;

public interface Transformable {

    <T> T transformTo(Class<T> target);
}
