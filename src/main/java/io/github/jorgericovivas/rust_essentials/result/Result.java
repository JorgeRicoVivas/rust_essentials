package io.github.jorgericovivas.rust_essentials.result;

import io.github.jorgericovivas.rust_essentials.option.None;
import io.github.jorgericovivas.rust_essentials.option.Option;
import io.github.jorgericovivas.rust_essentials.option.Some;
import io.github.jorgericovivas.rust_essentials.tuples.Tuple0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
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
 *     return Result.unwrapOrThrow(contents);
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
public sealed interface Result<T, E> extends Serializable permits Ok, Err {
    
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
                                                                             errorClass.getName() + ", but it is " + e.getClass()
                                                                                                                      .getName(), ex);
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
                                                                             errorClass.getName() + ", but it is " + e.getClass()
                                                                                                                      .getName(), ex);
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
     *     System.out.println("Contents of file are "+ Result.unwrapOrThrow(readFileRes));
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
    static <T, E extends Throwable> T unwrapOrThrow(Result<T, E> result) throws E {
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
    
    
    //The following are the joinOks methods, they allow to join multiple Oks whose value type are the same,allowing
    //to cast the Ok types to a common class that every Ok value extends.
    //
    //This was automatically generated with the following Rust code:
    //
    //fn main() {
    //    for n in 1..=12 {
    //        println!("{}\n\n\n", create_join_oks(n).unwrap_or_default());
    //    }
    //}
    //
    //fn create_join_oks(n_of_errors:usize) -> Option<String> {
    //    if n_of_errors==0{return None;}
    //    let parameter_definition = (1..=n_of_errors).map(|n|format!("T{n} extends TCommon")).collect::<Vec<_>>().join(", ");
    //    let argument_definition = (1..=n_of_errors).map(|n|format!("@NotNull Result<T{n}, E> ok{n}")).collect::<Vec<_>>().join(",\n");
    //    let return_errors = (1..=n_of_errors).map(|n|format!("if (requireNonNull(ok{n}) instanceof Ok(var okValue))
    //            return new Some<>(new Ok<>(okValue));")).collect::<Vec<_>>().join("\n");
    //
    //    let function = format!("/**\n * Returns the first {{@link Result}} that is {{@link Ok}}, otherwise, it returns a {{@link None}}.\n */ \n\
    //    @NotNull\n public static <TCommon, E, {parameter_definition}> Option<Result<TCommon, E>> joinOks(\n{argument_definition}\n){{\n\
    //         {return_errors}
    //         return new None<>();
    //    }}");
    //    Some(function)
    //}
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon, T8 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7,
            @NotNull Result<T8, E> ok8
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon, T8 extends TCommon, T9 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7,
            @NotNull Result<T8, E> ok8,
            @NotNull Result<T9, E> ok9
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon, T8 extends TCommon, T9 extends TCommon, T10 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7,
            @NotNull Result<T8, E> ok8,
            @NotNull Result<T9, E> ok9,
            @NotNull Result<T10, E> ok10
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon, T8 extends TCommon, T9 extends TCommon, T10 extends TCommon, T11 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7,
            @NotNull Result<T8, E> ok8,
            @NotNull Result<T9, E> ok9,
            @NotNull Result<T10, E> ok10,
            @NotNull Result<T11, E> ok11
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok11) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, E, T1 extends TCommon, T2 extends TCommon, T3 extends TCommon, T4 extends TCommon, T5 extends TCommon, T6 extends TCommon, T7 extends TCommon, T8 extends TCommon, T9 extends TCommon, T10 extends TCommon, T11 extends TCommon, T12 extends TCommon> Option<Result<TCommon, E>> joinOks(
            @NotNull Result<T1, E> ok1,
            @NotNull Result<T2, E> ok2,
            @NotNull Result<T3, E> ok3,
            @NotNull Result<T4, E> ok4,
            @NotNull Result<T5, E> ok5,
            @NotNull Result<T6, E> ok6,
            @NotNull Result<T7, E> ok7,
            @NotNull Result<T8, E> ok8,
            @NotNull Result<T9, E> ok9,
            @NotNull Result<T10, E> ok10,
            @NotNull Result<T11, E> ok11,
            @NotNull Result<T12, E> ok12
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok11) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        if (requireNonNull(ok12) instanceof Ok(var okValue))
            return new Some<>(new Ok<>(okValue));
        return new None<>();
    }
    
    
    //The following are the joinErrors methods, they allow to join multiple Errors whose value type are the same,
    //allowing to cast the Error types to a common class that every Error value extends.
    //
    //This was automatically generated with the following Rust code:
    //
    //fn main() {
    //    for n in 1..=12 {
    //        println!("{}\n\n\n", create_join_errors(n).unwrap_or_default());
    //    }
    //}
    //
    //fn create_join_errors(n_of_errors:usize) -> Option<String> {
    //    if n_of_errors==0{return None;}
    //    let parameter_definition = (1..=n_of_errors).map(|n|format!("E{n} extends ECommon")).collect::<Vec<_>>().join(", ");
    //    let argument_definition = (1..=n_of_errors).map(|n|format!("@NotNull Result<T, E{n}> error{n}")).collect::<Vec<_>>().join(",\n");
    //    let return_errors = (1..=n_of_errors).map(|n|format!("if (requireNonNull(error{n}) instanceof Err(var error))
    //            return new Some<>(new Err<>(error));")).collect::<Vec<_>>().join("\n");
    //
    //    let function = format!("/**\n * Returns the first {{@link Result}} that is {{@link Err}}, otherwise, it returns a {{@link None}}.\n */ \n\
    //    @NotNull\n public static <T, ECommon, {parameter_definition}> Option<Result<T, ECommon>> joinErrors(\n{argument_definition}\n){{\n\
    //         {return_errors}
    //         return new None<>();
    //    }}");
    //    Some(function)
    //}
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon, E8 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7,
            @NotNull Result<T, E8> error8
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon, E8 extends ECommon, E9 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7,
            @NotNull Result<T, E8> error8,
            @NotNull Result<T, E9> error9
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon, E8 extends ECommon, E9 extends ECommon, E10 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7,
            @NotNull Result<T, E8> error8,
            @NotNull Result<T, E9> error9,
            @NotNull Result<T, E10> error10
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon, E8 extends ECommon, E9 extends ECommon, E10 extends ECommon, E11 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7,
            @NotNull Result<T, E8> error8,
            @NotNull Result<T, E9> error9,
            @NotNull Result<T, E10> error10,
            @NotNull Result<T, E11> error11
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error11) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    
    /**
     * Returns the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Ok<Integer, Number> okSix = Result.ok(6);
     * Err<Integer, Double> errorSixAndHalf = Result.err(6.5);
     * Err<Integer, Integer> errorSix = Result.err(6);
     *
     * Option<Result<Integer, Number>> firstError = Result.joinErrors(
     *         okSix, errorSixAndHalf, errorSix
     * );
     *
     * Assertions.assertEquals(firstError, Option.some(Result.err(6.5)));
     * }
     * </pre>
     */
    @NotNull
    static <T, ECommon, E1 extends ECommon, E2 extends ECommon, E3 extends ECommon, E4 extends ECommon, E5 extends ECommon, E6 extends ECommon, E7 extends ECommon, E8 extends ECommon, E9 extends ECommon, E10 extends ECommon, E11 extends ECommon, E12 extends ECommon> Option<Result<T, ECommon>> joinErrors(
            @NotNull Result<T, E1> error1,
            @NotNull Result<T, E2> error2,
            @NotNull Result<T, E3> error3,
            @NotNull Result<T, E4> error4,
            @NotNull Result<T, E5> error5,
            @NotNull Result<T, E6> error6,
            @NotNull Result<T, E7> error7,
            @NotNull Result<T, E8> error8,
            @NotNull Result<T, E9> error9,
            @NotNull Result<T, E10> error10,
            @NotNull Result<T, E11> error11,
            @NotNull Result<T, E12> error12
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error11) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        if (requireNonNull(error12) instanceof Err(var error))
            return new Some<>(new Err<>(error));
        return new None<>();
    }
    
    //The following are the firstError methods, they allow get the first Error value allowing to cast the Error types to
    //a common class that every Error value extends, while the Value types can be different .
    //
    //This was automatically generated with the following Rust code:
    //
    //fn main() {
    //    for n in 1..=12 {
    //        println!("{}\n\n\n", create_join_errors(n).unwrap_or_default());
    //    }
    //}
    //
    //fn create_join_errors(n_of_errors:usize) -> Option<String> {
    //    if n_of_errors==0{return None;}
    //    let parameter_definition = (1..=n_of_errors).map(|n|format!("T{n}, E{n} extends ECommon")).collect::<Vec<_>>().join(", ");
    //    let argument_definition = (1..=n_of_errors).map(|n|format!("@NotNull Result<T{n}, E{n}> error{n}")).collect::<Vec<_>>().join(",\n");
    //    let return_errors = (1..=n_of_errors).map(|n|format!("if (requireNonNull(error{n}) instanceof Err(var error))
    //            return new Some<>(error);")).collect::<Vec<_>>().join("\n");
    //
    //    let function = format!("/**\n * Returns the error value of the first {{@link Result}} that is {{@link Err}}, otherwise, it returns a {{@link None}}.\n */ \n\
    //    @NotNull\n public static <ECommon, {parameter_definition}> Option<ECommon> firstError(\n{argument_definition}\n){{\n\
    //         {return_errors}
    //         return new None<>();
    //    }}");
    //    Some(function)
    //}
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon, T8, E8 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7,
            @NotNull Result<T8, E8> error8
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon, T8, E8 extends ECommon, T9, E9 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7,
            @NotNull Result<T8, E8> error8,
            @NotNull Result<T9, E9> error9
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon, T8, E8 extends ECommon, T9, E9 extends ECommon, T10, E10 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7,
            @NotNull Result<T8, E8> error8,
            @NotNull Result<T9, E9> error9,
            @NotNull Result<T10, E10> error10
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon, T8, E8 extends ECommon, T9, E9 extends ECommon, T10, E10 extends ECommon, T11, E11 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7,
            @NotNull Result<T8, E8> error8,
            @NotNull Result<T9, E9> error9,
            @NotNull Result<T10, E10> error10,
            @NotNull Result<T11, E11> error11
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error11) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    /**
     * Returns the error value of the first {@link Result} that is {@link Err}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <ECommon, T1, E1 extends ECommon, T2, E2 extends ECommon, T3, E3 extends ECommon, T4, E4 extends ECommon, T5, E5 extends ECommon, T6, E6 extends ECommon, T7, E7 extends ECommon, T8, E8 extends ECommon, T9, E9 extends ECommon, T10, E10 extends ECommon, T11, E11 extends ECommon, T12, E12 extends ECommon> Option<ECommon> firstError(
            @NotNull Result<T1, E1> error1,
            @NotNull Result<T2, E2> error2,
            @NotNull Result<T3, E3> error3,
            @NotNull Result<T4, E4> error4,
            @NotNull Result<T5, E5> error5,
            @NotNull Result<T6, E6> error6,
            @NotNull Result<T7, E7> error7,
            @NotNull Result<T8, E8> error8,
            @NotNull Result<T9, E9> error9,
            @NotNull Result<T10, E10> error10,
            @NotNull Result<T11, E11> error11,
            @NotNull Result<T12, E12> error12
    ) {
        if (requireNonNull(error1) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error2) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error3) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error4) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error5) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error6) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error7) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error8) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error9) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error10) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error11) instanceof Err(var error))
            return new Some<>(error);
        if (requireNonNull(error12) instanceof Err(var error))
            return new Some<>(error);
        return new None<>();
    }
    
    
    //The following are the firstPl methods, they allow get the first Pl value allowing to cast the Success types to
    //a common class that every Ok value extends, while the Err types can be different .
    //
    //This was automatically generated with the following Rust code:
    //
    //fn main() {
    //    for n in 3..=3 {
    //        println!("{}\n\n\n", create_join_errors(n).unwrap_or_default());
    //    }
    //}
    //
    //fn create_join_errors(n_of_errors:usize) -> Option<String> {
    //    if n_of_errors==0{return None;}
    //    let parameter_definition = (1..=n_of_errors).map(|n|format!("T{n} extends TCommon, E{n}")).collect::<Vec<_>>().join(", ");
    //    let argument_definition = (1..=n_of_errors).map(|n|format!("@NotNull Result<T{n}, E{n}> ok{n}")).collect::<Vec<_>>().join(",\n");
    //    let return_errors = (1..=n_of_errors).map(|n|format!("if (requireNonNull(ok{n}) instanceof Ok(var okValue))
    //            return new Some<>(okValue);")).collect::<Vec<_>>().join("\n");
    //
    //    let function = format!("/**\n * Returns the success value of the first {{@link Result}} that is {{@link Ok}}, otherwise, it returns a {{@link None}}.\n */ \n\
    //    @NotNull\n public static <TCommon, {parameter_definition}> Option<TCommon> firstOk(\n{argument_definition}\n){{\n\
    //         {return_errors}
    //         return new None<>();
    //    }}");
    //    Some(function)
    //}
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7, T8 extends TCommon, E8> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7,
            @NotNull Result<T8, E8> ok8
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7, T8 extends TCommon, E8, T9 extends TCommon, E9> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7,
            @NotNull Result<T8, E8> ok8,
            @NotNull Result<T9, E9> ok9
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7, T8 extends TCommon, E8, T9 extends TCommon, E9, T10 extends TCommon, E10> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7,
            @NotNull Result<T8, E8> ok8,
            @NotNull Result<T9, E9> ok9,
            @NotNull Result<T10, E10> ok10
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7, T8 extends TCommon, E8, T9 extends TCommon, E9, T10 extends TCommon, E10, T11 extends TCommon, E11> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7,
            @NotNull Result<T8, E8> ok8,
            @NotNull Result<T9, E9> ok9,
            @NotNull Result<T10, E10> ok10,
            @NotNull Result<T11, E11> ok11
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok11) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
    /**
     * Returns the success value of the first {@link Result} that is {@link Ok}, otherwise, it returns a {@link None}.
     */
    @NotNull
    static <TCommon, T1 extends TCommon, E1, T2 extends TCommon, E2, T3 extends TCommon, E3, T4 extends TCommon, E4, T5 extends TCommon, E5, T6 extends TCommon, E6, T7 extends TCommon, E7, T8 extends TCommon, E8, T9 extends TCommon, E9, T10 extends TCommon, E10, T11 extends TCommon, E11, T12 extends TCommon, E12> Option<TCommon> firstOk(
            @NotNull Result<T1, E1> ok1,
            @NotNull Result<T2, E2> ok2,
            @NotNull Result<T3, E3> ok3,
            @NotNull Result<T4, E4> ok4,
            @NotNull Result<T5, E5> ok5,
            @NotNull Result<T6, E6> ok6,
            @NotNull Result<T7, E7> ok7,
            @NotNull Result<T8, E8> ok8,
            @NotNull Result<T9, E9> ok9,
            @NotNull Result<T10, E10> ok10,
            @NotNull Result<T11, E11> ok11,
            @NotNull Result<T12, E12> ok12
    ) {
        if (requireNonNull(ok1) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok2) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok3) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok4) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok5) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok6) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok7) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok8) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok9) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok10) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok11) instanceof Ok(var okValue))
            return new Some<>(okValue);
        if (requireNonNull(ok12) instanceof Ok(var okValue))
            return new Some<>(okValue);
        return new None<>();
    }
    
    
}