/**
 * The following package adapts some of Rust features to Java, allowing to create more readable, maintainable, and
 * secure code, the following features have been adapted this far:
 * <p>
 * - {@link Option}&lt;T&gt;: Represents optional values, where every {@link Option} is either {@link Some}&lt;T&gt;
 * and contains a non-null value, or {@link None} when it contains no value.
 * <p>
 * - {@link Result}&lt;T, E&gt;: Represents the result of executing an operation, being either an {@link Ok}&lt;T&gt;
 * containing a value, or {@link Err}&lt;E&gt; representing the operation couldn't finish due to some condition, and
 * contains a value that usually tells information about what happened.
 * <p>
 * In Java, most of {@link Err} would usually contain {@link Exception}s.
 * <p>
 * - {@link Tuples} represent collections of values of different types, like a tuple of three values such as
 * {@link Tuple3}&lt;{@link String}, {@link Integer}, {@link Double}&gt;, but due to limitations, tuples can only be up
 * to 7 values.
 * <p>
 * <br>
 * This is not in any way related to the real <a href="https://www.rust-lang.org/">Rust programming language</a>.
 */
package io.github.jorgericovivas.rust_essentials;

import io.github.jorgericovivas.rust_essentials.option.Option;
import io.github.jorgericovivas.rust_essentials.option.Some;
import io.github.jorgericovivas.rust_essentials.option.None;
import io.github.jorgericovivas.rust_essentials.result.Result;
import io.github.jorgericovivas.rust_essentials.result.Ok;
import io.github.jorgericovivas.rust_essentials.result.Err;
import io.github.jorgericovivas.rust_essentials.tuples.Tuples;
import io.github.jorgericovivas.rust_essentials.tuples.Tuple3;