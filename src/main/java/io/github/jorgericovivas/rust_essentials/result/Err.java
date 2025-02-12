package io.github.jorgericovivas.rust_essentials.result;

import io.github.jorgericovivas.rust_essentials.option.None;
import io.github.jorgericovivas.rust_essentials.option.Option;
import io.github.jorgericovivas.rust_essentials.option.Some;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents the error of a wrong execution of an operation; Example: in a file reading, a {@link Result} could be
 * {@link Result}&lt;{@link String}, {@link java.io.IOException}&gt;, where failing would return {@link Err}
 * &lt;{@link java.io.IOException}&gt;.
 *
 * @param error the error value of a wrong execution.
 * @param <T>   the type of result of the correct execution.
 * @param <E>   the type of error of a wrong execution.
 * @author Jorge Rico Vivas
 * @see Result
 */

public record Err<T, E>(@NotNull E error) implements Result<T, E> {

    /**
     * Default constructor requiring error to not be null.
     *
     * @param error value required not to be null.
     */
    public Err {
        requireNonNull(error);
    }

    /**
     * Returns false.
     *
     * @return false, always.
     */
    @Override
    public boolean isOk() {
        return false;
    }

    /**
     * Returns false.
     *
     * @return false, always.
     */
    @Override
    public boolean isOkAnd(@NotNull final Predicate<T> predicate) {
        return false;
    }

    /**
     * Returns true.
     *
     * @return true, always.
     */
    @Override
    public boolean isErr() {
        return true;
    }

    /**
     * Returns whether the {@link Err#error} matches or not this predicate.
     *
     * @param predicate predicated tested against the value if the result is Error.
     * @return whether the {@link Err#error} matches or not this predicate.
     */
    @Override
    public boolean isErrAnd(@NotNull final Predicate<E> predicate) {
        return requireNonNull(predicate).test(error);
    }

    /**
     * Returns a {@link None}, this is because {@link Err} represents an invalid result, meaning there is no
     * {@link Ok} state.
     *
     * @return A {@link None}.
     */
    @Override @NotNull
    public None<T> ok() {
        return new None<>();
    }

    /**
     * Returns a {@link Some} containing this {@link Err#error}.
     *
     * @return a {@link Some} containing this {@link Err#error}.
     */
    @Override @NotNull
    public Some<E> err() {
        return Option.some(error);
    }

