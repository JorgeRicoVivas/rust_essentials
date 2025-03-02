package io.github.jorgericovivas.rust_essentials.result;

import io.github.jorgericovivas.rust_essentials.option.None;
import io.github.jorgericovivas.rust_essentials.option.Option;
import io.github.jorgericovivas.rust_essentials.option.Some;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents the result of the correct execution of an operation; Example: in a file reading, a {@link Result} could be
 * {@link Result}&lt;{@link String}, {@link java.io.IOException}&gt;, where the correct execution would return
 * {@link Ok}&lt;{@link String}&gt;.
 *
 * @param value the result of the correct execution.
 * @param <T>   the type of result of the correct execution.
 * @param <E>   the type of error of a wrong execution.
 * @author Jorge Rico Vivas
 * @see Result
 */
public record Ok<T, E>(@NotNull T value) implements Result<T, E>, Serializable {
    
    /**
     * Default constructor requiring value to not be null.
     *
     * @param value value required not to be null.
     */
    public Ok {
        requireNonNull(value);
    }

    /**
     * Returns true.
     *
     * @return true, always.
     */
    @Override
    public boolean isOk() {
        return true;
    }

    /**
     * Returns whether the {@link Ok#value} matches or not this predicate.
     *
     * @param predicate predicated tested against the value if the result is Ok.
     * @return whether the {@link Ok#value} matches or not this predicate.
     */
    @Override
    public boolean isOkAnd(@NotNull final Predicate<T> predicate) {
        return requireNonNull(predicate).test(value);
    }

    /**
     * Returns false.
     *
     * @return false, always.
     */
    @Override
    public boolean isErr() {
        return false;
    }

    /**
     * Returns false.
     *
     * @param predicate ignored.
     * @return false, always.
     */
    @Override
    public boolean isErrAnd(@NotNull final Predicate<E> predicate) {
        return false;
    }

    /**
     * Returns a {@link Some} containing this {@link Ok#value}.
     *
     * @return a {@link Some} containing this {@link Ok#value}.
     */
    @Override @NotNull
    public Some<T> ok() {
        return Option.some(value);
    }

    /**
     * Returns a {@link None}, this is because {@link Ok} represents a valid result, meaning there is no {@link Err}
     * state.
     *
     * @return A {@link None}.
     */
    @Override @NotNull
    public None<E> err() {
        return new None<>();
    }

