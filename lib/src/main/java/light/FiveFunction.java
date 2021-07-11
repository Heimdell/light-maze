package light;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
interface FiveFunction<A,B,C,D,E,R> {

    R apply(A a, B b, C c, D d, E e);

    default <V> FiveFunction<A, B, C, D, E, V> andThen(
                                Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d, E e) -> after.apply(apply(a, b, c, d, e));
    }
}
