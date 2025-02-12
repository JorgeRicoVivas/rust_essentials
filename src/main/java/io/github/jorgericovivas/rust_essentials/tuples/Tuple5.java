package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 5 non-null values, all of them with possibly different types.
 *
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @param <W> Fourth value type.
 * @param <X> Fifth value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple5<T, U, V, W, X> {

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
     * Fifth value.
     */
    public X v4;

    /**
     * Creates a tuple with said values.
     *
     * @param v0 First value.
     * @param v1 Second value.
     * @param v2 Third value.
     * @param v3 Fourth value.
     * @param v4 Fifth value.
     */
    public Tuple5(T v0, U v1, V v2, W v3, X v4) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple5Record<T, U, V, W, X> toRecord() {
        return new Tuple5Record<>(v0, v1, v2, v3, v4);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple5Record<T, U, V, W, X> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(v0, tuple5.v0) && Objects.equals(v1, tuple5.v1) && Objects.equals(v2, tuple5.v2) && Objects.equals(v3, tuple5.v3) && Objects.equals(v4, tuple5.v4);
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(v0);
        result = 31 * result + Objects.hashCode(v1);
        result = 31 * result + Objects.hashCode(v2);
        result = 31 * result + Objects.hashCode(v3);
        result = 31 * result + Objects.hashCode(v4);
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple5{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", v4=" + v4 +
                '}';
    }
}
