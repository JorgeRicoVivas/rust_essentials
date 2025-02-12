package io.github.jorgericovivas.rust_essentials.tuples;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A tuple containing 3 non-null values, all of them with possibly different types.
 *
 * @param <T> First value type.
 * @param <U> Second value type.
 * @param <V> Third value type.
 * @author Jorge Rico Vivas
 */
public final class Tuple3<T, U, V> {

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
     * Creates a tuple with said values.
     *
     * @param v0 First value.
     * @param v1 Second value.
     * @param v2 Third value.
     */
    public Tuple3(T v0, U v1, V v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple3Record<T, U, V> toRecord() {
        return new Tuple3Record<>(v0, v1, v2);
    }

    /**
     * Turns this tuple into its record representation.
     *
     * @return this tuple as a record.
     */
    public @NotNull Tuple3Record<T, U, V> record() {
        return toRecord();
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(v0, tuple3.v0) && Objects.equals(v1, tuple3.v1) && Objects.equals(v2, tuple3.v2);
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(v0);
        result = 31 * result + Objects.hashCode(v1);
        result = 31 * result + Objects.hashCode(v2);
        return result;
    }

    @Override @NotNull public String toString() {
        return "Tuple3{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
