package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 4 non-null values, all of them with possibly different types.
 * <p>
 * This is the record version mainly used for pattern matching.
 *
 * @param v0  First value.
 * @param v1  Second value.
 * @param v2  Third value.
 * @param v3  Fourth value.
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @param <W> Fourth value type.
 * @author Jorge Rico Vivas
 */
public record Tuple4Record<T, U, V, W>(T v0, U v1, V v2, W v3) {


    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple4<T, U, V, W> toClass() {
        return new Tuple4<>(v0, v1, v2, v3);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple4Record<?, ?, ?, ?> that = (Tuple4Record<?, ?, ?, ?>) o;
        return v0.equals(that.v0) && v1.equals(that.v1) && v2.equals(that.v2) && v3.equals(that.v3);
    }

    @Override public int hashCode() {
        int result = v0.hashCode();
        result = 31 * result + v1.hashCode();
        result = 31 * result + v2.hashCode();
        result = 31 * result + v3.hashCode();
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple4Record{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                '}';
    }
}