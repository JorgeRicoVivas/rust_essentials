package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 7 non-null values, all of them with possibly different types.
 *
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @param <W> Fourth value type.
 * @param <X> Fifth value type.
 * @param <Y> Sixth value type.
 * @param <Z> Seventh value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple7<T, U, V, W, X, Y, Z> {

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
     * Sixth value.
     */
    public Y v5;
    /**
     * Seventh value.
     */
    public Z v6;

    /**
     * Creates a tuple with said values.
     *
     * @param v0 First value.
     * @param v1 Second value.
     * @param v2 Third value.
     * @param v3 Fourth value.
     * @param v4 Fifth value.
     * @param v5 Sixth value.
     * @param v6 Seventh value.
     */
    public Tuple7(T v0, U v1, V v2, W v3, X v4, Y v5, Z v6) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
        this.v6 = v6;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple7Record<T, U, V, W, X, Y, Z> toRecord() {
        return new Tuple7Record<>(v0, v1, v2, v3, v4, v5, v6);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple7Record<T, U, V, W, X, Y, Z> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7 = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(v0, tuple7.v0) && Objects.equals(v1, tuple7.v1) && Objects.equals(v2, tuple7.v2) && Objects.equals(v3, tuple7.v3) && Objects.equals(v4, tuple7.v4) && Objects.equals(v5, tuple7.v5) && Objects.equals(v6, tuple7.v6);
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(v0);
        result = 31 * result + Objects.hashCode(v1);
        result = 31 * result + Objects.hashCode(v2);
        result = 31 * result + Objects.hashCode(v3);
        result = 31 * result + Objects.hashCode(v4);
        result = 31 * result + Objects.hashCode(v5);
        result = 31 * result + Objects.hashCode(v6);
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple7{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", v4=" + v4 +
                ", v5=" + v5 +
                ", v6=" + v6 +
                '}';
    }
}
