package light;

public class Ref<A> {
  public A a;

  public Ref(A a) {
    this.a = a;
  }

  public static <A> Ref<A> of(A a) {
    return new Ref<A>(a);
  }
}
