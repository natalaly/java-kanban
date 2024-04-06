package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

  private  final List<Task> history = new ArrayList<>();
  private static final int HISTORY_CAPACITY = 10;

  @Override
  public void add(Task task) {
    if (history.size() >= HISTORY_CAPACITY) {
      history.remove(0);
    }
    history.add(task);
  }

  @Override
  public List<Task> getHistory() {
    return this.history;
  }
}