    /**
     * Returns a new {@link Err} with the same {@link Err#error} as this instance, but changing the success type to
     * that of the conversion.
     *
     * @param mapper Maps the original success value to another success type.
     * @param <U>    The new type of the success value resulting on the conversion.
     * @return a new {@link Err} with the same {@link Err#error} as this instance.
     */
    @Override @NotNull
    public <U> Err<U, E> map(@NotNull final Function<T, U> mapper) {
        return new Err<>(error);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue the value to return.
     * @param mapper       unused.
     * @param <U>          the type of the default value.
     * @return the default value.
     */
    @Override @NotNull
    public <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(defaultValue);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue the value to return.
     * @param mapper       unused.
     * @param <U>          the type of the default value.
     * @return the default value.
     */
    @Override @NotNull
    public <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(defaultValue).get());
    }

    /**
     * Turns this {@link Err#error} into a new error, which will be contained in a new {@link Err}.
     *
     * @param errorMapper Maps the original {@link Err#error}  to another value.
     * @param <O>         The new error type of the conversion.
     * @return A new {@link Err} with the mapped value.
     */
    @Override @NotNull
    public <O> Err<T, O> mapError(@NotNull final Function<E, O> errorMapper) {
        return new Err<>(requireNonNull(requireNonNull(errorMapper).apply(error)));
    }

    /**
     * Does nothing.
     *
     * @param inspector unused.
     */
    @Override
    public void inspect(@NotNull final Consumer<T> inspector) {

    }

    /**
     * Executes the inspector over the {@link Err#error}.
     *
     * @param inspector consumer function to trigger on the contained {@link Err#error}.
     */
    @Override
    public void inspectErr(@NotNull final Consumer<E> inspector) {
        requireNonNull(inspector).accept(error);
    }

    /**
     * Throws an exception specifying {@link Result#unwrap()} cannot be executed from a {@link Err}.
     *
     * @return nothing, it throws the exception.
     * @throws IllegalCallerException an exception specifying {@link Result#unwrap()} cannot be executed from a
     *                                {@link Err}.
     */
    @Override @NotNull
    public T unwrap() throws IllegalCallerException {
        IllegalCallerException illegalCallerException;
        if (error instanceof Throwable thrown) {
            illegalCallerException = new IllegalCallerException("called `Result.unwrap()` on an `Err` value", thrown);
        } else {
            illegalCallerException = new IllegalCallerException("called `Result.unwrap()` on an `Err` value");
        }
        throw illegalCallerException;
    }

    /**
     * Throws an exception specifying {@link Result#expect(String)} cannot be executed from a {@link Err}, including
     * the specified reason in errorMessage.
     *
     * @return nothing, it throws the exception.
     * @throws IllegalCallerException an exception specifying {@link Result#expect(String)} cannot be executed from a
     *                                {@link Ok}.
     */
    @Override @NotNull
    public T expect(@Nullable String errorMessage) {
        if (errorMessage != null && !errorMessage.isBlank()) {
            errorMessage += System.lineSeparator() + "called `Result.expect()` on an `Error` value";
        } else {
            errorMessage = "called `Result.expect()` on an `Error` value";
        }
        IllegalCallerException illegalCallerException;
        if (error instanceof Throwable thrown) {
            illegalCallerException = new IllegalCallerException(errorMessage, thrown);
        } else {
            illegalCallerException = new IllegalCallerException(errorMessage);
        }
        throw illegalCallerException;
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @return defaultValue.
     */
    @Override @NotNull
    public T unwrapOr(@NotNull final T defaultValue) {
        return requireNonNull(defaultValue);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @return defaultValue.
     */
    @Override @NotNull
    public T unwrapOrElse(@NotNull final Supplier<T> defaultValue) {
        return requireNonNull(requireNonNull(defaultValue).get());
    }

    /**
     * Returns this {@link Err#error} value.
     *
     * @return this {@link Err#error} value.
     * @throws IllegalCallerException it is never thrown.
     */
    @Override @NotNull
    public E unwrapErr() throws IllegalCallerException {
        return error;
    }

    /**
     * Returns this {@link Err#error} value.
     *
     * @param errorMessage unused.
     * @return this {@link Err#error} value.
     * @throws IllegalCallerException it is never thrown.
     */
    @Override @NotNull
    public E expectErr(@Nullable final String errorMessage) throws IllegalCallerException {
        return error;
    }

    /**
     * Returns a new {@link Err} containing this {@link Err#error}, but with the success type of U.
     *
     * @param res unused.
     * @param <U> new success type of the {@link Err}.
     * @return a new {@link Err} containing this {@link Err#error}, but with the success type of U.
     */
    @Override @NotNull
    public <U> Err<U, E> and(@NotNull final Result<U, E> res) {
        return new Err<>(error);
    }

    /**
     * Returns a new {@link Err} containing this {@link Err#error}, but with the success type of U.
     *
     * @param res unused.
     * @param <U> new success type of the {@link Err}.
     * @return a new {@link Err} containing this {@link Err#error}, but with the success type of U.
     */
    @Override @NotNull
    public <U> Err<U, E> andThen(@NotNull final Function<T, Result<U, E>> res) {
        return new Err<>(error);
    }

    /**
     * Returns the res parameter.
     *
     * @param res The {@link Result} value to return.
     * @param <O> The Error type of said {@link Result}.
     * @return The res parameter.
     */
    @Override @NotNull
    public <O> Result<T, O> or(@NotNull final Result<T, O> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the res parameter.
     *
     * @param res The {@link Ok} value to return.
     * @param <O> The Error type of said {@link Ok}.
     * @return The res parameter.
     */
    @NotNull
    public <O> Ok<T, O> or(@NotNull final Ok<T, O> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the res parameter.
     *
     * @param res The {@link Err} value to return.
     * @param <O> The Error type of said {@link Err}.
     * @return The res parameter.
     */
    @NotNull
    public <O> Err<T, O> or(@NotNull final Err<T, O> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the result of applying said function to this {@link Err#error}.
     *
     * @param res mapper function that turns this {@link Err#error} into a new {@link Result}.
     * @param <O> The Error type of said {@link Ok}.
     * @return the result of applying said function to this {@link Err#error}.
     */
    @Override @NotNull
    public <O> Result<T, O> orElse(@NotNull final Function<E, Result<T, O>> res) {
        return requireNonNull(requireNonNull(res).apply(error));
    }
}
