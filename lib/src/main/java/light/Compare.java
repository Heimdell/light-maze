package light;

public enum Compare {
  LT, EQ, GT;

  public static <A extends Comparable<A>> Compare the (A l, A r) {
    var cmp = l.compareTo(r);
    return cmp < 0 ? LT : cmp > 0 ? GT : EQ;
  }
}
