package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 2 non-null values, all of them with possibly different types.
 *
 * @param <T> First value type.
 * @param <U> Second value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple2<T, U> {

    /**
     * First value.
     */
    public T v0;
    /**
     * Second value.
     */
    public U v1;

    /**
     * Creates a tuple with said values.
     *
     * @param v0 First value.
     * @param v1 Second value.
     */
    public Tuple2(T v0, U v1) {
        this.v0 = v0;
        this.v1 = v1;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple2Record<T, U> toRecord() {
        return new Tuple2Record<>(v0, v1);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple2Record<T, U> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(v0, tuple2.v0) && Objects.equals(v1, tuple2.v1);
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(v0);
        result = 31 * result + Objects.hashCode(v1);
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple2{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                '}';
    }
}
