import java.util.ArrayList;

public class dsu {
  private int n;
  private int[] parentOrSize;

  public dsu() {
    n = 0;
  }

  public dsu(int n) {
    this.n = n;
    this.parentOrSize = new int[n];

    for (int i = 0; i < n; i++) {
      parentOrSize[i] = -1;
    }
  }

  int merge(int a, int b) {
    assert (0 <= a && a < n);
    assert (0 <= b && b < n);

    int x = leader(a), y = leader(b);
    if (x == y)
      return x;
    if (-parentOrSize[x] < parentOrSize[y]) {
      int tmp = x;
      x = y;
      y = tmp;
    }
    parentOrSize[x] += parentOrSize[y];
    parentOrSize[y] = x;
    return x;
  }

  boolean same(int a, int b) {
    assert (0 <= a && a < n);
    assert (0 <= b && b < n);

    return leader(a) == leader(b);
  }

  int leader(int a) {
    assert (0 <= a && a < n);
    if (parentOrSize[a] < 0)
      return a;
    return parentOrSize[a] = leader(parentOrSize[a]);
  }

  int size(int a) {
    assert (0 <= a && a < n);
    return -parentOrSize[leader(a)];
  }

  ArrayList<ArrayList<Integer>> groups() {
    int[] leaderBuf = new int[n];
    int[] groupSize = new int[n];
    
    for(int i = 0; i < n; i++){
      leaderBuf[i] = leader(i);
      groupSize[leaderBuf[i]]++;
    }

    ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
    for(int i = 0; i < n; i++){
      result.get(i).ensureCapacity(groupSize[i]);
    }
    for(int i = 0; i < n; i++){
      result.get(leaderBuf[i]).add(i);
    }

    result.removeIf(e -> e.size() == 0);

    return groups();
  }
}
