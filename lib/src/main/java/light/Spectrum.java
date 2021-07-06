package light;

public class Spectrum {

  public Map<Color, Integer> raw;

  public static Spectrum empty() {
    var s = new Spectrum();
    s.raw = Map.empty();
    return s;
  }

  public static Spectrum pure(Color c, Integer i) {
    return of(Pair.of(c, i));
  }

  public static Spectrum of(Pair<Color, Integer>... colors) {
    return makeFrom(Map.makeFrom(List.of(colors)));
  }

  public static Spectrum makeFrom(Map<Color, Integer> raw) {
    var s = empty();
    s.raw = raw;
    return s;
  }

  public Spectrum half() {
    return makeFrom(raw.map((k, v) -> v / 2));
  }

  public Spectrum add(Spectrum s) {
    return makeFrom(raw.crossJoin(s.raw, (mv, mv1) -> {
      var v = mv.orElse(0);
      var v1 = mv1.orElse(0);
      var vN = v + v1;
      return vN == 0 ? Maybe.nothing() : Maybe.just(vN);
    }));
  }

  @Override
  public String toString() {
    return raw.toString();
  }
}
