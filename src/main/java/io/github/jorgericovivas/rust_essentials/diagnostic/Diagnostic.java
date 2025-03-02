package io.github.jorgericovivas.rust_essentials.diagnostic;

import io.github.jorgericovivas.rust_essentials.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A structure representing a diagnostic message.
 * <p>
 * Note: 'message' is renamed to 'concept'.
 *
 * <p>Example of use:</p>
 * <pre>
 * {@code
 * Diagnostic diagnostic = new Diagnostic()
 *         .withConcept("This is an error.\nThis message tells what the error means.")
 *         .withNote("This is a note message.\nThis message tells why the error happened.")
 *         .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
 * System.out.println(diagnostic);
 * }
 * </pre>
 * <br>
 * This shows:
 * <br><br>
 * <strong><span style="color:red;">Error:</span></strong> This is an error.<br><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells what the error means.
 * <br><br><br>
 * <strong><span style="color:blue;">Note:</span></strong> This is a note message.<br><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells why the error happened.
 * <br><br><br>
 * <strong><span style="color:green;">Help</span></strong>: This is a help message.<br><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells information to help solve in solving the
 * problem.
 * <br><br><br><br>
 * <a href="https://doc.rust-lang.org/proc_macro/struct.Diagnostic.html">Rust's Diagnostic from the proc macro crate</a>.
 *
 * @author Jorge Rico Vivas
 */
@SuppressWarnings("UnusedReturnValue")
public class Diagnostic implements Serializable {
    
    /**
     * An enum representing a diagnostic level.
     */
    public enum Level {
        ERROR, WARNING;
        
        //com.diogonunes.jcolor.new AnsiFormat(com.diogonunes.jcolor.Attribute.BRIGHT_RED_TEXT(), Attribute.BOLD()).format("Error: ");
        @NotNull
        private static final String PREPEND_ERROR = "\u001B[91;1mError: \u001B[0m";
        
        @NotNull
        @SuppressWarnings("SuspiciousRegexArgument")
        private static final String PREPEND_ERROR_EMPTY = "       ";
        
        @NotNull
        private static final String PREPEND_WARNING = "\u001B[93;1mWarning: \u001B[0m";
        
        @NotNull
        @SuppressWarnings("SuspiciousRegexArgument")
        private static final String PREPEND_WARNING_EMPTY = "         ";
        
        @NotNull private String coloredTitle() {
            return switch (this) {
                case ERROR -> PREPEND_ERROR;
                case WARNING -> PREPEND_WARNING;
            };
        }
        
        @NotNull private String emptyPrepend() {
            return switch (this) {
                case ERROR -> PREPEND_ERROR_EMPTY;
                case WARNING -> PREPEND_WARNING_EMPTY;
            };
        }
    }
    
    
    @NotNull
    private final Level level;
    
    @NotNull
    private Option<String> concept;
    
    @NotNull
    private final List<String> helps;
    
    @NotNull
    private final List<String> notes;
    
    /**
     * Creates a new empty {@link Diagnostic}.
     */
    public Diagnostic() {
        this.level = Level.ERROR;
        this.concept = Option.none();
        this.helps = new ArrayList<>();
        this.notes = new ArrayList<>();
    }
    
    /**
     * Creates a new empty {@link Diagnostic} with the specified {@link Level}.
     */
    public Diagnostic(@NotNull Level level) {
        this.level = Option.of(level).unwrapOr(Level.ERROR);
        this.concept = Option.none();
        this.helps = new ArrayList<>();
        this.notes = new ArrayList<>();
    }
    
    /**
     * Creates a new {@link Diagnostic} with message of concept.
     */
    public Diagnostic(@Nullable String concept) {
        this.level = Level.ERROR;
        this.concept = Option.none();
        this.helps = new ArrayList<>();
        this.notes = new ArrayList<>();
        withConcept(concept);
    }
    
    /**
     * Creates a new {@link Diagnostic} with message of concept and the specified {@link Level}.
     */
    public Diagnostic(@NotNull Level level, @Nullable String concept) {
        this.level = Option.of(level).unwrapOr(Level.ERROR);
        this.concept = Option.none();
        this.helps = new ArrayList<>();
        this.notes = new ArrayList<>();
        withConcept(concept);
    }
    
    
    /**
     * Sets the main message to show when turning this diagnostic into a {@link String}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Diagnostic diagnostic = new Diagnostic()
     *         .withConcept("This is an error.\nThis message tells what the error means.")
     *         .withNote("This is a note message.\nThis message tells why the error happened.")
     *         .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
     * System.out.println(diagnostic);
     * }
     * </pre>
     * <br>
     * This shows:
     * <br><br>
     * <strong><span style="color:red;">Error:</span></strong> This is an error.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells what the error means.
     * <br><br><br>
     * <strong><span style="color:blue;">Note:</span></strong> This is a note message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells why the error happened.
     * <br><br><br>
     * <strong><span style="color:green;">Help</span></strong>: This is a help message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells information to help solve in solving the
     * problem.
     *
     * @param concept the main message to show when turning this diagnostic into a {@link String}.
     * @return self
     */
    @NotNull
    public Diagnostic withConcept(@Nullable String concept) {
        this.concept = Option.of(concept)
                             .filter((string) -> !string.isBlank())
                             .or(this.concept);
        return this;
    }
    
