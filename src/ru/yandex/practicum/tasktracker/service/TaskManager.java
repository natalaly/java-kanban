package ru.yandex.practicum.tasktracker.service;

import java.util.Set;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import java.util.List;

public interface TaskManager {

  List<Task> getAllTasks();

  List<Epic> getAllEpics();

  List<Subtask> getAllSubtasks();

  void clearTasks();

  void clearEpics();

  void clearSubtasks();

  void deleteTask(int id);

  void deleteEpic(int id);

  void deleteSubtask(int id);

  Task getTaskById(int id);

  Epic getEpicById(int id);

  Subtask getSubtaskById(int id);

  Task addTask(Task task);

  Epic addEpic(Epic epic);

  Subtask addSubtask(Subtask subtask);

  void updateTask(Task task);

  void updateEpic(Epic epic);

  void updateSubtask(Subtask subtask);

  Set<Subtask> getSubtasksByEpicId(int id);

  List<Task> getHistory();

}