    /**
     * Turns this {@link Ok#value} into a new value, which will be contained in a new {@link Ok}.
     *
     * @param mapper Maps the original value to another value.
     * @param <U>    The new type of the conversion.
     * @return A new {@link Ok} with the mapped value.
     */
    @Override @NotNull
    public <U> Ok<U, E> map(@NotNull final Function<T, U> mapper) {
        return new Ok<>(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns the result of applying the mapper to the {@link Ok#value}.
     *
     * @param defaultValue unused.
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          The new type of the conversion.
     * @return the result of applying the mapper to the {@link Ok#value}.
     */
    @Override @NotNull
    public <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns the result of applying the mapper to the {@link Ok#value}.
     *
     * @param defaultValue unused.
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          The new type of the conversion.
     * @return the result of applying the mapper to the {@link Ok#value}.
     */
    @Override @NotNull
    public <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns a new {@link Ok} with the same {@link Ok#value} as this instance, but changing the Error type to that of
     * the conversion.
     *
     * @param errorMapper Maps the original error to another error.
     * @param <O>         The new type of error resulting on the conversion.
     * @return a new {@link Ok} with the same {@link Ok#value} as this instance.
     */
    @Override @NotNull
    public <O> Ok<T, O> mapError(@NotNull final Function<E, O> errorMapper) {
        return new Ok<>(value);
    }

    /**
     * Executes the inspector over the {@link Ok#value}.
     *
     * @param inspector consumer function to trigger on the contained {@link Ok#value}.
     */
    @Override
    public void inspect(@NotNull final Consumer<T> inspector) {
        requireNonNull(inspector).accept(value);
    }

    /**
     * Does nothing
     *
     * @param inspector unused.
     */
    @Override
    public void inspectErr(@NotNull final Consumer<E> inspector) {
    }

    /**
     * Returns the {@link Ok#value}.
     *
     * @return the {@link Ok#value}
     * @throws IllegalCallerException Is never thrown.
     */
    @Override @NotNull
    public T unwrap() throws IllegalCallerException {
        return value;
    }

    /**
     * Returns the {@link Ok#value}.
     *
     * @param errorMessage unused.
     * @return the {@link Ok#value}
     */
    @Override @NotNull
    public T expect(@Nullable final String errorMessage) {
        return value;
    }

    /**
     * Returns the {@link Ok#value}.
     *
     * @param defaultValue unused.
     * @return the {@link Ok#value}
     */
    @Override @NotNull
    public T unwrapOr(@NotNull final T defaultValue) {
        return value;
    }

    /**
     * Returns the {@link Ok#value}.
     *
     * @param defaultValue unused.
     * @return the {@link Ok#value}
     */
    @Override @NotNull
    public T unwrapOrElse(@NotNull final Supplier<T> defaultValue) {
        return value;
    }

    /**
     * Throws an exception specifying {@link Result#unwrapErr()} cannot be executed from a {@link Ok}.
     *
     * @return nothing, it throws the exception.
     * @throws IllegalCallerException an exception specifying {@link Result#unwrapErr()} cannot be executed from a
     *                                {@link Ok}.
     */
    @Override @NotNull
    public E unwrapErr() throws IllegalCallerException {
        throw new IllegalCallerException("called `Result.unwrapErr()` on an `Ok` value");
    }

    /**
     * Throws an exception specifying {@link Result#unwrapErr()} cannot be executed from a {@link Ok}, including the
     * specified reason in errorMessage.
     *
     * @return nothing, it throws the exception.
     * @throws IllegalCallerException an exception specifying {@link Result#unwrapErr()} cannot be executed from a
     *                                {@link Ok}.
     */
    @Override @NotNull
    public E expectErr(@Nullable String errorMessage) throws IllegalCallerException {
        if (errorMessage != null && !errorMessage.isBlank()) {
            errorMessage += System.lineSeparator() + "called `Result.expectErr()` on an `Ok` value";
        } else {
            errorMessage = "called `Result.expectErr()` on an `Ok` value";
        }
        throw new IllegalCallerException(errorMessage);
    }

    /**
     * Returns the value indicated as parameter.
     *
     * @param res the value to return.
     * @param <U> the new Successful type of the Result.
     * @return the res parameter.
     */
    @Override @NotNull
    public <U> Result<U, E> and(@NotNull final Result<U, E> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the {@link Ok} indicated as parameter.
     *
     * @param res the value to return.
     * @param <U> the new Successful type of the Result.
     * @return the res parameter.
     */
    @NotNull
    public <U> Ok<U, E> and(@NotNull final Ok<U, E> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the {@link Err} indicated as parameter.
     *
     * @param res the value to return.
     * @param <U> the new Successful type of the Result.
     * @return the res parameter.
     */
    @NotNull
    public <U> Err<U, E> and(@NotNull final Err<U, E> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the result of applying the function to this {@link Ok}'s {@link Ok#value}.
     *
     * @param res function to apply to this {@link Ok}'s {@link Ok#value}.
     * @param <U> the new Successful type of the Result.
     * @return the result of applying the function to this {@link Ok}'s {@link Ok#value}.
     */
    @Override @NotNull
    public <U> Result<U, E> andThen(@NotNull final Function<T, Result<U, E>> res) {
        return requireNonNull(requireNonNull(res).apply(value));
    }

    /**
     * Returns a new {@link Ok} with this {@link Ok}'s {@link Ok#value}, but changing its Error type.
     *
     * @param res unused.
     * @param <O> the new Error type.
     * @return a new {@link Ok} with this {@link Ok}'s {@link Ok#value},
     */
    @Override @NotNull
    public <O> Ok<T, O> or(@NotNull final Result<T, O> res) {
        return new Ok<>(value);
    }

    /**
     * Returns a new {@link Ok} with this {@link Ok}'s {@link Ok#value}, but changing its Error type.
     *
     * @param res unused.
     * @param <O> the new Error type.
     * @return a new {@link Ok} with this {@link Ok}'s {@link Ok#value},
     */
    @Override @NotNull
    public <O> Ok<T, O> orElse(@NotNull final Function<E, Result<T, O>> res) {
        return new Ok<>(value);
    }
}
