package light;

import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Consumer;

public class List<A> {
  public A head;
  public List<A> tail;

  public static <A> List<A> empty() {
    return new List<>();
  }

  public static <A> List<A> cons(A head, List<A> tail) {
    var list = new List<A>();
    list.head = head;
    list.tail = tail;
    return list;
  }

  public static <A> List<A> of(A... as) {
    var acc = List.<A>empty();
    for (int i = as.length - 1; i >= 0; i--) {
      acc = List.cons(as[i], acc);
    }
    return acc;
  }

  public boolean isEmpty() {
    return head == null;
  }

  public <B> List<B> map(Function<A, B> f) {
    var acc = List.<B>empty();
    final var write = new Ref<List<B>>(acc);
    foreach(el -> {
      write.a.head = f.apply(el);
      write.a.tail = List.<B>empty();
      write.a = write.a.tail;
    });
    return acc;
  }

  public List<A> filter(Predicate<A> f) {
    var acc = List.<A>empty();
    final var write = new Ref<List<A>>(acc);
    foreach(el -> {
      if (f.test(el)) {
        write.a.head = el;
        write.a.tail = List.<A>empty();
        write.a = write.a.tail;
      }
    });
    return acc;
  }

  public void foreach(Consumer<A> f) {
    for (var self = this; !self.isEmpty(); self = self.tail) {
      f.accept(self.head);
    }
  }

  public <C> C foldl(C start, BiFunction<A, C, C> f) {
    final var acc = new Ref<C>(start);
    foreach(el -> {
      acc.a = f.apply(el, acc.a);
    });
    return acc.a;
  }

  public List<A> unique() {
    HashSet<A> set = new HashSet<A>();
    foreach(el -> set.add(el));
    A[] a = (A[]) new Object[] {};
    return of(set.<A>toArray(a));
  }

  @Override
  public String toString() {
    return foldl("[", (el, s) -> s + el + ", ") + "]";
  }

  public List<A> append(List<A> xs) {
    List<A> start = empty();
    final var acc = new Ref<List<A>>(start);
    foreach(el -> {
      acc.a.head = el;
      acc.a.tail = empty();
      acc.a = acc.a.tail;
    });
    if (!xs.isEmpty()) {
      acc.a.head = xs.head;
      acc.a.tail = xs.tail;
    }
    return start;
  }
}
