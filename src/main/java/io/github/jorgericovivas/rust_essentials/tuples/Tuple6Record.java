package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 6 non-null values, all of them with possibly different types.
 * <p>
 * This is the record version mainly used for pattern matching.
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
 * @author Jorge Rico Vivas
 */
public record Tuple6Record<T, U, V, W, X, Y>(T v0, U v1, V v2, W v3, X v4, Y v5) {


    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple6<T, U, V, W, X, Y> toClass() {
        return new Tuple6<>(v0, v1, v2, v3, v4, v5);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple6Record<?, ?, ?, ?, ?, ?> that = (Tuple6Record<?, ?, ?, ?, ?, ?>) o;
        return v0.equals(that.v0) && v1.equals(that.v1) && v2.equals(that.v2) && v3.equals(that.v3) && v4.equals(that.v4) && v5.equals(that.v5);
    }

    @Override public int hashCode() {
        int result = v0.hashCode();
        result = 31 * result + v1.hashCode();
        result = 31 * result + v2.hashCode();
        result = 31 * result + v3.hashCode();
        result = 31 * result + v4.hashCode();
        result = 31 * result + v5.hashCode();
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple6Record{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", v4=" + v4 +
                ", v5=" + v5 +
                '}';
    }
}