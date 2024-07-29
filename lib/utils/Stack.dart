import 'dart:collection';

import 'dart:convert';

////Created by saurabh.lahoti on 14/05/21
class DSStack<T> {
  final _stack = Queue<T?>();

  void push(T element) {
    _stack.addLast(element);
  }

  T? pop() {
    final T? lastElement = _stack.last;
    if (lastElement != null) {
      _stack.removeLast();
    }
    return lastElement;
  }

  T? top() {
    final T? lastElement = _stack.last;
    return lastElement;
  }

  void clear() {
    _stack.clear();
  }

  int get length => _stack.length;

  bool get isEmpty => _stack.isEmpty;

  bool contains(T item) {
    bool found = false;
    var iterator = _stack.iterator;
    do {
      T? current = iterator.current;
      if (current != null && current == item) {
        found = true;
        break;
      }
    } while (iterator.moveNext());
    return found;
  }

  dynamic toJson(){
    return jsonEncode(_stack);
  }
}
