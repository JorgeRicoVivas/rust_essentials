package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 5 non-null values, all of them with possibly different types.
 * <p>
 * This is the record version mainly used for pattern matching.
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
 * @author Jorge Rico Vivas
 */
public record Tuple5Record<T, U, V, W, X>(T v0, U v1, V v2, W v3,
                                          X v4) {

    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple5<T, U, V, W, X> toClass() {
        return new Tuple5<>(v0, v1, v2, v3, v4);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple5Record<?, ?, ?, ?, ?> that = (Tuple5Record<?, ?, ?, ?, ?>) o;
        return v0.equals(that.v0) && v1.equals(that.v1) && v2.equals(that.v2) && v3.equals(that.v3) && v4.equals(that.v4);
    }

    @Override public int hashCode() {
        int result = v0.hashCode();
        result = 31 * result + v1.hashCode();
        result = 31 * result + v2.hashCode();
        result = 31 * result + v3.hashCode();
        result = 31 * result + v4.hashCode();
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple5Record{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", v4=" + v4 +
                '}';
    }
}