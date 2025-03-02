package io.github.jorgericovivas.rust_essentials.diagnostic;

import io.github.jorgericovivas.rust_essentials.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * An {@link Exception} with a {@link Diagnostic} attached to it.
 * <p>
 * When printing this exception, it also shows the {@link Diagnostic}'s message as show in it's documentation (See
 * {@link Diagnostic}).
 * <p>
 *
 *
 * Note: 'message' is renamed to 'concept'.
 * <br>
 * <a href="https://doc.rust-lang.org/proc_macro/struct.Diagnostic.html">Rust's Diagnostic from the proc macro crate</a>.
 *
 * @see Diagnostic
 * @author Jorge Rico Vivas
 */

public class DiagnosedException extends Exception implements Serializable {
    
    @NotNull private final Diagnostic diagnostic;
    
    /**
     * Creates a new {@link DiagnosedException} with a new {@link Diagnostic}.
     */
    public DiagnosedException() {
        this.diagnostic = new Diagnostic();
    }
    
    /**
     * Creates a new {@link DiagnosedException} with the {@link Diagnostic} sent as parameter, or a new one if null and
     * not using preprocessor.
     *
     * @param diagnostic The diagnostic where explanation about this error is written.
     */
    public DiagnosedException(@NotNull Diagnostic diagnostic) {
        this.diagnostic = Option.of(diagnostic).unwrapOrElse(Diagnostic::new);
    }
    
    /**
     * returns This {@link DiagnosedException}'s {@link Diagnostic}.
     *
     * @return This {@link DiagnosedException}'s {@link Diagnostic}.
     */
    @NotNull @SuppressWarnings("unused")
    public Diagnostic diagnostic() {
        return diagnostic;
    }
    
    /**
     * Sets the main message to show when turning this diagnostic into a {@link String}.
     *
     * @param concept the main message to show when turning this diagnostic into a {@link String}.
     * @return self
     */
    @NotNull @SuppressWarnings("UnusedReturnValue")
    public DiagnosedException withConcept(@Nullable String concept) {
        diagnostic.withConcept(concept);
        return this;
    }
    
    /**
     * Adds a new message with information on how to solve the problem that happened.
     * <p>
     * This will show up, along other help messages, when turning this diagnostic into a {@link String}.
     *
     *
     * @param helpMessage a new message with information on how to solve the problem that happened.
     * @return self
     */
    @NotNull @SuppressWarnings("UnusedReturnValue")
    public DiagnosedException withHelp(@Nullable String helpMessage) {
        diagnostic.withHelp(helpMessage);
        return this;
    }
    
    /**
     * Adds a new message with information explaining the problem that happened.
     * <p>
     * This will show up, along other note messages, when turning this diagnostic into a {@link String}.
     *
     * @param noteMessage a new message with information explaining the problem that happened.
     * @return self
     */
    @NotNull @SuppressWarnings("UnusedReturnValue")
    public DiagnosedException withNote(@Nullable String noteMessage) {
        diagnostic.withNote(noteMessage);
        return this;
    }
    
    /**
     * Returns the detail message with the diagnostic attached to it.
     *
     * @return the detail message with the diagnostic attached to it.
     */
    @NotNull @Override
    public String getMessage() {
        return commonGetMessage(this, diagnostic);
    }
    
    /**
     * Creates a message out of the {@link Diagnostic}, prepending and postpending it with two line separators.
     * <p>
     * If the source exception doesn't have an empty stack trace, it postpends the message with a string telling:
     * <p>
     * 'If you can't solve this problem, show this information to the developers along this:'
     *
     * @param sourceException The source exception that might or not have a stack trace.
     * @param diagnostic The diagnostic this message gets built from.
     * @return A message out of the {@link Diagnostic}, prepending and postpending it with two line separators, with a
     *         special message telling whether the source exception has a backtrace or not.
     */
    public static @NotNull String commonGetMessage(@NotNull Exception sourceException, @NotNull Diagnostic diagnostic) {
        var post_pend = requireNonNull(sourceException).getStackTrace().length > 0 ? "\nIf you can't solve this problem, show this information to the developers along this:\n\nStack trace is:" : "";
        return "\n\n" + requireNonNull(diagnostic) + "\n\n" + post_pend;
    }
}
