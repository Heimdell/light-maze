package light;

public enum Direction {
  PX, NX, PY, NY, PZ, NZ;

  public Point toOffset() {
    switch (this) {
      case NX: return new Point(-1,  0,  0);
      case NY: return new Point( 0, -1,  0);
      case NZ: return new Point( 0,  0, -1);
      case PX: return new Point(+1,  0,  0);
      case PY: return new Point( 0, +1,  0);
      case PZ: return new Point( 0,  0, +1);
      default: return new Point( 0,  0,  0);
    }
  }
}
