package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 1 non-null value.
 * <p>
 * This is the record version mainly used for pattern matching.
 *
 * @param v0  First value.
 * @param <T> First value type.
 * @author Jorge Rico Vivas
 */
public record Tuple1Record<T>(T v0) {


    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple1<T> toClass() {
        return new Tuple1<>(v0);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple1Record<?> that = (Tuple1Record<?>) o;
        return v0.equals(that.v0);
    }

    @Override public int hashCode() {
        return v0.hashCode();
    }

    @Override @NotNull public String toString() {
        return "Tuple1Record{" +
                "v0=" + v0 +
                '}';
    }
}