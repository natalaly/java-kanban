package ru.yandex.practicum.tasktracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.practicum.tasktracker.model.Task;

/**
 * The {@code InMemoryHistoryManager} class implements the {@link HistoryManager} interface and
 * provides an in-memory storage solution for managing the history of viewed tasks.
 * <p>
 * This class maintains a doubly linked list structure to store the history of viewed tasks. It
 * allows adding tasks to the history, removing tasks from the history by their IDs, and retrieving
 * the entire history of viewed tasks.
 * <p>
 * The implementation ensures that only the latest view of a task is kept in the history. If a task
 * is viewed multiple times, only its latest view is retained in the history.
 * <p>
 * Additionally, the {@link #remove(int)} method has been added to allow removing tasks from the
 * history when they are deleted from the system.
 *
 * @see HistoryManager
 * @see Task
 */
public class InMemoryHistoryManager implements HistoryManager {

  private final Map<Integer, Node> nodes = new HashMap<>();
  private Node head;
  private Node tail;

  @Override
  public void add(final Task task) {
    if (task == null) {
      return;
    }
    remove(task.getId());
    linkLast(task);
  }

  @Override
  public void remove(final int id) {
    final Node nodeToRemove = nodes.remove(id);
    removeNode(nodeToRemove);
  }

  @Override
  public List<Task> getHistory() {
    return getTasks();
  }

  private void linkLast(final Task taskToAdd) {
    final Node newNode = new Node(taskToAdd);
    if (tail == null) {
      head = newNode;
    } else {
      tail.next = newNode;
      newNode.prev = tail;
    }
    tail = newNode;
    nodes.put(taskToAdd.getId(), newNode);
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

  private void removeNode(final Node nodeToRemove) {
    if (nodeToRemove == null) {
      return;
    }

    final Node nodeAfter = nodeToRemove.next;
    final Node nodeBefore = nodeToRemove.prev;

    if (nodeBefore == null) {
      head = nodeAfter;
    } else {
      nodeBefore.next = nodeAfter;
      nodeToRemove.prev = null;
    }

    if (nodeAfter == null) {
      tail = nodeBefore;
    } else {
      nodeAfter.prev = nodeBefore;
      nodeToRemove.next = null;
    }
  }

  private static class Node {

    public Task data;
    public Node next;
    public Node prev;

    public Node(final Task task) {
      this.data = task;
      this.next = null;
      this.prev = null;
    }
  }
}


