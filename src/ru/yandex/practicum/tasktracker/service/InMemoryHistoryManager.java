package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

  private final Map<Integer, Node> nodes = new HashMap<>();
  private Node head;
  private Node tail;


  @Override
  public void add(Task task) {
    if (task == null) {
      return;
    }
    remove(task.getId());
    linkLast(task);
  }

  @Override
  public void remove(int id) {
    Node nodeToRemove = nodes.remove(id);
    removeNode(nodeToRemove);
  }

  @Override
  public List<Task> getHistory() {
    return getTasks();
  }

  private void linkLast(Task task) {
    final Node oldTail = tail;
    final Node newNode = new Node(oldTail, task, null);
    tail = newNode;
    if (oldTail == null) {
      head = newNode;
    } else {
      oldTail.next = newNode;
    }
    nodes.put(task.getId(), newNode);
  }

  private List<Task> getTasks() {
    final List<Task> history = new ArrayList<>();
    Node current = head;
    while (current != null) {
      history.add(current.data);
      current = current.next;
    }
    return history;
  }

  private void removeNode(Node node) {
    if (node == null) {
      return;
    }

    final Node nodeAfter = node.next;
    final Node nodeBefore = node.prev;

    if (nodeBefore == null) {
      head = nodeAfter;
    } else {
      nodeBefore.next = nodeAfter;
      node.prev = null;
    }

    if (nodeAfter == null) {
      tail = nodeBefore;
    } else {
      nodeAfter.prev = nodeBefore;
      node.next = null;
    }
  }
}

class Node {

  public Task data;
  public Node next;
  public Node prev;

  public Node(Node prev, Task data, Node next) {
    this.data = data;
    this.next = next;
    this.prev = prev;
  }
}
