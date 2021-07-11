package light;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
interface FourFunction<A,B,C,D,R> {

    R apply(A a, B b, C c, D d);

    default <V> FourFunction<A, B, C, D, V> andThen(
                                Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
    }
}