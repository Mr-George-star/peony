package net.george.peony.util;

@FunctionalInterface
public interface ThreeParamsFunction<T, U, V, R> {
    R apply(T param1, U param2, V param3);
}
