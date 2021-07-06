package light;

import java.util.function.Function;

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
    return fold(a, x -> x);
  }

  public <C> C fold(C c, Function<A, C> f) {
    return isJust() ? f.apply(fromJust) : c;
  }

  @Override
  public String toString() {
    return isNothing()? "Nothing" : "Just(" + fromJust + ")";
  }
}
