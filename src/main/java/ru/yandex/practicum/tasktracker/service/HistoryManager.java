package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {

  void add(Task task);

  List<Task> getHistory();
}
