package io.github.jorgericovivas.rust_essentials.result;

/**
 * A {@link Runnable} that allows to throw one single kind of {@link Throwable}.
 *
 * @param <E> The kind of exception that can happen when executing {@link ThrowingRunnable#run()}.
 * @author Jorge Rico Vivas
 */
public @FunctionalInterface interface ThrowingRunnable<E extends Throwable> {

    /**
     * Runs an operation that might throw an exception.
     *
     * @throws E an exception.
     */
    void run() throws E;
}