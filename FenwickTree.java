public class FenwickTree {
  private int n;
  private long[] data;

  FenwickTree() {
    n = 0;
  }

  FenwickTree(int n) {
    this.n = n;
    data = new long[n];
  }

  void add(int p, long x) {
    assert (0 <= p && p < n);
    p++;
    while (p <= n) {
      data[p - 1] += x;
      p += p & -p;
    }
  }

  long sum(int l, int r) {
    assert (0 <= l && l <= r && r <= n);
    return sum(r) - sum(l);
  }

  private long sum(int r) {
    long s = 0;
    while (r > 0) {
      s += data[r - 1];
      r -= r & -r;
    }
    return s;
  }
}