package light;

import java.util.function.Function;

public class Point {
  final int x;
  final int y;
  final int z;

  public Point(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point x(Function<Integer, Integer> f) {
    return new Point(f.apply(x), y, z);
  }

  public Point x(int x) {
    return new Point(x, y, z);
  }

  public Point y(Function<Integer, Integer> f) {
    return new Point(x, f.apply(y), z);
  }

  public Point y(int y) {
    return new Point(x, y, z);
  }

  public Point z(Function<Integer, Integer> f) {
    return new Point(x, y, f.apply(z));
  }

  public Point z(int z) {
    return new Point(x, y, z);
  }

  public Point add(Point p) {
    return new Point(x + p.x, y + p.y, z + p.z);
  }

  public Point scale(int n) {
    return new Point(x * n, y * n, z * n);
  }

  public Point negate() {
    return scale(-1);
  }

  public Point sub(Point p) {
    return add(p.negate());
  }
}
