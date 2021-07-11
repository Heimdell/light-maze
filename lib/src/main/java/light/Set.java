package light;

public class Set<K extends Comparable<K>> {
  final Map<K, Object> raw;

  private Set(Map<K, Object> raw) {
    this.raw = raw;
  }

  public static <K extends Comparable<K>> Set<K> empty() {
    return of(Map.<K, Object>empty());
  }

  public static <K extends Comparable<K>> Set<K> of(List<K> list) {
    var mapped = list.map(k -> Pair.of(k, null));
    var map    = Map.of(mapped);
    return new Set<K>((Map<K, Object>) map);
  }

  public static <K extends Comparable<K>, V> Set<K> of(Map<K, V> map) {
    return new Set<K>((Map<K, Object>) map);
  }

  public Set<K> merge(Set<K> set) {
    return new Set<K>(raw.merge(set.raw));
  }

  public List<K> toList() {
    return raw.toList().map(p -> p.first);
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "set " + raw.toString();
  }
}
