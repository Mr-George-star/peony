package net.george.peony.util;

@FunctionalInterface
public interface DoubleParamsFunction<T, V, R> {
    R apply(T param1, V param2);
}
