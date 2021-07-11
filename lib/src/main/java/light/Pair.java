package light;

import java.util.function.Function;

public class Pair<A, B> {
  public A first;
  public B second;

  public static <A, B> Pair<A, B> of(A first, B second) {
    var p = new Pair<A, B>();
    p.first = first;
    p.second = second;
    return p;
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  public <C> Pair<C, B> first(Function<A, C> f) {
    return Pair.of(f.apply(first), second);
  }

  public <C> Pair<A, C> second(Function<B, C> f) {
    return Pair.of(first, f.apply(second));
  }
}
