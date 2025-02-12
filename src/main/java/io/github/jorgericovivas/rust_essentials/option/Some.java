package io.github.jorgericovivas.rust_essentials.option;

import io.github.jorgericovivas.rust_essentials.result.Ok;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents an existing and non-null value.
 * <p>
 * Example: In searching an index of a {@link java.util.List} whose element matches a {@link Predicate}, the resulting
 * value could be {@link Option}&lt;{@link Integer}&gt;, and when present, the operation would return a
 * {@link Some}&lt;{@link Integer}&gt; containing the index of the value matching this predicate.
 *
 * @param value the existing value.
 * @param <T>   the type of the value.
 * @author Jorge Rico Vivas
 * @see Option
 */
public record Some<T>(@NotNull T value) implements Option<T> {

    /**
     * Default constructor requiring value to not be null.
     *
     * @param value value required not to be null.
     */
    public Some {
        requireNonNull(value);
    }

    /**
     * Returns true.
     *
     * @return true, always.
     */
    @Override
    public boolean isSome() {
        return true;
    }

    /**
     * Returns true if the predicate is met by the value.
     *
     * @param predicate predicated tested against the value if value is Some.
     * @return true if the predicate is met.
     */
    @Override
    public boolean isSomeAnd(@NotNull final Predicate<T> predicate) {
        return requireNonNull(predicate).test(value);
    }

    /**
     * Returns false.
     *
     * @return false, always.
     */
    @Override
    public boolean isNone() {
        return false;
    }

    /**
     * Return a new {@link Some} where the current value as been mapped with the function.
     *
     * @param mapper Maps the original value to another value.
     * @param <U>    New type of option, as a result of mapping {@link Some#value}
     * @return a new {@link Some} where the current value as been mapped with the function.
     */
    @Override
    @NotNull
    public <U> Some<U> map(@NotNull final Function<T, U> mapper) {
        return new Some<>(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns a new {@link Some} where the current value as been mapped with the function.
     *
     * @param defaultValue unused.
     * @param mapper       Maps the original value to another value.
     * @param <U>          New type of option, as a result of mapping {@link Some#value}
     * @return a new {@link Some} where the current value as been mapped with the function.
     */
    @Override
    @NotNull
    public <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns a new {@link Some} where the current value as been mapped with the function.
     *
     * @param defaultValue unused.
     * @param mapper       Maps the original value to another value.
     * @param <U>          New type of option, as a result of mapping {@link Some#value}
     * @return a new {@link Some} where the current value as been mapped with the function.
     */
    @Override
    @NotNull
    public <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper) {
        return requireNonNull(requireNonNull(mapper).apply(value));
    }

    /**
     * Returns an {@link Ok} containing this {@link Some#value}.
     *
     * @param error ignored.
     * @param <E>   type of the error.
     * @return An {@link Ok} containing this {@link Some#value}.
     */
    @Override
    public <E> @NotNull Ok<T, E> okOr(@NotNull final E error) {
        return new Ok<>(value);
    }

    /**
     * Returns an {@link Ok} containing this {@link Some#value}.
     *
     * @param error ignored.
     * @param <E>   type of the error.
     * @return An {@link Ok} containing this {@link Some#value}.
     */
    @Override
    public <E> @NotNull Ok<T, E> okOrElse(@NotNull final Supplier<E> error) {
        return new Ok<>(value);
    }

    /**
     * Executes the function over the contained value.
     *
     * @param inspector consumer function to trigger on the contained value (if Some).
     */
    @Override
    public void inspect(@NotNull final Consumer<T> inspector) {
        requireNonNull(inspector).accept(value);
    }

    /**
     * Returns {@link Some#value}.
     *
     * @return {@link Some#value}
     * @throws IllegalCallerException does never get triggered.
     */
    @Override
    @NotNull
    public T unwrap() throws IllegalCallerException {
        return value;
    }

    /**
     * Returns {@link Some#value}.
     *
     * @param errorMessage unused.
     * @return {@link Some#value}.
     * @throws IllegalCallerException does never get triggered.
     */
    @Override
    @NotNull
    public T expect(@Nullable final String errorMessage) throws IllegalCallerException {
        return value;
    }

    /**
     * Returns {@link Some#value}.
     *
     * @param defaultValue unused.
     * @return {@link Some#value}.
     */
    @Override
    @NotNull
    public T unwrapOr(@NotNull final T defaultValue) {
        return value;
    }

    /**
     * Returns {@link Some#value}.
     *
     * @param defaultValue unused.
     * @return {@link Some#value}.
     */
    @Override
    @NotNull
    public T unwrapOrElse(@NotNull final Supplier<T> defaultValue) {
        return value;
    }

    /**
     * Returns this option if the predicate is met, otherwise it returns a None.
     *
     * @param predicate Condition this Some(value) has to match in order to return itself
     * @return this option if the predicate is met, otherwise it returns a None.
     */
    @Override
    @NotNull
    public Option<T> filter(@NotNull final Predicate<T> predicate) {
        return requireNonNull(predicate).test(value) ? this : new None<>();
    }

    /**
     * Returns the res parameter.
     *
     * @param res The value to return.
     * @param <U> Type of res value.
     * @return the res parameter
     */
    @Override
    @NotNull
    public <U> Option<U> and(@NotNull final Option<U> res) {
        return requireNonNull(res);
    }

    /**
     * Returns the result of applying the function to the value.
     *
     * @param res Generates an Option&lt;U&gt; from the T value.
     * @param <U> Type of res value.
     * @return the result of applying the function to the value.
     */
    @Override
    @NotNull
    public <U> Option<U> andThen(@NotNull final Function<T, Option<U>> res) {
        return requireNonNull(res).apply(value);
    }

    /**
     * Returns this.
     *
     * @param res unused.
     * @return this.
     */
    @Override
    @NotNull
    public Some<T> or(@NotNull final Option<T> res) {
        return this;
    }

    /**
     * Returns this.
     *
     * @param res unused.
     * @return this.
     */
    @Override
    @NotNull
    public Some<T> orElse(@NotNull final Supplier<Option<T>> res) {
        return this;
    }

    /**
     * Returns this if other is none, otherwise, it returns None.
     *
     * @param res the other option value.
     * @return this if other is none, otherwise, it returns None.
     */
    @Override
    @NotNull
    public Option<T> xor(@NotNull final Option<T> res) {
        return requireNonNull(res).isSome() ? new None<>() : this;
    }

}
