package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 3 non-null values, all of them with possibly different types.
 * <p>
 * This is the record version mainly used for pattern matching.
 *
 * @param v0  First value.
 * @param v1  Second value.
 * @param v2  Third value.
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @author Jorge Rico Vivas
 */
public record Tuple3Record<T, U, V>(T v0, U v1, V v2) {

    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple3<T, U, V> toClass() {
        return new Tuple3<>(v0, v1, v2);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3Record<?, ?, ?> that = (Tuple3Record<?, ?, ?>) o;
        return v0.equals(that.v0) && v1.equals(that.v1) && v2.equals(that.v2);
    }

    @Override public int hashCode() {
        int result = v0.hashCode();
        result = 31 * result + v1.hashCode();
        result = 31 * result + v2.hashCode();
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple3Record{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}