package io.github.jorgericovivas.rust_essentials.option;

import io.github.jorgericovivas.rust_essentials.result.Err;
import io.github.jorgericovivas.rust_essentials.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents an empty value, as it was a 'null' value;
 * <p>
 * Example: In searching an index of a {@link java.util.List} whose element matches a {@link Predicate}, the resulting
 * value could be {@link Option}&lt;{@link Integer}&gt;, and when not present, the operation would return a
 * {@link None}&lt;{@link Integer}&gt; containing no value, indicating the programmer the operation didn't find any
 * value.
 * <p>
 * This is clearer than using a special value to indicate errors, like -1 would have been, but this way, the programmer
 * doesn't need to remember these special values, and if you want to add information about why an operation couldn't be
 * executed, you can use {@link Err} instead to follow the pattern of {@link Result}
 * instead of that of {@link Option}.
 *
 * @param <T> the type of the value.
 * @author Jorge Rico Vivas
 * @see Option
 */
public record None<T>() implements Option<T>, Serializable {

    /**
     * Returns false.
     *
     * @return false, always.
     */
    @Override
    public boolean isSome() {
        return false;
    }

    /**
     * Returns false.
     *
     * @param predicate unused.
     * @return false, always.
     */
    @Override
    public boolean isSomeAnd(final @NotNull Predicate<T> predicate) {
        return false;
    }

    /**
     * Returns true.
     *
     * @return true, always.
     */
    @Override
    public boolean isNone() {
        return true;
    }

    /**
     * Returns a None with the U type.
     *
     * @param mapper unused.
     * @param <U>    new type of Option.
     * @return a new {@link None}.
     */
    @Override
    @NotNull
    public <U> None<U> map(@NotNull final Function<T, U> mapper) {
        return new None<>();
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @param mapper       unused.
     * @param <U>          type of the result value.
     * @return the default value.
     */
    @Override
    @NotNull
    public <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(defaultValue);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @param mapper       unused.
     * @param <U>          type of the result value.
     * @return the default value.
     */
    @Override
    @NotNull
    public <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(defaultValue).get());
    }

    /**
     * Returns the error value wrapped in an {@link Err}.
     *
     * @param <E> Type of the error.
     * @return The error value wrapped in an {@link Err}.
     */
    @Override
    @NotNull
    public <E> Err<T, E> okOr(@NotNull final E error) {
        return new Err<>(error);
    }

    /**
     * Returns the error value wrapped in an {@link Err}.
     *
     * @param <E> Type of the error.
     * @return The error value wrapped in an {@link Err}.
     */
    @Override
    @NotNull
    public <E> Err<T, E> okOrElse(@NotNull final Supplier<E> error) {
        return new Err<>(requireNonNull(requireNonNull(error).get()));
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
     * Fails to return a value as this is a {@link None}, throwing a {@link IllegalCallerException} explaining this.
     *
     * @return Never returns a value.
     * @throws IllegalCallerException Always returns this exception telling it tried to execute unwrap on a none.
     */
    @Override
    @NotNull
    public T unwrap() throws IllegalCallerException {
        throw new IllegalCallerException("called `Option.unwrap()` on a `None` value");
    }

    /**
     * Fails to return a value as this is a {@link None}, throwing a {@link IllegalCallerException} explaining this along the
     * error message.
     *
     * @param errorMessage Error message to include on the Runtime Exception on case it is triggered.
     * @return Never returns a value.
     * @throws IllegalCallerException Always returns this exception telling it tried to execute unwrap on a none.
     */
    @Override
    @NotNull
    public T expect(@Nullable String errorMessage) throws IllegalCallerException {
        if (errorMessage != null && !errorMessage.isBlank()) {
            errorMessage += System.lineSeparator() + "called `Option.unwrap()` on a `None` value";
        } else {
            errorMessage = "called `Option.unwrap()` on a `None` value";
        }
        throw new IllegalCallerException(errorMessage);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @return value to return.
     */
    @Override
    @NotNull
    public T unwrapOr(@NotNull final T defaultValue) {
        return requireNonNull(defaultValue);
    }

    /**
     * Returns the default value.
     *
     * @param defaultValue value to return.
     * @return value to return.
     */
    @Override
    @NotNull
    public T unwrapOrElse(@NotNull final Supplier<T> defaultValue) {
        return requireNonNull(requireNonNull(defaultValue).get());
    }

    /**
     * Always returns this None.
     *
     * @param predicate unused.
     * @return this none.
     */
    @Override
    @NotNull
    public None<T> filter(@NotNull final Predicate<T> predicate) {
        return this;
    }

    /**
     * Returns a new None with a mapped type.
     *
     * @param res unused.
     * @param <U> The type of the new Option.
     * @return a new None.
     */
    @Override
    @NotNull
    public <U> None<U> and(@NotNull final Option<U> res) {
        return new None<>();
    }

    /**
     * Returns a new None with a mapped type.
     *
     * @param res unused.
     * @param <U> The type of the new Option.
     * @return a new None.
     */
    @Override
    @NotNull
    public <U> None<U> andThen(@NotNull final Function<T, Option<U>> res) {
        return new None<>();
    }

    /**
     * Returns the other option.
     *
     * @param res value to return.
     * @return the other option.
     */
    @Override
    @NotNull
    public Option<T> or(@NotNull final Option<T> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the other option.
     *
     * @param res value to return.
     * @return the other option.
     */
    @Override
    @NotNull
    public Option<T> orElse(@NotNull final Supplier<Option<T>> res) {
        return requireNonNull(requireNonNull(res).get());
    }

    /**
     * Returns the other value if it is some, otherwise, it returns this None.
     *
     * @param res value to return if it is Some.
     * @return the other value if it is some, otherwise, it returns this None.
     */
    @Override
    @NotNull
    public Option<T> xor(@NotNull final Option<T> res) {
        if (requireNonNull(res).isSome()) {
            return requireNonNull(res);
        } else {
            return this;
        }
    }
}
