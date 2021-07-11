package light;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// a port from https://hackage.haskell.org/package/containers-0.6.5.1/docs/src/Data.Map.Internal.html#Map

public class Map<K extends Comparable<K>, V> {
  final int size;
  final K k;
  final V v;
  final Map<K, V> left;
  final Map<K, V> right;

  public boolean isEmpty() {
    return size == 0;
  }

  private Map(K k, V v, Map<K, V> left, Map<K, V> right) {
    this.size  = 1 + left.size + right.size;
    this.k     = k;
    this.v     = v;
    this.left  = left;
    this.right = right;
  }

  private Map() {
    this.size  = 0;
    this.k     = null;
    this.v     = null;
    this.left  = null;
    this.right = null;
  }

  public static <K extends Comparable<K>, V> Map<K, V> empty() {
    return new Map<K, V>();
  }

  public static <K extends Comparable<K>, V> Map<K, V> of(K k, V v) {
    return new Map<K,V>(k, v, empty(), empty());
  }

  public static <K extends Comparable<K>, V> Map<K, V> of(K k, V v, Map<K, V> l, Map<K, V> r) {
    return new Map<K,V>(k, v, l, r);
  }

  public boolean member(K k) {
    return lookup(k).isJust();
  }

  public Map<K, V> insert(K k, V v) {
    return match(
      () -> of(k, v, empty(), empty()),
      (size, k1, v1, left, right) -> {
        switch (Compare.the(k, k1)) {
          case LT: return rebalance(k1, v1, left.insert(k, v), right);
          case GT: return rebalance(k1, v1, left,              right.insert(k, v));
          default: return of       (k,  v,  left,              right);
        }
      }
    );
  }

  public Map<K, V> delete(K k) {
    return match(
      () -> empty(),
      (size, k1, v1, left, right) -> {
        switch (Compare.the(k, k1)) {
          case LT: return rebalance(k1, v1, left.delete(k), right);
          case GT: return rebalance(k1, v1, left,           right.delete(k));
          default: return glue(left, right);
        }
      }
    );
  }

  public static <K extends Comparable<K>, V> Map<K, V> glue(Map<K, V> left, Map<K, V> right) {
    if (left .isEmpty()) return right;
    if (right.isEmpty()) return left;
    if (left.size > right.size) {
      var maxView = left.maxView();
      return rebalance(maxView.first, maxView.second.first, maxView.second.second, right);
    } else {
      var minView = right.minView();
      return rebalance(minView.first, minView.second.first, left, minView.second.second);
    }
  }

  private Pair<K, Pair<V, Map<K, V>>> maxView() {
    if (right.isEmpty()) return Pair.of(k, Pair.of(v, left));
    var view = right.maxView();
    return view.second(p -> p.second(r -> rebalance(k, v, left, r)));
  }

  private Pair<K, Pair<V, Map<K, V>>> minView() {
    if (left.isEmpty()) return Pair.of(k, Pair.of(v, right));
    var view = left.minView();
    return view.second(p -> p.second(l -> rebalance(k, v, l, right)));
  }

  public Map<K, V> change(K k, Maybe<V> v) {
    return v.fold(
      () -> delete(k),
      v1 -> insert(k, v1)
    );
  }

  public <C> C match(Supplier<C> tip, FiveFunction<Integer, K, V, Map<K, V>, Map<K, V>, C> bin) {
    if (isEmpty()) {
      return tip.get();
    } else {
      return bin.apply(size, k, v, left, right);
    }
  }

  private <C> C matchBin(FiveFunction<Integer, K, V, Map<K, V>, Map<K, V>, C> bin) {
    if (isEmpty()) {
      return null; // shouldn't happen
    } else {
      return bin.apply(size, k, v, left, right);
    }
  }

  private static int delta = 3;
  private static int ratio = 2;

  private static <K extends Comparable<K>, V> Map<K, V> rebalance(K k, V v, Map<K, V> l, Map<K, V> r) {
    if (l.size + r.size < 3)     return new Map<K, V>(k, v, l, r);
    if (l.size > delta * r.size) return rotateRight(k, v, l, r);
    if (r.size > delta * l.size) return rotateLeft (k, v, l, r);
    return new Map<K, V>(k, v, l, r);
  }

  private static <K extends Comparable<K>, V> Map<K, V> rotateLeft(K k1, V v1, Map<K, V> l1, Map<K, V> r1) {
    return
      r1.matchBin((s2, k2, v2, l2, r2) ->
        l2.size < ratio * r2.size
          ? new Map<K, V>(k2, v2, new Map<K, V>(k1, v1, l1, l2), r2)
          : r2.matchBin((s3, k3, v3, l3, r3) ->
              new Map<K, V>(k2, v2,
                new Map<K, V>(k1, v1, l1, l2),
                new Map<K, V>(k3, v3, l3, r3)
              )
            )
      );
  }

