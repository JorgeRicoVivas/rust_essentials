package io.github.jorgericovivas.rust_essentials.result;

import java.util.function.Supplier;

/**
 * A {@link Supplier} that allows to throw one single kind of {@link Exception}.
 *
 * @param <T> The successful result type of execution {@link ThrowingSupplier#get()}.
 * @param <E> The kind of exception that can happen when executing {@link ThrowingSupplier#get()}.
 * @author Jorge Rico Vivas
 */
public @FunctionalInterface interface ThrowingSupplier<T, E extends Throwable> {
    /**
     * Gets a result value.
     *
     * @return a result value.
     * @throws E an exception.
     */
    T get() throws E;
}