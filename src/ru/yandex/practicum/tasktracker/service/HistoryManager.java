package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

/**
 * The {@code HistoryManager} interface defines methods for managing the history of viewed tasks.
 * Implementations of this interface should allow adding tasks to the history, removing tasks from
 * the history by their IDs, and retrieving the entire history of viewed tasks.
 *
 * @see Task
 */
public interface HistoryManager {

  void add(Task task);

  void remove(int id);

  List<Task> getHistory();
}
