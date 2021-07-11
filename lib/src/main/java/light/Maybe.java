package light;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Maybe<A> {
  A fromJust;

  public static <A> Maybe<A> nothing() {
    return new Maybe<>();
  }

  public static <A> Maybe<A> just(A a) {
    var mb = new Maybe<A>();
    mb.fromJust = a;
    return mb;
  }

  public boolean isJust() {
    return fromJust != null;
  }

  public boolean isNothing() {
    return !isJust();
  }

  public A orElse(A a) {
    return fold(() -> a, x -> x);
  }

  public <C> C fold(Supplier<C> c, Function<A, C> f) {
    return isJust() ? f.apply(fromJust) : c.get();
  }

  @Override
  public String toString() {
    return isNothing()? "Nothing" : "Just(" + fromJust + ")";
  }

  public <B> Maybe<B> then(Function<A, Maybe<B>> k) {
    return fold(() -> Maybe.nothing(), k);
  }

  public static <A, B> Function<A, Maybe<B>> guard(Function<A, B> step, Predicate<A> stop) {
    return a -> stop.test(a) ? Maybe.nothing() : Maybe.just(step.apply(a));
  }
}