    /**
     * Adds a new message with information on how to solve the problem that happened.
     * <p>
     * This will show up, along other help messages, when turning this diagnostic into a {@link String}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Diagnostic diagnostic = new Diagnostic()
     *         .withConcept("This is an error.\nThis message tells what the error means.")
     *         .withNote("This is a note message.\nThis message tells why the error happened.")
     *         .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
     * System.out.println(diagnostic);
     * }
     * </pre>
     * <br>
     * This shows:
     * <br><br>
     * <strong><span style="color:red;">Error:</span></strong> This is an error.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells what the error means.
     * <br><br><br>
     * <strong><span style="color:blue;">Note:</span></strong> This is a note message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells why the error happened.
     * <br><br><br>
     * <strong><span style="color:green;">Help</span></strong>: This is a help message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells information to help solve in solving the
     * problem.
     *
     * @param helpMessage a new message with information on how to solve the problem that happened.
     * @return self
     */
    @NotNull
    public Diagnostic withHelp(@Nullable String helpMessage) {
        Option.of(helpMessage)
              .filter(string -> !string.isBlank())
              .inspect(this.helps::add);
        return this;
    }
    
    /**
     * Adds a new message with information explaining the problem that happened.
     * <p>
     * This will show up, along other note messages, when turning this diagnostic into a {@link String}.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Diagnostic diagnostic = new Diagnostic()
     *         .withConcept("This is an error.\nThis message tells what the error means.")
     *         .withNote("This is a note message.\nThis message tells why the error happened.")
     *         .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
     * System.out.println(diagnostic);
     * }
     * </pre>
     * <br>
     * This shows:
     * <br><br>
     * <strong><span style="color:red;">Error:</span></strong> This is an error.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells what the error means.
     * <br><br><br>
     * <strong><span style="color:blue;">Note:</span></strong> This is a note message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells why the error happened.
     * <br><br><br>
     * <strong><span style="color:green;">Help</span></strong>: This is a help message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells information to help solve in solving the
     * problem.
     *
     * @param noteMessage a new message with information explaining the problem that happened.
     * @return self
     */
    @NotNull
    public Diagnostic withNote(@Nullable String noteMessage) {
        Option.of(noteMessage)
              .filter(string -> !string.isBlank())
              .inspect(this.notes::add);
        return this;
    }
    
    /**
     * Turns this diagnostic into a user-friendly message.
     *
     * <p>Example of use:</p>
     * <pre>
     * {@code
     * Diagnostic diagnostic = new Diagnostic()
     *         .withConcept("This is an error.\nThis message tells what the error means.")
     *         .withNote("This is a note message.\nThis message tells why the error happened.")
     *         .withHelp("This is a help message.\nThis message tells information to help solve in solving the problem.");
     * System.out.println(diagnostic);
     * }
     * </pre>
     * <br>
     * This shows:
     * <br><br>
     * <strong><span style="color:red;">Error:</span></strong> This is an error.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells what the error means.
     * <br><br><br>
     * <strong><span style="color:blue;">Note:</span></strong> This is a note message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells why the error happened.
     * <br><br><br>
     * <strong><span style="color:green;">Help</span></strong>: This is a help message.<br><br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This message tells information to help solve in solving the
     * problem.
     *
     * @return this diagnostic into a user-friendly message.
     */
    @NotNull @Override
    public String toString() {
        var title = concept.unwrapOr("An error has occurred");
        return Stream
                .of(Stream.of(title).map(message -> prependWith(message, level.coloredTitle(), level.emptyPrepend())),
                    this.notes.stream().map(message -> prependWith(message, PREPEND_NOTE, PREPEND_NOTE_EMPTY)),
                    this.helps.stream().map(message -> prependWith(message, PREPEND_HELP, PREPEND_HELP_EMPTY))
                )
                .flatMap(self -> self)
                .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }
    
    @NotNull
    private String prependWith(@NotNull String message, @NotNull String prepend_to_first_line, @NotNull String prepend_to_other_lines) {
        AtomicBoolean is_first_line = new AtomicBoolean(true);
        return message
                .lines()
                .map(line -> {
                    var prepend = is_first_line.get() ? prepend_to_first_line : prepend_to_other_lines;
                    is_first_line.set(false);
                    return prepend + line;
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }
    
    
    @NotNull
    private static final String PREPEND_HELP = "\u001B[92;1mHelp: \u001B[0m";
    
    @NotNull
    @SuppressWarnings("SuspiciousRegexArgument")
    private static final String PREPEND_HELP_EMPTY = "      ";
    
    @NotNull
    
    private static final String PREPEND_NOTE = "\u001B[94;1mNote: \u001B[0m";
    
    @NotNull
    @SuppressWarnings("SuspiciousRegexArgument")
    private static final String PREPEND_NOTE_EMPTY = "      ";
    
}
