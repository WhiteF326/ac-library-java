public class math {
  static private long safeMod(long x, long m) {
    x %= m;
    if (x < 0)
      x += m;
    return x;
  }

  static private long internalFloorSum(long n, long m, long a, long b){
    long ans = 0;
    
    while(true){
      if(a >= m){
        ans += n * (n - 1) / 2 * (a / m);
        a %= m;
      }
      if(b >= m){
        ans += n * (b / m);
        b %= m;
      }

      long yMax = a * n + b;
      if(yMax < m) break;

      n = yMax / m;
      b = yMax % m;

      long tmp = m;
      m = a;
      a = tmp;
    }

    return ans;
  }

  static long floorSum(long n, long m, long a, long b) {
    assert (0 <= n && n < (1L << 32));
    assert (0 <= m && m < (1L << 32));

    long ans = 0;
    if (a < 0) {
      long a2 = safeMod(a, m);
      ans -= 1L * n * (n - 1) / 2 * ((a2 - a) / m);
    }
    if (b < 0) {
      long b2 = safeMod(b, m);
      ans -= 1L * n * (n - 1) / 2 * ((b2 - a) / m);
    }
    
    return ans + internalFloorSum(n, m, a, b);
  }
}
