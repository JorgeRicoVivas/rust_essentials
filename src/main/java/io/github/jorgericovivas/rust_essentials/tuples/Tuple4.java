package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 4 non-null values, all of them with possibly different types.
 *
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @param <W> Fourth value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple4<T, U, V, W> {

    /**
     * First value.
     */
    public T v0;
    /**
     * Second value.
     */
    public U v1;
    /**
     * Third value.
     */
    public V v2;
    /**
     * Fourth value.
     */
    public W v3;

    /**
     * Creates a tuple with said values.
     *
     * @param v0 First value.
     * @param v1 Second value.
     * @param v2 Third value.
     * @param v3 Fourth value.
     */
    public Tuple4(T v0, U v1, V v2, W v3) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple4Record<T, U, V, W> toRecord() {
        return new Tuple4Record<>(v0, v1, v2, v3);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple4Record<T, U, V, W> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(v0, tuple4.v0) && Objects.equals(v1, tuple4.v1) && Objects.equals(v2, tuple4.v2) && Objects.equals(v3, tuple4.v3);
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(v0);
        result = 31 * result + Objects.hashCode(v1);
        result = 31 * result + Objects.hashCode(v2);
        result = 31 * result + Objects.hashCode(v3);
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple4{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                '}';
    }
}
