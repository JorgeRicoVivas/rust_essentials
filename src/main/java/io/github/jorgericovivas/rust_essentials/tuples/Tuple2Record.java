package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * A tuple containing 2 non-null values, all of them with possibly different types.
 * <p>
 * This is the record version mainly used for pattern matching.
 *
 * @param v0  First value.
 * @param v1  Second value.
 * @param <T> First value type.
 * @param <U> Second value type.
 * @author Jorge Rico Vivas
 */
public record Tuple2Record<T, U>(T v0, U v1) {


    /**
     * Turns this tuple record into its class representation.
     *
     * @return this tuple record as a standard.
     */
    public @NotNull Tuple2<T, U> toClass() {
        return new Tuple2<>(v0, v1);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2Record<?, ?> that = (Tuple2Record<?, ?>) o;
        return v0.equals(that.v0) && v1.equals(that.v1);
    }

    @Override public int hashCode() {
        int result = v0.hashCode();
        result = 31 * result + v1.hashCode();
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple2Record{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                '}';
    }
}