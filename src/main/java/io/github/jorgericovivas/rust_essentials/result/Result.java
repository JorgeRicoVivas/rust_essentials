package io.github.jorgericovivas.rust_essentials.result;

import io.github.jorgericovivas.rust_essentials.option.None;
import io.github.jorgericovivas.rust_essentials.option.Option;
import io.github.jorgericovivas.rust_essentials.option.Some;
import io.github.jorgericovivas.rust_essentials.tuples.Tuple0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A type used for returning and propagating errors. It represents two possible states: <p>
 * - {@link Ok}(T), representing success and containing a non-null value.<p>
 * - {@link Err}(E), representing error and containing a non-null error value.
 * <p>
 * This offers a series of advantages over Java's error handling system:
 * <p>
 * - Inheritance isn't needed, as 'throw' clauses required the error state to extend {@link Throwable}, allowing you to
 * not use inheritance for your error classes, or to extend something else that isn't a child of {@link Throwable}.
 * <p>
 * - You can handle both the success and the error states at the same time, rather than having to create intermediary
 * code to store an optional T and an optional E.
 * <p>
 * - Syntax is clearer, as you don't need to write the try {...} catch (...) {...} blocks, nor the keywords 'throws' or
 * 'throw' are required, and most of the control can be handled with pattern matching in 'switch' statements or / and
 * using a functional style with functions like {@link Result#map(Function)} or {@link Result#isOkAnd(Predicate)}.
 * <p>
 * - Maintainability is simplified, as many of vanilla Java's pattern can be suppressed, resulting in code that is
 * easier to read, written in less lines, and easier to change; An example of this can be found below where a function
 * that can throw multiple IOExceptions is implemented both in the traditional Java style, and in this Rust-based style.
 * <p>
 *
 * <br>
 * This is a port and Java adaptation of
 * <a href="https://doc.rust-lang.org/std/result/index.html">Rust's Result Type</a>.
 *
 * <p>Example of use:</p>
 * <pre>
 * {@code
 * /// List of 5 female names.
 * public static final List<String> NAMES = Arrays.asList("Alice", "Belle", "Claire", "Diana", "Elena");
 *
 * /// Finds the first name whose initial is the same as the one indicated as parameter, returning a NameNotFound when
 * /// no name is found.
 * public static Result<String, NameNotFound> findNameByInitial(char initial) {
 *     for (String name : NAMES) {
 *         if (name.charAt(0) == initial) {
 *             return Result.ok(name);
 *         }
 *     }
 *     return Result.err(new NameNotFound(initial));
 * }
 *
 * /// A record representing an error state where no name matches an initial.
 * public record NameNotFound(char initial) {
 *     @Override public String toString() {
 *         return "There is no name by initial " + initial;
 *     }
 * }
 *
 * /// Searches for two names by their initials: 'A' and 'X'
 * public static void main(String[] args) {
 *     // By using a switch with pattern matching, we can easily
 *     // differentiate whether the operation is successful or not.
 *     switch (findNameByInitial('A')) {
 *         case Error(var nameNotFound) -> System.out.println(nameNotFound);
 *         case Ok(var name) -> System.out.println("The first name starting with A is " + name);
 *     }
 *
 *     switch (findNameByInitial('X')) {
 *         case Error(var nameNotFound) -> System.out.println(nameNotFound);
 *         case Ok(var name) -> System.out.println("The first name starting with X is " + name);
 *     }
 * }
 * }
 * </pre>
 *
 *
 * <p>Comparison with plain Java:</p>
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
 *
 *
 * /// Reads two files and returns the contents of the largest file, if one of the files is absent, it returns the
 * /// contents of the other, and if both are absent, an IOException is thrown.
 * ///
 * /// This uses a syntax that is nature to Rust, yet, the signature of the method is aligned to Java.
 * private static String searchFileRustStyleAndJavaSignature(Path firstPath, Path secondPath) throws IOException {
 *     var firstFileRead = Result.checked(() -> Files.readString(firstPath));
 *     var secondFileRead = Result.checked(() -> Files.readString(secondPath));
 *     Result<String, IOException> contents = switch (Tuples.record(firstFileRead, secondFileRead)) {
 *         case Tuple2Record(Ok(var firstContents), Ok(var secondContents)) ->
 *                 Result.ok(firstContents.length() >= secondContents.length() ? firstContents : secondContents);
 *         case Tuple2Record(Ok(var firstContents), Err<?, ?> ignored) -> Result.ok(firstContents);
 *         case Tuple2Record(Err<?, ?> ignored, Ok(var secondContents)) -> Result.ok(secondContents);
 *         case Tuple2Record(Err(var firstError), var ignored) -> Result.err(firstError);
 *     };
 *     return Result.unwrap_or_throw(contents);
 * }
 *
 *
 * /// Reads two files and returns the contents of the largest file, if one of the files is absent, it returns the
 * /// contents of the other, and if both are absent, an IOException is thrown.
 * ///
 * /// This uses the vanilla Java syntax.
 * private static String searchFileJavaStyle(Path firstPath, Path secondPath) throws IOException {
 *     String firstContents = null, secondContents = null;
 *     IOException firstError = null, secondError = null;
 *     try {
 *         firstContents = Files.readString(firstPath);
 *     } catch (IOException e) {
 *         firstError = e;
 *     }
 *     try {
 *         secondContents = Files.readString(secondPath);
 *     } catch (IOException e) {
 *         secondError = e;
 *     }
 *     if (firstContents != null && secondContents != null) {
 *         return firstContents.length() >= secondContents.length() ? firstContents : secondContents;
 *     } else if (firstContents != null) {
 *         return firstContents;
 *     } else if (secondContents != null) {
 *         return secondContents;
 *     } else {
 *         throw firstError;
 *     }
 * }
 *
 *
 * /// Executes the searching methods created earlier over a set of couples of files.
 * ///
 * /// All the methods result in the same outputs for the same files.
 * public static void main(String[] args) {
 *     List<Tuple2Record<String, String>> testSampleFiles = Arrays.asList(
 *             new Tuple2Record<>("long_file.txt", "short_file.txt"),
 *             new Tuple2Record<>("short_file.txt", "non_existing_file.txt"),
 *             new Tuple2Record<>("non_existing_file.txt", "non_existing_file.txt")
 *     );
 *     for (var sampleFiles : testSampleFiles) {
 *         System.out.println("- Reading files " + sampleFiles.v0() + " and " + sampleFiles.v1() + " -");
 *         var firstFilePath = Path.of(sampleFiles.v0());
 *         var secondFilePath = Path.of(sampleFiles.v1());
 *         // Tests the classic Rust version.
 *         System.out.println("Res of classic Rust " + searchFileRustStyle(firstFilePath, secondFilePath));
 *         // Tests the classic Rust version but where the method signature feels like a classic Java method.
 *         try {
 *             System.out.println("Res of classic Rust with Java signature " + searchFileRustStyleAndJavaSignature(firstFilePath, secondFilePath));
 *         } catch (IOException e) {
 *             System.out.println("Res of classic Rust with Java signature " + e);
 *         }
 *         // Tests the classic Java method.
 *         try {
 *             System.out.println("Res of classic Java signature " + searchFileJavaStyle(firstFilePath, secondFilePath));
 *         } catch (IOException e) {
 *             System.out.println("Res of classic Java signature " + e);
 *         }
 *         System.out.println(System.lineSeparator());
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> Type of success state.
 * @param <E> Type of error state.
 * @author Jorge Rico Vivas
 */
public sealed interface Result<T, E> permits Ok, Err {

    /**
     * Executes the runnable and gets a {@link Ok} value with a None, returning an {@link Err} only if it couldn't
     * execute the operation and caught its exception.
     * <p>
     * This function is unable to turn unchecked exceptions ({@link RuntimeException}s) into {@link Err}, if you want
     * to catch an unchecked exception, use {@link Result#unchecked(Class, ThrowingSupplier)} instead.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<String, IOException> contentsOfAFile = Result.checked(() -> Files.readString(Path.of("my_file.txt")));
     * }
     * </pre>
     *
     * @param supplier Operation that gives a result to return.
     * @param <T>      Type of the success value operation.
     * @param <E>      Type of the error in the operation.
     * @return a {@link Ok} with the successful value, or {@link Err} with an exception if it failed.
     */
    @NotNull
    static <T, E extends Throwable> Result<T, E> checked(@NotNull ThrowingSupplier<T, E> supplier) {
        var notNullSupplier = requireNonNull(supplier);
        T value;
        try {
            value = notNullSupplier.get();
        } catch (Throwable thrown) {
            if (thrown instanceof RuntimeException e) {
                throw e;
            }
            @SuppressWarnings("unchecked")
            var error = (E) thrown;
            return new Err<>(error);
        }
        return new Ok<>(value);
    }

    /**
     * Executes the runnable and gets a {@link Ok} value with a {@link Tuple0} (As an empty object), returning an
     * {@link Err} only if it couldn't execute the operation and caught its exception.
     * <p>
     * This function is unable to turn unchecked exceptions ({@link RuntimeException}s) into {@link Err}, if you want
     * to catch an unchecked exception, use {@link Result#unchecked(Class, ThrowingRunnable)} instead.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Tuple0, IOException> readResult = Result.checked(() -> {
     *     Files.readString(Path.of("my_file.txt"));
     *     System.out.println("This operation returns nothing");
     * });
     * }
     * </pre>
     *
     * @param runnable Operation to run.
     * @param <E>      Type of the error in the operation.
     * @return a {@link Ok} with an empty successful value, or {@link Err} with an exception if it failed.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <E extends Throwable> Result<Tuple0, E> checked(@NotNull ThrowingRunnable<E> runnable) {
        var notNullRunnable = requireNonNull(runnable);
        try {
            notNullRunnable.run();
        } catch (Throwable thrown) {
            if (thrown instanceof RuntimeException e) {
                throw e;
            }
            @SuppressWarnings({"unchecked"})
            var error = (E) thrown;
            return new Err<>(error);
        }
        return new Ok<>(new Tuple0());
    }

    /**
     * Executes the supplier and gets a {@link Ok} value with the result of it, returning an {@link Err} only if it
     * couldn't execute the operation and caught its exception, which contains the exception that happened.
     * <p>
     * The exception launched is always checked to be the type of &lt;E&gt;, this is why you pass the errorClass
     * argument, being able to also catch {@link RuntimeException}s, unlike {@link Result#checked(ThrowingSupplier)}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<String, IOException> contentsOfAFile = Result.unchecked(IOException.class, () ->
     *         Files.readString(Path.of("my_file.txt")));
     * }
     * </pre>
     *
     * @param errorClass Class of the error,
     * @param supplier   Operation that gives a result to return.
     * @param <T>        Type of the success value operation.
     * @param <E>        Type of the error in the operation.
     * @return a {@link Ok} with the successful value, or {@link Err} with an exception if it failed.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T, E extends Throwable> Result<T, E> unchecked(Class<E> errorClass, @NotNull ThrowingSupplier<T, E> supplier) {
        var notNullSupplier = requireNonNull(supplier);
        T value;
        try {
            value = notNullSupplier.get();
        } catch (Throwable e) {
            try {
                E castedError = errorClass.cast(e);
                return new Err<>(castedError);
            } catch (ClassCastException ex) {
                RuntimeException couldNotCast = new RuntimeException("Exception was expected to be of type " +
                        errorClass.getName() + ", but it is " + e.getClass().getName(), ex);
                couldNotCast.setStackTrace(new StackTraceElement[]{});
                throw couldNotCast;
            }
        }
        return new Ok<>(value);
    }

    /**
     * Executes the runnable and gets a {@link Ok} value with a {@link Tuple0} (As an empty object), returning an
     * {@link Err} only if it couldn't execute the operation and caught its exception.
     * <p>
     * The exception launched is always checked to be the type of &lt;E&gt;, this is why you pass the errorClass
     * argument, being able to also catch {@link RuntimeException}s, unlike {@link Result#checked(ThrowingRunnable)}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Tuple0, IOException> readResult = Result.unchecked(IOException.class, () -> {
     *     Files.readString(Path.of("my_file.txt"));
     *     System.out.println("This operation returns nothing");
     * });
     * }
     * </pre>
     *
     * @param errorClass Class of the error,
     * @param runnable   Operation to run.
     * @param <E>        Type of the error in the operation.
     * @return a {@link Ok} with the successful value, or {@link Err} with an exception if it failed.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <E extends Throwable> Result<Tuple0, E> unchecked(Class<E> errorClass, @NotNull ThrowingRunnable<E> runnable) {
        var notNullSupplier = requireNonNull(runnable);
        try {
            notNullSupplier.run();
        } catch (Throwable e) {
            try {
                E castedError = errorClass.cast(e);
                return new Err<>(castedError);
            } catch (ClassCastException ex) {
                RuntimeException couldNotCast = new RuntimeException("Exception was expected to be of type " +
                        errorClass.getName() + ", but it is " + e.getClass().getName(), ex);
                couldNotCast.setStackTrace(new StackTraceElement[]{});
                throw couldNotCast;
            }
        }
        return new Ok<>(new Tuple0());
    }

    /**
     * Gets the contents of the result if is {@link Ok}, or throws the exception contained in {@link Err}'s
     * {@link Err#error()}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<String, IOException> readFileRes = Result.checked(() -> Files.readString(Path.of("my_file.txt")));
     * try {
     *     System.out.println("Contents of file are "+ Result.unwrap_or_throw(readFileRes));
     * } catch (IOException e) {
     *     System.out.println("Found IOException! "+e);
     * }
     * }
     * </pre>
     *
     * @param result The value to unwrap.
     * @param <T>    The success type.
     * @param <E>    The error type.
     * @return the contents of the result if is {@link Ok}.
     * @throws E the exception contained in {@link Err}'s {@link Err#error()} when the result is {@link Err}.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T, E extends Throwable> T unwrap_or_throw(Result<T, E> result) throws E {
        if (result.isOk()) {
            return result.unwrap();
        }
        throw result.unwrapErr();
    }

    /**
     * Turns this value into {@link Ok}, and it is the same as using {@link Ok}'s default constructor.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Exception> five = Result.ok(5);
     * Err<Integer, Exception> error = Result.err(new Exception("Aw no, an error"));
     * Result<String, Exception> result = Result.ok("Oh, the type of the variable now hides whether I am an Ok or an Err!");
     * Result<String, Exception> anotherResult = Result.err(new Exception("See? This is also hidden!"));
     * }
     * </pre>
     *
     * @param value value to turn into {@link Ok}.
     * @param <T>   type of the success in the Result.
     * @param <E>   type of the error in the Result.
     * @return A {@link Ok} value.
     */
    @NotNull
    static <T, E> Ok<T, E> ok(@NotNull final T value) {
        return new Ok<>(value);
    }

    /**
     * Turns this error value into {@link Err}, and it is the same as using {@link Err}'s default constructor.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Exception> five = Result.ok(5);
     * Err<Integer, Exception> error = Result.err(new Exception("Aw no, an error"));
     * Result<String, Exception> result = Result.ok("Oh, the type of the variable now hides whether I am an Ok or an Err!");
     * Result<String, Exception> anotherResult = Result.err(new Exception("See? This is also hidden!"));
     * }
     * </pre>
     *
     * @param error value to turn into {@link Err}.
     * @param <T>   type of the success in the Result.
     * @param <E>   type of the error in the Result.
     * @return A {@link Ok} value.
     */
    @NotNull
    static <T, E> Err<T, E> err(@NotNull final E error) {
        return new Err<>(error);
    }

    /**
     * Converts from {@link Result}&lt;{@link Result}&lt;T, E&gt;, E&gt; to {@link Result}&lt;T, E&gt;.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Result<Integer, Exception>, Exception> thisIsAResultWithAnError
     *         = Result.ok(Result.err( new Exception("This is an error")));
     * Result<Integer, Exception> theResultIsFlatNow = Result.flatten(thisIsAResultWithAnError);
     * }
     * </pre>
     *
     * @param result value to transform.
     * @param <T>    The success type.
     * @param <E>    The error type.
     * @return A {@link Result}&lt;T, E&gt;.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T, E> Result<T, E> flatten(@NotNull final Result<Result<T, E>, E> result) {
        requireNonNull(result);
        if (result.isErr()) {
            return new Err<>(requireNonNull(result.unwrapErr()));
        }
        if (result.unwrap().isErr()) {
            return new Err<>(requireNonNull(requireNonNull(result.unwrap()).unwrapErr()));
        }
        return new Ok<>(requireNonNull(requireNonNull(result.unwrap()).unwrap()));
    }

    /**
     * Transposes a {@link Result} of an {@link Option} into an {@link Option} of a {@link Result}.
     * <p>
     * {@link Ok}({@link None}) will be mapped to {@link None}. {@link Ok}({@link Some}(_)) and {@link Err}(_) will be
     * mapped to {@link Some}({@link Ok}(_)) and {@link Some}({@link Err}(_)).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Option<String>, IOException> readFile =
     *         Result.checked(() -> Option.of(Files.readString(Path.of("aFile.txt")), ""));
     * Option<Result<String, IOException>> readTransposed = Result.transpose(readFile);
     * }
     * </pre>
     *
     * @param result The result to transpose
     * @param <T>    Success type.
     * @param <E>    Error type.
     * @return an {@link Option} of a {@link Result}.
     */
    @SuppressWarnings("unused")
    @NotNull
    static <T, E> Option<Result<T, E>> transpose(@NotNull final Result<Option<T>, E> result) {
        requireNonNull(result);
        if (result.isErr()) {
            return new Some<>(new Err<>(requireNonNull(result.unwrapErr())));
        }
        if (result.unwrap().isSome()) {
            return new Some<>(new Ok<>(requireNonNull(requireNonNull(result.unwrap()).unwrap())));
        }
        return new None<>();
    }

    /**
     * Returns true if the result is Ok.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.isOk();
     * thisIsTrue = error_exception.isErr();
     * thisIsTrue = ok_five.isOkAnd(number -> number == 5);
     * thisIsTrue = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Is empty"));
     *
     * boolean thisIsFalse = ok_five.isErr();
     * thisIsFalse = error_exception.isOk();
     * thisIsFalse = ok_five.isOkAnd(number -> number == 123895);
     * thisIsFalse = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Oh, wrong message"));
     * }
     * </pre>
     *
     * @return true if the result is Ok.
     */
    boolean isOk();

    /**
     * Returns true if the result is Ok and the value inside of it matches a predicate.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.isOk();
     * thisIsTrue = error_exception.isErr();
     * thisIsTrue = ok_five.isOkAnd(number -> number == 5);
     * thisIsTrue = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Is empty"));
     *
     * boolean thisIsFalse = ok_five.isErr();
     * thisIsFalse = error_exception.isOk();
     * thisIsFalse = ok_five.isOkAnd(number -> number == 123895);
     * thisIsFalse = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Oh, wrong message"));
     * }
     * </pre>
     *
     * @param predicate predicated tested against the value if the result is Ok.
     * @return true if the result is Ok and the value inside of it matches a predicate.
     */
    @SuppressWarnings("unused")
    boolean isOkAnd(@NotNull final Predicate<T> predicate);

    /**
     * Returns true if the result is Err.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.isOk();
     * thisIsTrue = error_exception.isErr();
     * thisIsTrue = ok_five.isOkAnd(number -> number == 5);
     * thisIsTrue = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Is empty"));
     *
     * boolean thisIsFalse = ok_five.isErr();
     * thisIsFalse = error_exception.isOk();
     * thisIsFalse = ok_five.isOkAnd(number -> number == 123895);
     * thisIsFalse = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Oh, wrong message"));
     * }
     * </pre>
     *
     * @return true if the result is Err.
     */
    boolean isErr();

    /**
     * Returns true if the result is Err and the value inside of it matches a predicate.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.isOk();
     * thisIsTrue = error_exception.isErr();
     * thisIsTrue = ok_five.isOkAnd(number -> number == 5);
     * thisIsTrue = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Is empty"));
     *
     * boolean thisIsFalse = ok_five.isErr();
     * thisIsFalse = error_exception.isOk();
     * thisIsFalse = ok_five.isOkAnd(number -> number == 123895);
     * thisIsFalse = error_exception.isErrAnd(exception -> Objects.equals(exception.getMessage(), "Oh, wrong message"));
     * }
     * </pre>
     *
     * @param predicate predicated tested against the error if the result is Err.
     * @return true if the result is Err and the value inside of it matches a predicate.
     */
    @SuppressWarnings("unused")
    boolean isErrAnd(@NotNull final Predicate<E> predicate);

    /**
     * Converts from Result&lt;T, E&gt; to Option&lt;T&gt;.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.ok().isSome();
     * thisIsTrue = error_exception.err().isSome();
     * boolean thisIsFalse = error_exception.ok().isSome();
     * thisIsFalse = ok_five.err().isSome();
     * }
     * </pre>
     *
     * @return Option containing value if Result is Ok, empty otherwise.
     */
    @NotNull
    Option<T> ok();

    /**
     * Converts from Result&lt;T, E&gt; to Option&lt;E&gt;.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> error_exception = Result.err(new Exception("Is empty"));
     *
     * boolean thisIsTrue = ok_five.ok().isSome();
     * thisIsTrue = error_exception.err().isSome();
     * boolean thisIsFalse = error_exception.ok().isSome();
     * thisIsFalse = ok_five.err().isSome();
     * }
     * </pre>
     *
     * @return Option containing error if Result is Err, empty otherwise.
     */
    @NotNull
    Option<E> err();

    /**
     * Maps a Result&lt;T, E&gt; to Result&lt;U, E&gt; by applying a function to a contained Ok value, leaving an Err
     * value untouched.
     * <p>
     * This function can be used to compose the results of two functions.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<String, Exception> sixAsStringResult = resultFive.map(num->""+(num+1));
     * String sixAsString = resultFive.mapOr("This text won't show up", num->""+(num+1));
     * sixAsString = resultFive.mapOrElse(()->"This text won't show up", num->""+(num+1));
     * Result<Integer, String> changedExceptionToString = resultFive
     *         .mapError(error -> "This text won't show up " + error.getMessage());
     *
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     * Result<String, Exception> thisIsntSixButTheException = resultException.map(num->""+(num+1));
     * String thisReturnsDefaultText = resultException.mapOr("Default text", num->""+(num+1));
     * thisReturnsDefaultText = resultException.mapOrElse(()->"Default text", num->""+(num+1));
     * Result<Integer, String> mappedException = resultException
     *         .mapError(error -> "I have changed the exception to a String" + error.getMessage());
     * }
     * </pre>
     *
     * @param mapper Maps the original value to another value.
     * @param <U>    Type T transforms to.
     * @return Result&lt;T, E&gt; transformed to Result&lt;U, E&gt;, where T transformed into U using mapper.
     */
    @NotNull
    <U> Result<U, E> map(@NotNull final Function<T, U> mapper);

    /**
     * Returns the provided default (if Err), or applies a function to the contained value (if Ok).
     * <p>
     * Arguments passed to map_or are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use map_or_else, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<String, Exception> sixAsStringResult = resultFive.map(num->""+(num+1));
     * String sixAsString = resultFive.mapOr("This text won't show up", num->""+(num+1));
     * sixAsString = resultFive.mapOrElse(()->"This text won't show up", num->""+(num+1));
     * Result<Integer, String> changedExceptionToString = resultFive
     *         .mapError(error -> "This text won't show up " + error.getMessage());
     *
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     * Result<String, Exception> thisIsntSixButTheException = resultException.map(num->""+(num+1));
     * String thisReturnsDefaultText = resultException.mapOr("Default text", num->""+(num+1));
     * thisReturnsDefaultText = resultException.mapOrElse(()->"Default text", num->""+(num+1));
     * Result<Integer, String> mappedException = resultException
     *         .mapError(error -> "I have changed the exception to a String" + error.getMessage());
     * }
     * </pre>
     *
     * @param defaultValue a provided default which will be returned if this Result is Err(error).
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          Type T transforms to as a return result.
     * @return Value of the transformation from T to U if Result is Ok(value), otherwise, it returns the default
     * value.
     */
    @NotNull
    <U> U mapOr(@NotNull final U defaultValue, @NotNull final Function<T, U> mapper);

    /**
     * Maps a Result&lt;T, E&gt; to U by applying fallback function default to a contained Err value, or function
     * defaultValueGetter to a contained Ok value.
     * <p>
     * This function can be used to unpack a successful result while handling an error.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<String, Exception> sixAsStringResult = resultFive.map(num->""+(num+1));
     * String sixAsString = resultFive.mapOr("This text won't show up", num->""+(num+1));
     * sixAsString = resultFive.mapOrElse(()->"This text won't show up", num->""+(num+1));
     * Result<Integer, String> changedExceptionToString = resultFive
     *         .mapError(error -> "This text won't show up " + error.getMessage());
     *
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     * Result<String, Exception> thisIsntSixButTheException = resultException.map(num->""+(num+1));
     * String thisReturnsDefaultText = resultException.mapOr("Default text", num->""+(num+1));
     * thisReturnsDefaultText = resultException.mapOrElse(()->"Default text", num->""+(num+1));
     * Result<Integer, String> mappedException = resultException
     *         .mapError(error -> "I have changed the exception to a String" + error.getMessage());
     * }
     * </pre>
     *
     * @param defaultValue a provided supplier which results in default value which will be calculated and
     *                     returned if this Result is Err(error).
     * @param mapper       Maps the original value to another value as a return result.
     * @param <U>          Type T transforms to as a return result.
     * @return Value of the transformation from T to U if Result is Ok(value), otherwise, it calculates and returns
     * the default value from the supplier.
     */
    @NotNull
    <U> U mapOrElse(@NotNull final Supplier<U> defaultValue, @NotNull final Function<T, U> mapper);

    /**
     * Maps a Result&lt;T, E&gt; to Result&lt;T, F&gt; by applying a function to a contained Err value, leaving an Ok
     * value untouched.
     * <p>
     * This function can be used to pass through a successful result while handling an error.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<String, Exception> sixAsStringResult = resultFive.map(num->""+(num+1));
     * String sixAsString = resultFive.mapOr("This text won't show up", num->""+(num+1));
     * sixAsString = resultFive.mapOrElse(()->"This text won't show up", num->""+(num+1));
     * Result<Integer, String> changedExceptionToString = resultFive
     *         .mapError(error -> "This text won't show up " + error.getMessage());
     *
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     * Result<String, Exception> thisIsntSixButTheException = resultException.map(num->""+(num+1));
     * String thisReturnsDefaultText = resultException.mapOr("Default text", num->""+(num+1));
     * thisReturnsDefaultText = resultException.mapOrElse(()->"Default text", num->""+(num+1));
     * Result<Integer, String> mappedException = resultException
     *         .mapError(error -> "I have changed the exception to a String" + error.getMessage());
     * }
     * </pre>
     *
     * @param errorMapper Maps the original error to another error.
     * @param <O>         Type the error E transforms to.
     * @return Result&lt;T, E&gt; transformed to Result&lt;T, O&gt;, where the error E is transformed into O using
     * errorMapper.
     */
    @SuppressWarnings("unused")
    @NotNull
    <O> Result<T, O> mapError(@NotNull final Function<E, O> errorMapper);

    /**
     * Calls the provided {@link Consumer} on the contained value (if Ok).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * resultFive.inspect(num-> System.out.println("The number contained is "+num));
     * resultException.inspectErr(num-> System.out.println("The exception contained is "+num));
     *
     * resultFive.inspectErr(num-> System.out.println("This message won't show up, as the result is Ok, not Err"));
     * resultException.inspect(num-> System.out.println("This message won't show up, as the result is Err, not Ok"));
     * }
     * </pre>
     *
     * @param inspector consumer function to trigger on the contained value (if Ok).
     */
    void inspect(@NotNull final Consumer<T> inspector);

    /**
     * Calls the provided consumer function on the contained error (if Err).
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * resultFive.inspect(num-> System.out.println("The number contained is "+num));
     * resultException.inspectErr(num-> System.out.println("The exception contained is "+num));
     *
     * resultFive.inspectErr(num-> System.out.println("This message won't show up, as the result is Ok, not Err"));
     * resultException.inspect(num-> System.out.println("This message won't show up, as the result is Err, not Ok"));
     * }
     * </pre>
     *
     * @param inspector consumer function to trigger on the contained error (if Err).
     */
    @SuppressWarnings("unused")
    void inspectErr(@NotNull final Consumer<E> inspector);

    /**
     * Returns the contained Ok value.
     * <p>
     * Because this function may throw a IllegalCallerException, its use is generally discouraged. Instead, prefer to
     * use pattern matching and handle the Err case explicitly, or call either unwrap_or or unwrap_or_else.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @return the contained Ok value.
     * @throws IllegalCallerException if the value is an Err, with an error message provided by the Error’s value.
     */
    @NotNull
    T unwrap() throws IllegalCallerException;

    /**
     * Returns the contained Ok value.
     * <p>
     * Because this function may throw a IllegalCallerException, its use is generally discouraged. Instead, prefer to
     * use pattern matching and handle the Err case explicitly, or call unwrap_or, unwrap_or_else, or unwrap_or_default.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @param errorMessage Error message to include on the Runtime Exception on case it is triggered.
     * @return the contained Ok value.
     * @throws IllegalCallerException if the value is an Err, the message error will include an error message provided
     *                                by the Error’s value and the passed error message.
     */
    @NotNull
    T expect(@Nullable final String errorMessage) throws IllegalCallerException;

    /**
     * Returns the contained Ok value or a provided default.
     * <p>
     * Arguments passed to unwrap_or are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use unwrap_or_else, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @param defaultValue a provided default which will be returned if this Result is Err(error).
     * @return contained value if Result is Ok(value), otherwise, returns the default value.
     */
    @NotNull
    T unwrapOr(@NotNull final T defaultValue);

    /**
     * Returns the contained Ok value or computes it from a {@link Supplier}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @param defaultValue a provided default value getter whose value will be calculated and returned if this Result is
     *                     Err(error).
     * @return contained value if Result is Ok(value), otherwise, calculates and returns the default value from the
     * supplier.
     */
    @NotNull
    T unwrapOrElse(@NotNull final Supplier<T> defaultValue);

    /**
     * Returns the contained Err value.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @return the contained Err value.
     * @throws IllegalCallerException if the value is Ok(value).
     */
    @NotNull
    E unwrapErr() throws IllegalCallerException;

    /**
     * Returns the contained Err value.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> resultFive = Result.ok(5);
     * Result<Integer, Exception> resultException = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five = resultFive.unwrap();
     * five = resultFive.unwrapOr(0);
     * five = resultFive.unwrapOrElse(()->0);
     * resultFive.expect("This message won't show up");
     *
     * Exception exception = resultException.unwrapErr();
     * exception = resultException.expectErr("This message won't show up");
     * int zero = resultException.unwrapOr(0);
     * zero = resultException.unwrapOrElse(()->0);
     *
     * try{
     *     resultFive.unwrapErr();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultFive.expectErr("You cant expect to return an error from an Ok value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     *
     * try{
     *     resultException.unwrap();
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * try{
     *     resultException.expect("You cant expect a valid value to from an Err value!");
     * }catch (IllegalCallerException ex){
     *     System.out.println(ex);
     * }
     * }
     * </pre>
     *
     * @param errorMessage Error message to include on the Runtime Exception on case it is triggered.
     * @return the contained Err value.
     * @throws IllegalCallerException if the value is an Ok, with an error message which includes the passed message.
     */
    @SuppressWarnings("unused")
    @NotNull
    E expectErr(@Nullable final String errorMessage) throws IllegalCallerException;

    /**
     * Returns res if the result is Ok, otherwise returns the Err value of this Result.
     * <p>
     * Arguments passed to and are eagerly evaluated; if you are passing the result of a function call, it is
     * recommended to use and_then, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> ok_six = Result.ok(5);
     * Result<Integer, Exception> err_exception = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five;
     * Exception exception;
     *
     * five = ok_five.and(ok_six).unwrap();
     * exception = err_exception.and(ok_five).unwrapErr();
     * exception = ok_five.and(err_exception).unwrapErr();
     * exception = err_exception.and(err_exception).unwrapErr();
     *
     * five = ok_five.or(ok_six).unwrap();
     * five = err_exception.or(ok_six).unwrap();
     * five = ok_five.or(err_exception).unwrap();
     * exception = err_exception.or(err_exception).unwrapErr();
     * }
     * </pre>
     *
     * @param res The other Result whose contents are returned if this Result is Ok(value).
     * @param <U> Value type of the latter Result.
     * @return res if the result is Ok, otherwise returns the Err value of this Result.
     */
    @NotNull
    <U> Result<U, E> and(@NotNull final Result<U, E> res);

    /**
     * Returns the resulting of calling op over the value if Ok, otherwise returns the Err value of this Result.
     * <p>
     * This function can be used for control flow based on Result values.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> ok_six = Result.ok(5);
     * Result<Integer, Exception> err_exception = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five;
     * Exception exception;
     *
     * five = ok_five.and(ok_six).unwrap();
     * exception = err_exception.and(ok_five).unwrapErr();
     * exception = ok_five.and(err_exception).unwrapErr();
     * exception = err_exception.and(err_exception).unwrapErr();
     *
     * five = ok_five.or(ok_six).unwrap();
     * five = err_exception.or(ok_six).unwrap();
     * five = ok_five.or(err_exception).unwrap();
     * exception = err_exception.or(err_exception).unwrapErr();
     * }
     * </pre>
     *
     * @param res Generates a Result&lt;U,E&gt; from the T value.
     * @param <U> Value type of the produced Result if this Result is Ok.
     * @return Result with mapped value if Result was Ok, otherwise returns the Err value of this Result.
     */
    @NotNull
    <U> Result<U, E> andThen(@NotNull final Function<T, Result<U, E>> res);

    /**
     * Returns res if the result is Err, otherwise returns the Ok value of this Result.
     * <p>
     * Arguments passed to or are eagerly evaluated; if you are passing the result of a function call, it is recommended
     * to use or_else, which is lazily evaluated.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> ok_six = Result.ok(5);
     * Result<Integer, Exception> err_exception = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five;
     * Exception exception;
     *
     * five = ok_five.and(ok_six).unwrap();
     * exception = err_exception.and(ok_five).unwrapErr();
     * exception = ok_five.and(err_exception).unwrapErr();
     * exception = err_exception.and(err_exception).unwrapErr();
     *
     * five = ok_five.or(ok_six).unwrap();
     * five = err_exception.or(ok_six).unwrap();
     * five = ok_five.or(err_exception).unwrap();
     * exception = err_exception.or(err_exception).unwrapErr();
     * }
     * </pre>
     *
     * @param res The other Result whose contents are returned if this Result is Err(error).
     * @param <O> Error type of the latter Result.
     * @return res if the result is Err, otherwise returns the Ok value of this Result.
     */
    @NotNull
    <O> Result<T, O> or(@NotNull final Result<T, O> res);

    /**
     * Returns the resulting of calling op over the value if Err, otherwise returns the Ok value of self.
     * <p>
     * This function can be used for control flow based on result values.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Result<Integer, Exception> ok_five = Result.ok(5);
     * Result<Integer, Exception> ok_six = Result.ok(5);
     * Result<Integer, Exception> err_exception = Result.err(new Exception("Whops, this wasn't a number"));
     *
     * int five;
     * Exception exception;
     *
     * five = ok_five.and(ok_six).unwrap();
     * exception = err_exception.and(ok_five).unwrapErr();
     * exception = ok_five.and(err_exception).unwrapErr();
     * exception = err_exception.and(err_exception).unwrapErr();
     *
     * five = ok_five.or(ok_six).unwrap();
     * five = err_exception.or(ok_six).unwrap();
     * five = ok_five.or(err_exception).unwrap();
     * exception = err_exception.or(err_exception).unwrapErr();
     * }
     * </pre>
     *
     * @param res Generates a Result&lt;T,O&gt; from the E error.
     * @param <O> Error type of the latter Result.
     * @return Result with mapped error if Result was Err, otherwise returns the Ok value of this Result.
     */
    @NotNull
    <O> Result<T, O> orElse(@NotNull final Function<E, Result<T, O>> res);

}