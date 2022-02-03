import java.util.ArrayList;

public class InternalQueue<T> {
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
