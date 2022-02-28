import java.util.*;
import java.io.*;

class InternalPair<T, U> {
  T first;
  U second;

  InternalPair(T first, U second) {
    this.first = first;
    this.second = second;
  }
}

class InternalQueue<T> {
  ArrayList<T> payload;
  int pos = 0;

  InternalQueue() {
    reserve(0);
  }

  void reserve(int n) {
    payload = new ArrayList<T>(n);
  }

  int size() {
    return payload.size() - pos;
  }

  boolean empty() {
    return pos == payload.size();
  }

  void push(T t) {
    payload.add(t);
  }

  T front() {
    return payload.get(pos);
  }

  void clear() {
    payload.clear();
    pos = 0;
  }

  void pop() {
    pos++;
  }
}

class MaxFlow {
  private int n;

  class Edge {
    int from, to;
    long cap, flow;

    Edge() {
      //
    }

    Edge(int from, int to, long cap) {
      this.from = from;
      this.to = to;
      this.cap = cap;
      this.flow = 0;
    }

    Edge(int from, int to, long cap, long flow) {
      this.from = from;
      this.to = to;
      this.cap = cap;
      this.flow = flow;
    }
  }

  private class _Edge {
    int to, rev;
    long cap;

    _Edge(int to, int rev, long cap) {
      this.to = to;
      this.rev = rev;
      this.cap = cap;
    }
  }

  private ArrayList<InternalPair<Integer, Integer>> pos;
  private ArrayList<ArrayList<_Edge>> g;

  private ArrayList<Integer> level;
  private ArrayList<Integer> iter;
  InternalQueue<Integer> que;

  MaxFlow() {
    n = 0;
  }

  MaxFlow(int n) {
    this.n = n;
    g = new ArrayList<ArrayList<_Edge>>();
    pos = new ArrayList<InternalPair<Integer, Integer>>();
    for (int i = 0; i < n; i++) {
      g.add(new ArrayList<>());
    }
  }

  int add_edge(int from, int to, long cap) {
    assert (0 <= from && from < n);
    assert (0 <= to && to < n);
    assert (0 <= cap);

    int m = pos.size();
    pos.add(new InternalPair<Integer, Integer>(from, g.get(from).size()));

    int from_id = g.get(from).size();
    int to_id = g.get(to).size();

    if (from == to) {
      to_id++;
    }
    g.get(from).add(new _Edge(to, to_id, cap));
    g.get(to).add(new _Edge(from, from_id, 0));
    return m;
  }

  Edge getEdge(int i) {
    int m = pos.size();
    assert (0 <= i && i < m);
    var e = g.get(pos.get(i).first).get(pos.get(i).second);
    var re = g.get(e.to).get(e.rev);
    return new Edge(pos.get(i).first, e.to, e.cap + re.cap, re.cap);
  }

  ArrayList<Edge> edges() {
    int m = pos.size();
    ArrayList<Edge> result = new ArrayList<>();
    result.ensureCapacity(m);
    for (int i = 0; i < m; i++) {
      result.add(getEdge(i));
    }
    return result;
  }

  void changeEdge(int i, long newCap, long newFlow) {
    int m = pos.size();
    assert (0 <= i && i < m);
    assert (0 <= newFlow && newFlow <= newCap);

    var e = g.get(pos.get(i).first).get(pos.get(i).second);
    g.get(pos.get(i).first).get(pos.get(i).second).cap = newCap - newFlow;
    g.get(e.to).get(e.rev).cap = newFlow;
  }

  long flow(int s, int t) {
    return flow(s, t, Long.MAX_VALUE);
  }

  long flow(int s, int t, long flowLimit) {
    assert (0 <= s && s < n);
    assert (0 <= t && t < n);
    assert (s != t);

    level = new ArrayList<Integer>(n);
    iter = new ArrayList<Integer>(n);
    for (int i = 0; i < n; i++) {
      level.add(0);
      iter.add(0);
    }
    que = new InternalQueue<>();

    long flow = 0;
    while (flow < flowLimit) {
      bfs(s, t);
      if (level.get(t) == -1) {
        break;
      }
      for (int i = 0; i < n; i++) {
        iter.set(i, 0);
      }
      long f = dfs(t, s, flowLimit - flow);
      if (f == 0) {
        break;
      }
      flow += f;
    }
    return flow;
  }

  boolean[] minCut(int s) {
    boolean[] visited = new boolean[n];
    InternalQueue<Integer> que = new InternalQueue<>();

    que.push(s);
    while (!que.empty()) {
      int p = que.front();
      que.pop();

      visited[p] = true;
      for (var e : g.get(p)) {
        if (e.cap != 0 && !visited[e.to]) {
          visited[e.to] = true;
          que.push(e.to);
        }
      }
    }

    return visited;
  }

  private void bfs(int s, int t) {
    for (int i = 0; i < n; i++) {
      level.set(i, -1);
    }
    level.set(s, 0);
    que.clear();
    que.push(s);
    while (!que.empty()) {
      int v = que.front();
      que.pop();
      for (var e : g.get(v)) {
        if (e.cap == 0 || level.get(e.to) >= 0) {
          continue;
        }
        level.set(e.to, level.get(v) + 1);
        if (e.to == t) {
          return;
        }
        que.push(e.to);
      }
    }
  }

  private long dfs(int v, int s, long up) {
    if (v == s) {
      return up;
    }
    long res = 0;
    int levelV = level.get(v);

    for (int i = iter.get(v); i < g.get(v).size(); i++) {
      _Edge e = g.get(v).get(i);
      if (levelV <= level.get(e.to) || g.get(e.to).get(e.rev).cap == 0) {
        continue;
      }
      long d = dfs(e.to, s, Math.min(up - res, g.get(e.to).get(e.rev).cap));
      if (d <= 0)
        continue;
      g.get(v).get(i).cap += d;
      g.get(e.to).get(e.rev).cap -= d;
      res += d;
      if (res == up) {
        return res;
      }
    }
    level.set(v, n);
    return res;
  }
}

