import java.util.ArrayList;

/**
 * require InternalQueue, InternalPair
 */
public class MaxFlow {
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
