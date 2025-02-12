package io.github.jorgericovivas.rust_essentials.option;

import io.github.jorgericovivas.rust_essentials.result.Err;
import io.github.jorgericovivas.rust_essentials.result.Ok;
import io.github.jorgericovivas.rust_essentials.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents an optional value using two states: {@link Some} if containing a non-null value, or {@link None} if there
 * is no value, this offers a series of advantages over Java's null pointers:
 * <p>
 * - It makes it clearer when a value can be empty, instead of using special values such as -1 or null, as the
 * programmer knows a value might be empty just by looking at the type {@link Option}, for example, if a function
 * returns {@link Option}&lt;{@link Integer}&gt;, they will know the value might be empty, while if the return its just
 * {@link Integer}, then he has to read the documentation for checking for empty values like -1 or null.
 * <p>
 * - You avoid finding {@link NullPointerException}s by surprise in your code, as you are forced to check for emptiness
 * of the value.
 * <p>
 * - It gives functions to safely handle cases where values might be empty while still giving you a concise syntax, many
 * of it expressed with functional programming, giving you functions like {@link Option#map(Function)} or
 * {@link Option#isSomeAnd(Predicate)}.
 * <p>
 *
 * <br>
 * This is a port and Java adaptation of
 * <a href="https://doc.rust-lang.org/stable/std/option/index.html">Rust's Option Type</a>.
 *
 * <p>Example of use:</p>
 * <pre>
 * {@code
 * /// List of 5 female names.
 * public static final List<String> NAMES = Arrays.asList("Alice", "Belle", "Claire", "Diana", "Elena");
 *
 * /// Finds the first name whose initial is the same as the one indicated as parameter, returning None if not found.
 * public static Option<String> findNameByInitial(char initial) {
 *     for (String name : NAMES) {
 *         if (name.charAt(0) == initial) {
 *             return Option.some(name);
 *         }
 *     }
 *     return Option.none();
 * }
 *
 * /// Searches for two names by their initials: 'A' and 'X'
 * public static void main(String[] args) {
 *     // By using a switch with pattern matching, we can easily differentiate whether the value is present or not.
 *     switch (findNameByInitial('A')){
 *         case None() -> System.out.println("No name starts with A");
 *         case Some(var name) -> System.out.println("The first name starting with A is "+name);
 *     }
 *
 *     switch (findNameByInitial('X')){
 *         case None() -> System.out.println("No name starts with X");
 *         case Some(var name) -> System.out.println("The first name starting with X is "+name);
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> Value type.
 * @author Jorge Rico Vivas
 */
public sealed interface Option<T> permits Some, None {

    /**
     * Turns this value to an option, meaning it will be {@link None} if it is a null value, or {@link Some} otherwise.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * final int DEFAULT_WRONG_INDEX = -1;
     *
     * List<String> names = Arrays.asList("Alice", "Belle", "Claire", "Diana", "Elena");
     * Option<Integer> belleIndex = Option.of(names.indexOf("Belle"), DEFAULT_WRONG_INDEX);
     * switch (belleIndex){
     *     case None<Integer>() -> System.out.println("Belle isn't present, so she has no index");
     *     case Some<Integer>(var index) -> System.out.println("Index of Belle in the names list is "+index);
     * }
     * }
     * </pre>
     *
     * @param value         value to turn into {@link Option}.
     * @param specialValues if the value is any of those in the list (This gets checked by calling
     *                      {@link Object#equals(Object)}), then it returns {@link None}.
     * @param <T>           type of the option.
     * @return {@link None} if it is a null value, or {@link Some} otherwise.
     */
    @SafeVarargs
    @NotNull
    static <T> Option<T> of(@Nullable final T value, @Nullable final T... specialValues) {
        if (value == null) {
            return new None<>();
        }
        var isSpecialValue = Arrays.stream(specialValues)
                .filter(Objects::nonNull)
                .anyMatch(value::equals);
        if (isSpecialValue) {
            return new None<>();
        }
        return new Some<>(value);
    }

    /**
     * Turns this {@link Optional} to an {@link Option}, meaning it will be {@link None} if it is a null value or empty,
     * or {@link Some} otherwise.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * List<String> names = Arrays.asList("Alice", "Belle", "Claire", "Diana", "Elena");
     * Optional<String> optionalName = names.stream().filter(name -> name.startsWith("B")).findFirst();
     * Option<String> nameOfB = Option.of(optionalName);
     *
     * switch (nameOfB){
     *     case None<String>()-> System.out.println("The is no name starting with B");
     *     case Some<String>(var name)-> System.out.println("The first name starting with B is "+name);
     * }
     * }
     * </pre>
     *
     * @param value         {@link Optional} value to turn into {@link Option}.
     * @param specialValues if the value is any of those in the list (This gets checked by calling
     *                      {@link Object#equals(Object)}), then it returns {@link None}.
     * @param <T>           type of the option.
     * @return {@link None} if it is a null value, or {@link Some} otherwise.
     */
    @SafeVarargs
    @NotNull
    static <T> Option<T> of(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") @Nullable final Optional<T> value,
            @Nullable final T... specialValues) {
        //noinspection OptionalAssignedToNull
        if (value == null || value.isEmpty()) {
            return new None<>();
        }
        return Option.of(value.get(), specialValues);
    }

    /**
     * Converts from {@link Option}&lt;{@link Option}&lt;T&gt;&gt; to {@link Option}&lt;T&gt;.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Option<Integer>> unflattened = Option.of(Option.of(5));
     * Option<Integer> flattened = Option.flatten(unflattened);
     * assert flattened.unwrap() == 5;
     * }
     * </pre>
     *
     * @param option the option to flatten
     * @param <T>    The internal value type
     * @return A {@link Option}&lt;T&gt;.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T> Option<T> flatten(@NotNull final Option<Option<T>> option) {
        if (requireNonNull(option).isSome()) {
            return requireNonNull(option.unwrap());
        }
        return new None<>();
    }

    /**
     * Transposes an {@link Option} of a {@link Result} into a {@link Result} of an {@link Option}.
     * <p>
     * {@link None} will be mapped to {@link Ok}({@link None}). {@link Some}({@link Ok}(_)) and
     * {@link Some}({@link Err}(_)) will be mapped to {@link Ok}({@link Some}(_)) and {@link Err}(_).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Result<FileInputStream, FileNotFoundException>> possibleInputStream =
     *         Option.of(Result.checked(() -> new FileInputStream("NonExistingFile")));
     *
     * Result<Option<FileInputStream>, FileNotFoundException> transposedPossibleInputStream =
     *         Option.transpose(possibleInputStream);
     * }
     * </pre>
     *
     * @param option The option to transpose.
     * @param <T>    Success type.
     * @param <E>    Error type.
     * @return an {@link Result} of an {@link Option}.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T, E> Result<Option<T>, E> transpose(@NotNull final Option<Result<T, E>> option) {
        requireNonNull(option);
        if (option.isNone()) {
            return new Ok<>(new None<>());
        }
        if (option.unwrap().isOk()) {
            T value = requireNonNull(requireNonNull(option.unwrap()).unwrap());
            return new Ok<>(new Some<>(value));
        }
        E error = requireNonNull(requireNonNull(option.unwrap()).unwrapErr());
        return new Err<>(error);
    }

    /**
     * Turns this value into {@link Some}, and it is the same as using {@link Some}'s default constructor.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Some<Integer> someNumber = Option.some(5);
     * Option<Integer> optionalNumber = Option.some(5);
     * }
     * </pre>
     *
     * @param value value to turn into {@link Some}
     * @param <T>   type of the option.
     * @return A {@link Some} value.
     */
    @NotNull
    static <T> Some<T> some(@NotNull final T value) {
        return new Some<>(requireNonNull(value));
    }

    /**
     * Creates a {@link None}, and it is the same as using {@link None}'s default constructor.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * None<Integer> noneNumber = Option.none();
     * Option<Integer> optionalNumber = Option.none();
     * }
     * </pre>
     *
     * @param <T> type of the option.
     * @return A {@link None} value.
     */
    @NotNull
    static <T> None<T> none() {
        return new None<>();
    }

    /**
     * Returns true if the option is a Some value.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * boolean thisIsAlwaysTrue = Option.of(5).isSome();
     * boolean thisIsAlwaysFalse = Option.of(null).isSome();
     * }
     * </pre>
     *
     * @return true if the option is a Some value.
     */
    boolean isSome();

    /**
     * Returns true if the option is a Some and the value inside of it matches a predicate.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * boolean thisMightBeTrue = Option.of(5).isSomeAnd(num->num>0);
     * boolean thisIsAlwaysFalse = Option.of((Integer) null).isSomeAnd(num->num>0);
     * }
     * </pre>
     *
     * @param predicate predicated tested against the value if value is Some.
     * @return true if the option is a Some and the value inside of it matches a predicate.
     */
    @SuppressWarnings("unused")
    boolean isSomeAnd(@NotNull final Predicate<T> predicate);

    /**
     * Returns true if the option is a None value.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * boolean thisIsAlwaysFalse = Option.of(5).isNone();
     * boolean thisIsAlwaysTrue = Option.of(null).isNone();
     * }
     * </pre>
     *
     * @return true if the option is a None value.
     */
    boolean isNone();

    /**
     * Maps an Option&lt;T&gt; to Option&lt;U&gt; by applying a function to a contained value (if Some) or returns
     * None (if None).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.of(5);
     * Option<Float> possibleFloat = possibleInt.map(Float::valueOf);
     * }
     * </pre>
     *
     * @param mapper Maps the original value to another value.
     * @param <U>    Type T transforms to.
     * @return Option&lt;T&gt; transformed to Option&lt;U&gt;, where T transformed into U using mapper.
     */
    @NotNull
    <U> Option<U> map(@NotNull final Function<T, U> mapper);

    /**
     * Returns the provided default result (if none), or applies a function to the contained value (if any).
     * <p>
     * Arguments passed to mapOr are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use mapOrElse, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.of(5);
     * Float floatingNumber = possibleInt.mapOr(0.0f, Float::valueOf);
     * }
     * </pre>
     *
     * @param defaultValue a provided default which will be returned if this Option is None.
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          Type T transforms to as a return result.
     * @return Value of the transformation from T to U if Option is Some(value), otherwise, it returns the default
     * value.
     */
    @NotNull
    <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper);

    /**
     * Computes a default function result (if none), or applies a different function to the contained value (if any).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.of(5);
     * Float floatingNumber = possibleInt.mapOrElse(()->0.0f, Float::valueOf);
     * }
     * </pre>
     *
     * @param defaultValue a provided supplier which results in default value which will be calculated and returned if
     *                     this Option is None.
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          Type T transforms to as a return result.
     * @return Value of the transformation from T to U if Option is Some(value), otherwise, it calculates and returns
     * the default value from the supplier.
     */
    @NotNull
    <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper);

    /**
     * Transforms the Option&lt;T&gt; into a Result&lt;T, E&gt;, mapping Some(v) to Ok(v) and None to Err(err).
     * <p>
     * Arguments passed to okOr are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use okOrElse, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.none();
     * Result<Integer, RuntimeException> floatingNumber =
     *         possibleInt.okOr(new RuntimeException("There is no number"));
     * }
     * </pre>
     *
     * @param error error to transform into Result.Error if this Option is None
     * @param <E>   Error type parameter.
     * @return Result.Ok(value) if this option is Some(value), otherwise it returns Result.Error(error), where error is
     * a parameter.
     */
    @SuppressWarnings("unused")
    @NotNull
    <E> Result<T, E> okOr(@NotNull final E error);

    /**
     * Transforms the Option&lt;T&gt; into a Result&lt;T, E&gt;, mapping Some(v) to Ok(v) and None to Err(err()).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.none();
     * Result<Integer, RuntimeException> floatingNumber =
     *         possibleInt.okOrElse(() -> new RuntimeException("There is no number"));
     * }
     * </pre>
     *
     * @param error error to transform into Result.Error if this Option is None, this is a {@link Supplier}, meaning it
     *              is only calculated if this Option is None.
     * @param <E>   Error type parameter.
     * @return Result.Ok(value) if this option is Some(value), otherwise it returns Result.Error(error), where error is
     * a parameter whose value gets calculated (Only if Option is None).
     */
    @SuppressWarnings("unused")
    @NotNull
    <E> Result<T, E> okOrElse(@NotNull final Supplier<E> error);

    /**
     * Calls the provided {@link Consumer} on the contained value (if Some).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.some(5);
     * possibleInt.inspect(num-> System.out.println("There is a number valued as "+num));
     * }
     * </pre>
     *
     * @param inspector consumer function to trigger on the contained value (if Some).
     */
    void inspect(@NotNull final Consumer<T> inspector);

    /**
     * Returns the contained Some value.
     * <p>
     * Because this function may throw a IllegalCallerException, its use is generally discouraged. Instead, prefer to
     * use pattern matching and handle the None case explicitly, or call either unwrap_or or unwrap_or_else.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.some(5);
     * int number = possibleInt.unwrap();
     * }
     * </pre>
     *
     * @return the contained Some value.
     * @throws IllegalCallerException if the value is None.
     */
    @NotNull
    T unwrap() throws IllegalCallerException;

    /**
     * Returns the contained Some value.
     * <p>
     * Throws a IllegalCallerException if the value is a None with a custom panic message provided by errorMessage.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.some(5);
     * int number = possibleInt.expect("No number found, this is an error");
     * }
     * </pre>
     *
     * @param errorMessage Error message to include on the Runtime Exception on case it is triggered.
     * @return the contained Some value
     * @throws IllegalCallerException if the value is a None, the message error will include an error message provided
     *                                and the passed error message.
     */
    @NotNull
    T expect(@Nullable final String errorMessage) throws IllegalCallerException;

    /**
     * Returns the contained Some value or a provided default.
     * <p>
     * Arguments passed to unwrapOr are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use unwrapOrElse, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.some(5);
     * int number = possibleInt.unwrapOr(0);
     * }
     * </pre>
     *
     * @param defaultValue a provided default which will be returned if this Option is None.
     * @return the contained Some value or a provided default.
     */
    @NotNull
    T unwrapOr(@NotNull final T defaultValue);

    /**
     * Returns the contained Some value or computes it from a {@link Supplier}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> possibleInt = Option.some(5);
     * int number = possibleInt.unwrapOrElse(() -> 0);
     * }
     * </pre>
     *
     * @param defaultValue a provided default value getter whose value will be calculated and returned if this Option is
     *                     None.
     * @return contained value if Option is Some(value), otherwise, calculates and returns the default value from the
     * supplier.
     */
    @NotNull
    T unwrapOrElse(@NotNull final Supplier<T> defaultValue);

    /**
     * Returns None if the option is None, otherwise calls predicate with the wrapped value and returns:
     * <p>
     * - Some(T) if predicate returns true (where T is the wrapped value).
     * - None if predicate returns false.
     * <p>
     * This function works similar to Rust's Iterator::filter(). You can imagine the Option&lt;T&gt; being an iterator
     * over one or zero elements. filter() lets you decide which elements to keep.
     *
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> bigValue = Option.some(15);
     * Option<Integer> thisIsSome = bigValue.filter(num->num>10);
     *
     * Option<Integer> smallValue = Option.some(5);
     * Option<Integer> thisIsNone = smallValue.filter(num->num>10);
     * }
     * </pre>
     *
     * @param predicate Condition this Some(value) has to match in order to return itself
     * @return returns this if is Some(value) and the value matches the predicate, otherwise, it returns None.
     */
    @NotNull
    Option<T> filter(@NotNull final Predicate<T> predicate);

    /**
     * Returns None if the option is None, otherwise returns res.
     * <p>
     * Arguments passed to and are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use and_then, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> five = Option.of(5);
     * Option<Integer> six = Option.of(6);
     * Option<Integer> none = Option.of(null);
     *
     * Option<Integer> thisIsSix = five.and(six);
     * Option<Integer> thisIsNone = five.and(none);
     * Option<Integer> thisIsNoneToo = none.and(five);
     * Option<Integer> thisIsNoneAlso = none.and(none);
     * }
     * </pre>
     *
     * @param res The other Option whose contents are returned if this Option is Some.
     * @param <U> Value type of the latter Option.
     * @return res if the Option is Some, otherwise None.
     */
    @NotNull
    <U> Option<U> and(@NotNull final Option<U> res);

    /**
     * Returns None if the option is None, otherwise calls f with the wrapped value and returns the result.
     * <p>
     * This is similar to calling {@link Option#filter(Predicate)} and {@link Option#map(Function)}.
     * <p>
     * Some languages call this operation flatmap.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> none = Option.none();
     * Option<Integer> five = Option.of(5);
     * Option<Integer> minusFive = Option.of(-5);
     *
     * Function<Integer, Option<Integer>> filterPositiveNumbers =
     *         num -> num >= 0 ? Option.some(num) : Option.none();
     *
     * Option<Integer> thisIsNone = none.andThen(filterPositiveNumbers);
     * Option<Integer> thisIsFive = five.andThen(filterPositiveNumbers);
     * Option<Integer> thisIsAlsoNone = minusFive.andThen(filterPositiveNumbers);
     * }
     * </pre>
     *
     * @param res Generates an Option&lt;U&gt; from the T value.
     * @param <U> Value type of the produced Option if this Option is Some.
     * @return Result with mapped value if Option was Some, otherwise None.
     */
    @NotNull
    <U> Option<U> andThen(@NotNull final Function<T, Option<U>> res);

    /**
     * Returns the option if it contains a value, otherwise returns res.
     * <p>
     * Arguments passed to or are eagerly evaluated; if you are passing the result of a function call, it is recommended
     * to use orElse, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> five = Option.of(5);
     * Option<Integer> six = Option.of(6);
     * Option<Integer> none = Option.of(null);
     *
     * Option<Integer> thisIsFive = five.or(six);
     * Option<Integer> thisIsFiveToo = none.or(five);
     * Option<Integer> thisIsFiveAlso = five.or(none);
     * Option<Integer> thisIsNone = none.or(none);
     * }
     * </pre>
     *
     * @param res The other Option whose contents are returned if this Option is None.
     * @return This option if it contains a value, otherwise returns res.
     */
    @NotNull
    Option<T> or(@NotNull final Option<T> res);

    /**
     * Returns the option if it contains a value, otherwise calls the {@link Supplier} and returns the result.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> five = Option.of(5);
     * Option<Integer> six = Option.of(6);
     * Option<Integer> none = Option.of(null);
     *
     * Option<Integer> thisIsFive = five.orElse(()->six);
     * Option<Integer> thisIsFiveToo = none.orElse(()->five);
     * Option<Integer> thisIsFiveAlso = five.orElse(()->none);
     * Option<Integer> thisIsNone = none.orElse(()->none);
     * }
     * </pre>
     *
     * @param res Supplier resolving in another Option whose contents are returned if this Option is None.
     * @return this option if it contains a value, otherwise calls {@link Supplier} and returns the result.
     */
    @NotNull
    Option<T> orElse(@NotNull final Supplier<Option<T>> res);

    /**
     * Returns Some if exactly one of self, res is Some, otherwise returns None.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Option<Integer> five = Option.of(5);
     * Option<Integer> six = Option.of(6);
     * Option<Integer> none = Option.of(null);
     *
     * Option<Integer> thisIsNone = five.xor(six);
     * Option<Integer> thisIsFive = none.xor(five);
     * Option<Integer> thisIsFiveToo = five.xor(none);
     * Option<Integer> thisIsNoneToo = none.xor(none);
     * }
     * </pre>
     *
     * @param res The other Option whose contents are returned if this Option is None and res is Some.
     * @return Some if exactly one of self, res is Some, otherwise returns None.
     */
    @SuppressWarnings("unused")
    @NotNull
    Option<T> xor(@NotNull final Option<T> res);


}
