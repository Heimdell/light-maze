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

  @SafeVarargs
  public static Spectrum of(Pair<Color, Integer>... colors) {
    return of(Map.of(List.of(colors)));
  }

  public static Spectrum of(Map<Color, Integer> raw) {
    var s = empty();
    s.raw = raw;
    return s;
  }

  public Spectrum half() {
    return of(raw.map(v -> v / 2));
  }

  public Spectrum add(Spectrum s) {
    return of(raw.mergeWith(s.raw, (mv, mv1) -> mv + mv1).filter(x -> x != 0));
  }

  public Pair<Spectrum, Spectrum> split(Color color) {
    Map<Color, Integer> with = raw.lookup(color).fold(
      ()        -> Map.empty(),
      intensity -> Map.of(Pair.of(color, intensity))
    );

    Map<Color, Integer> without = raw.delete(color);

    return Pair.of(Spectrum.of(with), Spectrum.of(without));
  }

  @Override
  public String toString() {
    return raw.toString();
  }
}
