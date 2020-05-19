package com.dansoftware.libraryapp.util.function;

@FunctionalInterface
public interface DualConsumer<T1, T2> {
    void accept(T1 param1, T2 param2);
}
