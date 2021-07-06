package light;

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
    return "" + first + ": " + second;
  }
}
