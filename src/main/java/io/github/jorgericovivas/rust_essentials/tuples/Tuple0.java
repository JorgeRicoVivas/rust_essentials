package io.github.jorgericovivas.rust_essentials.tuples;


import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing no values, this is also known as 'empty'.
 * <p>
 * This is the record version mainly used for pattern matching, but since its empty, it also acts as the class version.
 *
 * @author Jorge Rico Vivas
 */
public record Tuple0() {
    @Override @NotNull public String toString() {
        return "Tuple0{}";
    }


    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple0 toRecord() {
        return this;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple0 record() {
        return this;
    }

    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple0 toClass() {
        return this;
    }
}