  private static <K extends Comparable<K>, V> Map<K, V> rotateRight(K k1, V v1, Map<K, V> l1, Map<K, V> r1) {
    return
      l1.matchBin((s2, k2, v2, l2, r2) ->
        r2.size < ratio * l2.size
          ? new Map<K, V>(k2, v2, new Map<K, V>(k1, v1, l2, r2), r1)
          : l2.matchBin((s3, k3, v3, l3, r3) ->
              new Map<K, V>(k2, v2,
                new Map<K, V>(k1, v1, l3, r3),
                new Map<K, V>(k3, v3, r2, r1)
              )
            )
      );
  }

  public static <K extends Comparable<K>, V> Map<K, V> of(List<Pair<K, V>> raw) {
    return raw.foldl(empty(), (p, m) -> m.insert(p.first, p.second));
  }

  @SafeVarargs
  public static <K extends Comparable<K>, V> Map<K, V> of(Pair<K, V>... as) {
    return of(List.of(as));
  }

  public Integer foreach(BiFunction<K, V, Integer> step) {
    return match(
      () -> 0,
      (Integer s, K k, V v, Map<K, V> l, Map<K, V> r) -> {
        l.foreach(step);
        step.apply(k, v);
        r.foreach(step);
        return 0;
      }
    );
  }

  public Integer foreachBack(BiFunction<K, V, Integer> step) {
    return match(
      () -> 0,
      (Integer s, K k, V v, Map<K, V> l, Map<K, V> r) -> {
        r.foreachBack(step);
        step.apply(k, v);
        l.foreachBack(step);
        return 0;
      }
    );
  }

  public Map<K, V> filterWithKey(BiPredicate<K, V> p) {
    Ref<Map<K, V>> acc = Ref.of(empty());
    foreach((k, v) -> {
      if (p.test(k, v)) {
        acc.a = acc.a.insert(k, v);
      }
      return 0;
    });
    return acc.a;
  }

  public Map<K, V> filter(Predicate<V> p) {
    return filterWithKey((k, v) -> p.test(v));
  }

  public <C> C foldlWithKey(C start, TriFunction<K, V, C, C> step) {
    Ref<C> acc = Ref.of(start);
    foreach((k, v) -> {
      acc.a = step.apply(k, v, acc.a);
      return 0;
    });
    return acc.a;
  }

  public <C> C foldl(C start, BiFunction<V, C, C> step) {
    return foldlWithKey(start, (k, v, c) -> step.apply(v, c));
  }

  public <C> C fold(C tip, FourFunction<K, V, C, C, C> reducer) {
    return match(
      () -> tip,
      (s, k, v, l, r) -> reducer.apply(
        k, v,
        l.fold(tip, reducer),
        r.fold(tip, reducer)
      )
    );
  }

  public <V1> Map<K, V1> mapWithKey(BiFunction<K, V, V1> f) {
    return fold(
      empty(),
      (k, v, l, r) -> new Map<>(k, f.apply(k, v), l, r)
    );
  }

  public <V1> Map<K, V1> map(Function<V, V1> f) {
    return mapWithKey((k, v) -> f.apply(v));
  }

  public Maybe<V> lookup(K k1) {
    return match(
      () -> Maybe.<V>nothing(),
      (s, k, v, l, r) -> {
        switch (Compare.the(k1, k)) {
          case LT: return l.lookup(k1);
          case GT: return r.lookup(k1);
          default: return Maybe.just(v);
        }
      }
    );
  }

  public Integer height() {
    return fold(0, (k, v, l, r) -> Math.max(l, r) + 1);
  }

  @Override
  public String toString() {
    return "{" + foldlWithKey("", (k, v, s) -> s + k + ": " + v + ", ") + "}";
  }

  public Set<K> keys() {
    return Set.of(this);
  }

  public Map<K, V> mergeWith(Map<K, V> map, BiFunction<V, V, V> merger) {
    return mergeWithKey(map, (k, v1, v) -> merger.apply(v1, v));
  }

  public Map<K, V> merge(Map<K, V> map) {
    return mergeWith(map, (v1, v) -> v);
  }

  public Map<K, V> mergeWithKey(Map<K, V> map, TriFunction<K, V, V, V> merger) {
    return map.foldlWithKey(
      this,
      (k, v, acc) ->
        acc.insert(
          k,
          acc.lookup(k).fold(
            () -> v,
            v1 -> merger.apply(k, v1, v)
          )
        )
    );
  }

  public static <K extends Comparable<K>, V> Map<K, V> tabulate(K k, Function<K, Maybe<K>> genIndex, Function<K, V> tabulator) {
    var acc = Map.<K, V>empty();
    for (var k1 = Maybe.just(k); k1.isJust(); k1 = k1.then(genIndex)) {
      var k2 = k1.fromJust;
      var v  = tabulator.apply(k2);
      acc = acc.insert(k2, v);
    }
    return acc;
  }

  public List<Pair<K, V>> toList() {
    Ref<List<Pair<K, V>>> acc = Ref.of(List.empty());
    foreachBack((k, v) -> {
      acc.a = List.cons(Pair.of(k, v), acc.a);
      return 0;
    });
    return acc.a;
  }
}
