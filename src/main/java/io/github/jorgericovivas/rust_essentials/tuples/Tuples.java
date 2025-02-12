package io.github.jorgericovivas.rust_essentials.tuples;

import io.github.jorgericovivas.rust_essentials.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Tuples are collection of values, where each element of the tuple can have a different type, and they might hold from
 * 0 to 7 values, for example, a tuple that holds 3 values is a {@link Tuple3}.
 * <p>
 * There are two kinds of tuple types, the regular class like {@link Tuple3} where values can be accessed directly
 * through their fields as they are public (Ex: You can get the second values calling {@link Tuple3#v1}), and their
 * record counterparts like {@link Tuple3Record} that are mainly used for pattern matching.
 * <p>
 * If you have the class version you can get its record counterpart using the toRecord or record methods (Ex:
 * {@link Tuple3#toRecord()} and {@link Tuple3#record()}), while the record version can also get the class counterpart
 * with the toClass method (Ex: {@link Tuple3Record#toClass()}).
 * <p>
 * The {@link Tuples} class offers you methods to create tuples without you needing to manually write arity of the
 * tuple, said methods are:
 * <p>
 * - Tuples.of(v0, v1, v2, ..., vN): Gets a tuple where all the values are checked to not be null.
 * <p>
 * - Tuples.of_nullables(v0, v1, v2, ..., vN): Gets a tuple with all the values, not checking their nullability.
 * <p>
 * - Tuples.record(v0, v1, v2, ..., vN): Gets a tuple record where all the values are checked to not be null.
 * <p>
 * - Tuples.record_of_nullables(v0, v1, v2, ..., vN): Gets a tuple record with all the values, not checking their
 * nullability.
 * <p>
 *
 * <br>
 * This is a partial port and Java adaptation of
 * <a href="https://doc.rust-lang.org/std/primitive.tuple.html">Rust's Tuples</a>.
 *
 * <p>Example of use:</p>
 * <pre>
 * {@code
 * var threeValueTuple = Tuples.of("Hello", " ", "world");
 * String textHelloWorld = threeValueTuple.v0 + threeValueTuple.v1 + threeValueTuple.v2;
 * System.out.println("Saying Hello world: " + textHelloWorld);
 * threeValueTuple.v0 = "Goodnight";
 * switch (threeValueTuple.record()){
 *     case Tuple3Record(var prelude, var space, var end) when end.equals("world") ->
 *             System.out.println("Tuple ends with world, and the prelude is: "+prelude);
 *     default -> System.out.println("Sorcery! This wasn't expected");
 * }
 * }
 * </pre>
 *
 * <br>
 * <p>This is specially useful when using a switch with multiple patterns, like shown in the {@link Result} example:</p>
 * <pre>
 * {@code
 * /// Reads two files and returns the contents of the largest file, if one of the files is absent, it returns the
 * /// contents of the other, and if both are absent, an IOException is return.
 * ///
 * /// This uses a syntax and signature that is nature to Rust.
 * public static Result<String, IOException> searchFileRustStyle(Path firstPath, Path secondPath) {
 *     var firstFileRead = Result.checked(() -> Files.readString(firstPath));
 *     var secondFileRead = Result.checked(() -> Files.readString(secondPath));
 *     return switch (Tuples.record(firstFileRead, secondFileRead)) {
 *         case Tuple2Record(Ok(var firstContents), Ok(var secondContents)) ->
 *                 Result.ok(firstContents.length() >= secondContents.length() ? firstContents : secondContents);
 *         case Tuple2Record(Ok(var firstContents), Err<?, ?> ignored) -> Result.ok(firstContents);
 *         case Tuple2Record(Err<?, ?> ignored, Ok(var secondContents)) -> Result.ok(secondContents);
 *         case Tuple2Record(Err(var firstError), var ignored) -> Result.err(firstError);
 *     };
 * }
 * }
 * </pre> *
 *
 */
@SuppressWarnings("unused")
public final class Tuples {

    /**
     * Hidden constructor
     */
    private Tuples(){}

    /**
     * Creates a {@link Tuple0}.
     *
     * @return a {@link Tuple0}.
     */
    public static @NotNull Tuple0 of() {
        return new Tuple0();
    }

    /**
     * Creates a {@link Tuple0}.
     *
     * @return a {@link Tuple0}.
     */
    public static @NotNull Tuple0 of_nullables() {
        return new Tuple0();
    }

    /**
     * Creates a {@link Tuple0}.
     *
     * @return a {@link Tuple0}.
     */
    public static @NotNull Tuple0 record() {
        return new Tuple0();
    }

    /**
     * Creates a {@link Tuple0}.
     *
     * @return a {@link Tuple0}.
     */
    public static @NotNull Tuple0 record_of_nullables() {
        return new Tuple0();
    }

    /**
     * Creates a {@link Tuple1} containing 1 non-null value.
     *
     * @param v0  The value.
     * @param <T> Type of the value.
     * @return a {@link Tuple1} containing 1 non-null value.
     */
    public static <T> @NotNull Tuple1<T>
    of(@NotNull T v0) {
        return new Tuple1<>(requireNonNull(v0));
    }

    /**
     * Creates a {@link Tuple1} containing 1 (possibly null) value.
     *
     * @param v0  The value.
     * @param <T> Type of the value.
     * @return a {@link Tuple1} containing 1 (possibly null) value.
     */
    public static <T> @NotNull Tuple1<T>
    of_nullables(@Nullable T v0) {
        return new Tuple1<>(v0);
    }

    /**
     * Creates a {@link Tuple1Record} containing 1 non-null value.
     *
     * @param v0  The value.
     * @param <T> Type of the value.
     * @return a {@link Tuple1Record} containing 1 non-null value.
     */
    public static <T> @NotNull Tuple1Record<T>
    record(@NotNull T v0) {
        return new Tuple1Record<>(requireNonNull(v0));
    }

    /**
     * Creates a {@link Tuple1Record} containing 1 (possibly null) value.
     *
     * @param v0  The value.
     * @param <T> Type of the value.
     * @return a {@link Tuple1Record} containing 1 (possibly null) value.
     */
    public static <T> @NotNull Tuple1Record<T>
    record_of_nullables(@Nullable T v0) {
        return new Tuple1Record<>(v0);
    }

    /**
     * Creates a {@link Tuple2} containing 2 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @return a {@link Tuple2} containing 2 non-null values.
     */
    public static <T, U> @NotNull Tuple2<T, U>
    of(@NotNull T v0, @NotNull U v1) {
        return new Tuple2<>(requireNonNull(v0), requireNonNull(v1));
    }

    /**
     * Creates a {@link Tuple2} containing 2 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @return a {@link Tuple2} containing 2 (possibly null) values.
     */
    public static <T, U> @NotNull Tuple2<T, U>
    of_nullables(@Nullable T v0, @Nullable U v1) {
        return new Tuple2<>(v0, v1);
    }

    /**
     * Creates a {@link Tuple2Record} containing 2 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @return a {@link Tuple2Record} containing 2 non-null values.
     */
    public static <T, U> @NotNull Tuple2Record<T, U>
    record(@NotNull T v0, @NotNull U v1) {
        return new Tuple2Record<>(requireNonNull(v0), requireNonNull(v1));
    }

    /**
     * Creates a {@link Tuple2Record} containing 2 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @return a {@link Tuple2Record} containing 2 (possibly null) values.
     */
    public static <T, U> @NotNull Tuple2Record<T, U>
    record_of_nullables(@Nullable T v0, @Nullable U v1) {
        return new Tuple2Record<>(v0, v1);
    }

    /**
     * Creates a {@link Tuple3} containing 3 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @return a {@link Tuple3} containing 3 non-null values.
     */
    public static <T, U, V> @NotNull Tuple3<T, U, V>
    of(@NotNull T v0, @NotNull U v1, @NotNull V v2) {
        return new Tuple3<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2));
    }

    /**
     * Creates a {@link Tuple3} containing 3 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @return a {@link Tuple3} containing 3 (possibly null) values.
     */
    public static <T, U, V> @NotNull Tuple3<T, U, V>
    of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2) {
        return new Tuple3<>(v0, v1, v2);
    }

    /**
     * Creates a {@link Tuple3Record} containing 3 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @return a {@link Tuple3Record} containing 3 non-null values.
     */
    public static <T, U, V> @NotNull Tuple3Record<T, U, V>
    record(@NotNull T v0, @NotNull U v1, @NotNull V v2) {
        return new Tuple3Record<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2));
    }

    /**
     * Creates a {@link Tuple3Record} containing 3 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @return a {@link Tuple3Record} containing 3 (possibly null) values.
     */
    public static <T, U, V> @NotNull Tuple3Record<T, U, V>
    record_of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2) {
        return new Tuple3Record<>(v0, v1, v2);
    }

    /**
     * Creates a {@link Tuple4} containing 4 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @return a {@link Tuple4} containing 4 non-null values.
     */
    public static <T, U, V, W> @NotNull Tuple4<T, U, V, W>
    of(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3) {
        return new Tuple4<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3));
    }

    /**
     * Creates a {@link Tuple4} containing 4 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @return a {@link Tuple4} containing 4 (possibly null) values.
     */
    public static <T, U, V, W> @NotNull Tuple4<T, U, V, W>
    of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3) {
        return new Tuple4<>(v0, v1, v2, v3);
    }

    /**
     * Creates a {@link Tuple4Record} containing 4 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @return a {@link Tuple4Record} containing 4 non-null values.
     */
    public static <T, U, V, W> @NotNull Tuple4Record<T, U, V, W>
    record(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3) {
        return new Tuple4Record<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3));
    }

    /**
     * Creates a {@link Tuple4Record} containing 4 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @return a {@link Tuple4Record} containing 4 (possibly null) values.
     */
    public static <T, U, V, W> @NotNull Tuple4Record<T, U, V, W>
    record_of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3) {
        return new Tuple4Record<>(v0, v1, v2, v3);
    }

    /**
     * Creates a {@link Tuple5} containing 5 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @return a {@link Tuple5} containing 5 non-null values.
     */
    public static <T, U, V, W, X> @NotNull Tuple5<T, U, V, W, X>
    of(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4) {
        return new Tuple5<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4));
    }

    /**
     * Creates a {@link Tuple5} containing 5 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @return a {@link Tuple5} containing 5 (possibly null) values.
     */
    public static <T, U, V, W, X> @NotNull Tuple5<T, U, V, W, X>
    of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4) {
        return new Tuple5<>(v0, v1, v2, v3, v4);
    }

    /**
     * Creates a {@link Tuple5Record} containing 5 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @return a {@link Tuple5Record} containing 5 non-null values.
     */
    public static <T, U, V, W, X> @NotNull Tuple5Record<T, U, V, W, X>
    record(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4) {
        return new Tuple5Record<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4));
    }

    /**
     * Creates a {@link Tuple5Record} containing 5 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @return a {@link Tuple5Record} containing 5 (possibly null) values.
     */
    public static <T, U, V, W, X> @NotNull Tuple5Record<T, U, V, W, X>
    record_of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4) {
        return new Tuple5Record<>(v0, v1, v2, v3, v4);
    }

    /**
     * Creates a {@link Tuple6} containing 6 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @return a {@link Tuple6} containing 6 non-null values.
     */
    public static <T, U, V, W, X, Y> @NotNull Tuple6<T, U, V, W, X, Y>
    of(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4, @NotNull Y v5) {
        return new Tuple6<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4), requireNonNull(v5));
    }

    /**
     * Creates a {@link Tuple6} containing 6 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @return a {@link Tuple6} containing 6 (possibly null) values.
     */
    public static <T, U, V, W, X, Y> @NotNull Tuple6<T, U, V, W, X, Y>
    of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4, @Nullable Y v5) {
        return new Tuple6<>(v0, v1, v2, v3, v4, v5);
    }

    /**
     * Creates a {@link Tuple6Record} containing 6 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @return a {@link Tuple6Record} containing 6 non-null values.
     */
    public static <T, U, V, W, X, Y> @NotNull Tuple6Record<T, U, V, W, X, Y>
    record(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4, @NotNull Y v5) {
        return new Tuple6Record<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4), requireNonNull(v5));
    }

    /**
     * Creates a {@link Tuple6Record} containing 6 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @return a {@link Tuple6Record} containing 6 (possibly null) values.
     */
    public static <T, U, V, W, X, Y> @NotNull Tuple6Record<T, U, V, W, X, Y>
    record_of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4, @Nullable Y v5) {
        return new Tuple6Record<>(v0, v1, v2, v3, v4, v5);
    }

    /**
     * Creates a {@link Tuple7} containing 7 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param v6  Seventh value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @param <Z> Seventh value type.
     * @return a {@link Tuple7} containing 7 non-null values.
     */
    public static <T, U, V, W, X, Y, Z> @NotNull Tuple7<T, U, V, W, X, Y, Z>
    of(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4, @NotNull Y v5, @NotNull Z v6) {
        return new Tuple7<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4), requireNonNull(v5),
                requireNonNull(v6));
    }

    /**
     * Creates a {@link Tuple7} containing 7 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param v6  Seventh value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @param <Z> Seventh value type.
     * @return a {@link Tuple7} containing 7 (possibly null) values.
     */
    public static <T, U, V, W, X, Y, Z> @NotNull Tuple7<T, U, V, W, X, Y, Z>
    of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4, @Nullable Y v5, @Nullable Z v6) {
        return new Tuple7<>(v0, v1, v2, v3, v4, v5, v6);
    }

    /**
     * Creates a {@link Tuple7Record} containing 7 non-null values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param v6  Seventh value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @param <Z> Seventh value type.
     * @return a {@link Tuple7Record} containing 7 non-null values.
     */
    public static <T, U, V, W, X, Y, Z> @NotNull Tuple7Record<T, U, V, W, X, Y, Z>
    record(@NotNull T v0, @NotNull U v1, @NotNull V v2, @NotNull W v3, @NotNull X v4, @NotNull Y v5, @NotNull Z v6) {
        return new Tuple7Record<>(requireNonNull(v0), requireNonNull(v1), requireNonNull(v2),
                requireNonNull(v3), requireNonNull(v4), requireNonNull(v5),
                requireNonNull(v6));
    }

    /**
     * Creates a {@link Tuple7Record} containing 7 (possibly null) values.
     *
     * @param v0  First value.
     * @param v1  Second value.
     * @param v2  Third value.
     * @param v3  Fourth value.
     * @param v4  Fifth value.
     * @param v5  Sixth value.
     * @param v6  Seventh value.
     * @param <T> First value type.
     * @param <U> Second value type.
     * @param <V> Third value type.
     * @param <W> Fourth value type.
     * @param <X> Fifth value type.
     * @param <Y> Sixth value type.
     * @param <Z> Seventh value type.
     * @return a {@link Tuple7Record} containing 7 (possibly null) values.
     */
    public static <T, U, V, W, X, Y, Z> @NotNull Tuple7Record<T, U, V, W, X, Y, Z>
    record_of_nullables(@Nullable T v0, @Nullable U v1, @Nullable V v2, @Nullable W v3, @Nullable X v4, @Nullable Y v5, @Nullable Z v6) {
        return new Tuple7Record<>(v0, v1, v2, v3, v4, v5, v6);
    }
}