class FastScanner {
  private BufferedReader reader = null;
  private StringTokenizer tokenizer = null;

  public FastScanner(InputStream in) {
    reader = new BufferedReader(new InputStreamReader(in));
    tokenizer = null;
  }

  public String next() {
    if (tokenizer == null || !tokenizer.hasMoreTokens()) {
      try {
        tokenizer = new StringTokenizer(reader.readLine());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return tokenizer.nextToken();
  }

  public String nextLine() {
    if (tokenizer == null || !tokenizer.hasMoreTokens()) {
      try {
        return reader.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return tokenizer.nextToken("\n");
  }

  public long nextLong() {
    return Long.parseLong(next());
  }

  public int nextInt() {
    return Integer.parseInt(next());
  }

  public double nextDouble() {
    return Double.parseDouble(next());
  }

  public int[] nextIntArray(int n) {
    int[] a = new int[n];
    for (int i = 0; i < n; i++)
      a[i] = (int) nextInt();
    return a;
  }

  public long[] nextLongArray(int n) {
    long[] a = new long[n];
    for (int i = 0; i < n; i++)
      a[i] = nextLong();
    return a;
  }
}

class FastPrinter implements Closeable {
  private PrintWriter printWriter = null;

  public FastPrinter(OutputStream outputStream) {
    printWriter = new PrintWriter(outputStream);
  }

  void print(boolean x) {
    printWriter.println(x);
  }

  void print(char x) {
    printWriter.print(x);
  }

  void print(char[] x) {
    printWriter.print(x);
  }

  void print(double x) {
    printWriter.print(x);
  }

  void print(float x) {
    printWriter.print(x);
  }

  void print(int x) {
    printWriter.print(x);
  }

  void print(long x) {
    printWriter.print(x);
  }

  void print(Object x) {
    printWriter.print(x);
  }

  void print(String x) {
    printWriter.print(x);
  }

  void printf(String format, Object... args) {
    printWriter.printf(format, args);
  }

  void println(boolean x) {
    printWriter.println(x);
  }

  void println(char x) {
    printWriter.println(x);
  }

  void println(char[] x) {
    printWriter.println(x);
  }

  void println(double x) {
    printWriter.println(x);
  }

  void println(float x) {
    printWriter.println(x);
  }

  void println(int x) {
    printWriter.println(x);
  }

  void println(long x) {
    printWriter.println(x);
  }

  void println(Object x) {
    printWriter.println(x);
  }

  void println(String x) {
    printWriter.println(x);
  }

  void flush() {
    printWriter.flush();
  }

  public void close() {
    printWriter.flush();
    printWriter.close();
  }
}

class Main {
  public static void main(String[] args) {
    FastScanner sc = new FastScanner(System.in);
    FastPrinter out = new FastPrinter(System.out);

    int n = sc.nextInt();
    int m = sc.nextInt();

    String[] grid = new String[n];
    char[][] ansGrid = new char[n][m];
    for (int i = 0; i < n; i++) {
      grid[i] = sc.nextLine();
      for (int j = 0; j < m; j++) {
        ansGrid[i][j] = grid[i].charAt(j);
      }
    }

    MaxFlow g = new MaxFlow(n * m + 2);
    int s = n * m, t = n * m + 1;

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (grid[i].charAt(j) == '#') {
          continue;
        }
        int v = i * m + j;
        if ((i + j) % 2 == 0) {
          g.add_edge(s, v, 1);
        } else {
          g.add_edge(v, t, 1);
        }
      }
    }

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if ((i + j) % 2 > 0 || grid[i].charAt(j) == '#') {
          continue;
        }
        int v0 = i * m + j;
        if (i > 0 && grid[i - 1].charAt(j) == '.') {
          int v1 = (i - 1) * m + j;
          g.add_edge(v0, v1, 1);
        }
        if (j > 0 && grid[i].charAt(j - 1) == '.') {
          int v1 = i * m + (j - 1);
          g.add_edge(v0, v1, 1);
        }
        if (i + 1 < n && grid[i + 1].charAt(j) == '.') {
          int v1 = (i + 1) * m + j;
          g.add_edge(v0, v1, 1);
        }
        if (j + 1 < m && grid[i].charAt(j + 1) == '.') {
          int v1 = i * m + (j + 1);
          g.add_edge(v0, v1, 1);
        }
      }
    }

    out.println(g.flow(s, t));

    var edges = g.edges();
    for (var e : edges) {
      if (e.from == s || e.to == t || e.flow == 0) {
        continue;
      }
      int i0 = e.from / m;
      int j0 = e.from % m;
      int i1 = e.to / m;
      int j1 = e.to % m;

      if (i0 == i1 + 1) {
        ansGrid[i1][j1] = 'v';
        ansGrid[i0][j0] = '^';
      } else if (j0 == j1 + 1) {
        ansGrid[i1][j1] = '>';
        ansGrid[i0][j0] = '<';
      } else if (i0 == i1 - 1) {
        ansGrid[i0][j0] = 'v';
        ansGrid[i1][j1] = '^';
      } else {
        ansGrid[i0][j0] = '>';
        ansGrid[i1][j1] = '<';
      }
    }

    for (int i = 0; i < n; i++) {
      StringBuilder stbl = new StringBuilder();
      stbl.append(ansGrid[i]);
      out.println(stbl.toString());
    }

    out.close();
  }
}
