package light;

import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

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

  private Map(int size, K k, V v, Map<K, V> left, Map<K, V> right) {
    this.size = size;
    this.k = k;
    this.v = v;
    this.left = left;
    this.right = right;
  }

  public static <K extends Comparable<K>, V> Map<K, V> empty() {
    return new Map<K, V>(0, null, null, null, null);
  }

  public static <K extends Comparable<K>, V> Map<K, V> pair(K k, V v) {
    return new Map<K,V>(1, k, v, empty(), empty());
  }

  public Maybe<V> lookup(K k) {
    for (var self = this; !self.isEmpty();) {
      final var cmp = k.compareTo(self.k);
      if (cmp < 0) self = self.left;
      if (cmp > 0) self = self.right;
      return Maybe.just(self.v);
    }
    return Maybe.nothing();
  }

  public boolean member(K k) {
    return lookup(k).isJust();
  }

  public Map<K, V> insert(K k, V v) {
    final var cmp = k.compareTo(this.k);
    if (cmp < 0) {
      return balance()
    }
  }

/*

insert :: Ord k => k -> a -> Map k a -> Map k a
insert k
kx0 = go kx0 kx0
  where
    -- Unlike insertR, we only get sharing here
    -- when the inserted value is at the same address
    -- as the present value. We try anyway; this condition
    -- seems particularly likely to occur in 'union'.
    go :: Ord k => k -> k -> a -> Map k a -> Map k a
    go orig !_  x Tip = singleton (lazy orig) x
    go orig !kx x t@(Bin sz ky y l r) =
        case compare kx ky of
            LT | l' `ptrEq` l -> t
               | otherwise -> balanceL ky y l' r
               where !l' = go orig kx x l
            GT | r' `ptrEq` r -> t
               | otherwise -> balanceR ky y l r'
               where !r' = go orig kx x r
            EQ | x `ptrEq` y && (lazy orig `seq` (orig `ptrEq` ky)) -> t
               | otherwise -> Bin sz (lazy orig) x l r

-- The balance function is equivalent to the following:
--
--   balance :: k -> a -> Map k a -> Map k a -> Map k a
--   balance k x l r
--     | sizeL + sizeR <= 1    = Bin sizeX k x l r
--     | sizeR > delta*sizeL   = rotateL k x l r
--     | sizeL > delta*sizeR   = rotateR k x l r
--     | otherwise             = Bin sizeX k x l r
--     where
--       sizeL = size l
--       sizeR = size r
--       sizeX = sizeL + sizeR + 1
--
--   rotateL :: a -> b -> Map a b -> Map a b -> Map a b
--   rotateL k x l r@(Bin _ _ _ ly ry) | size ly < ratio*size ry = singleL k x l r
--                                     | otherwise               = doubleL k x l r
--
--   rotateR :: a -> b -> Map a b -> Map a b -> Map a b
--   rotateR k x l@(Bin _ _ _ ly ry) r | size ry < ratio*size ly = singleR k x l r
--                                     | otherwise               = doubleR k x l r
--
--   singleL, singleR :: a -> b -> Map a b -> Map a b -> Map a b
--   singleL k1 x1 t1 (Bin _ k2 x2 t2 t3)  = bin k2 x2 (bin k1 x1 t1 t2) t3
--   singleR k1 x1 (Bin _ k2 x2 t1 t2) t3  = bin k2 x2 t1 (bin k1 x1 t2 t3)
--
--   doubleL, doubleR :: a -> b -> Map a b -> Map a b -> Map a b
--   doubleL k1 x1 t1 (Bin _ k2 x2 (Bin _ k3 x3 t2 t3) t4) = bin k3 x3 (bin k1 x1 t1 t2) (bin k2 x2 t3 t4)
--   doubleR k1 x1 (Bin _ k2 x2 t1 (Bin _ k3 x3 t2 t3)) t4 = bin k3 x3 (bin k2 x2 t1 t2) (bin k1 x1 t3 t4)
*/

  // private List<Pair<K, V>> raw;

  // public static <K, V> Map<K, V> empty() {
  //   var m = new Map<K, V>();
  //   m.raw = List.empty();
  //   return m;
  // }

  // public static <K, V> Map<K, V> makeFrom(List<Pair<K, V>> raw) {
  //   var m = Map.<K, V>empty();
  //   m.raw = raw;
  //   return m;
  // }

  // public static <K, V> Map<K, V> of(Pair<K, V>... as) {
  //   return makeFrom(List.of(as));
  // }

  // public Map<K, V> insert(K k, V v) {
  //   return makeFrom(
  //     List.<Pair<K, V>>cons(
  //       Pair.of(k, v),
  //       delete(k).raw
  //     )
  //   );
  // }

  // public Map<K, V> change(K k, Maybe<V> v) {
  //   return v.isNothing() ? delete(k) : insert(k, v.fromJust);
  // }

  // public Map<K, V> delete(K k) {
  //   return makeFrom(raw.filter(p -> p.first.hashCode() != k.hashCode()));
  // }

  // public Maybe<V> lookup(K k) {
  //   var slice = raw.filter(p -> p.first.hashCode() == k.hashCode());
  //   if (slice.isEmpty()) {
  //     return Maybe.<V>nothing();
  //   } else {
  //     return Maybe.<V>just(slice.head.second);
  //   }
  // }

  // public <W> Map<K, W> map(BiFunction<K, V, W> f) {
  //   return makeFrom(
  //     raw.map(p ->
  //       Pair.of(p.first, f.apply(p.first, p.second))
  //     )
  //   );
  // }

  // public Map<K, V> filter(BiPredicate<K, V> f) {
  //   return makeFrom(
  //     raw.filter(p ->
  //       f.test(p.first, p.second)
  //     )
  //   );
  // }

  // public <C> C foldlWithKey(C start, TriFunction<K, V, C, C> f) {
  //   return raw.foldl(start, (p, acc) -> f.apply(p.first, p.second, acc));
  // }

  // public Map<K, V> leftJoin(Map<K, V> m, BiFunction<V, V, Maybe<V>> op) {
  //   return foldlWithKey(Map.<K, V>empty(), (k, v, acc) -> {
  //     var v1 = m.lookup(k);
  //     if (v1.isNothing()) {
  //       return acc.insert(k, v);
  //     } else {
  //       return acc.change(k, op.apply(v, v1.fromJust));
  //     }
  //   });
  // }

  // public List<K> keys() {
  //   return foldlWithKey(List.empty(), (k, v, list) -> List.cons(k, list));
  // }

  // public Map<K, V> crossJoin(Map<K, V> m, BiFunction<Maybe<V>, Maybe<V>, Maybe<V>> f) {
  //   var ks = keys().append(m.keys()).unique();
  //   return ks.foldl(Map.empty(), (k, res) -> res.change(k, f.apply(lookup(k), m.lookup(k))));
  // }

  // @Override
  // public String toString() {
  //   return "map " + raw;
  // }
}
