package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 1 non-null value.
 *
 * @param <T> First value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple1<T> {

    /**
     * First value.
     */
    public T v0;

    /**
     * Creates a tuple with said value.
     *
     * @param v0 First value.
     */
    public Tuple1(T v0) {
        this.v0 = v0;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple1Record<T> toRecord() {
        return new Tuple1Record<>(v0);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple1Record<T> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple1<?> tuple1 = (Tuple1<?>) o;
        return Objects.equals(v0, tuple1.v0);
    }

    @Override public int hashCode() {
        return Objects.hashCode(v0);
    }

    @Override @NotNull public String toString() {
        return "Tuple1{" +
                "v0=" + v0 +
                '}';
    }
}
