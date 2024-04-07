package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

  private final Map<Integer, Task> tasks = new HashMap<>();
  private final Map<Integer, Epic> epics = new HashMap<>();
  private final Map<Integer, Subtask> subtasks = new HashMap<>();
  private final HistoryManager taskViewHistory = Managers.getDefaultHistory();
  private  static int counter = 0;

  @Override
  public int generateId() {
    return ++counter;
  }

  @Override
  public List<Task> getAllTasks() {
    return new ArrayList<>(tasks.values());
  }

  @Override
  public List<Epic> getAllEpics() {
    return new ArrayList<>(epics.values());
  }

  @Override
  public List<Subtask> getAllSubtasks() {
    return new ArrayList<>(subtasks.values());
  }

  @Override
  public void clearTasks() {
    tasks.clear();
  }

  @Override
  public void clearEpics() {
    subtasks.clear();
    epics.clear();
  }

  @Override
  public void clearSubtasks() {
    for (Epic epic : epics.values()) {
      epic.clearSubtasks();
    }
    subtasks.clear();
  }

  @Override
  public void deleteTask(int id) {
    tasks.remove(id);
  }

  @Override
  public void deleteEpic(int id) {
    for (Subtask subtask : getSubtasksByEpicId(id)) {
      subtasks.remove(subtask.getId());
    }
    epics.remove(id);
  }

  @Override
  public void deleteSubtask(int id) {
      int epicId = getSubtaskById(id).getEpicId();
      epics.get(epicId).removeSubtask(subtasks.get(id));
      subtasks.remove(id);
  }

  @Override
  public Task getTaskById(int id) {
    Task task = tasks.get(id);
    if (task != null) {
      taskViewHistory.add(task);
    }
    return task;
  }

  @Override
  public Epic getEpicById(int id) {
    Epic epic = epics.get(id);
    if (epic != null) {
      taskViewHistory.add(epic);
    }
    return epic;
  }

  @Override
  public Subtask getSubtaskById(int id) {
    Subtask subtask = subtasks.get(id);
    if (subtask != null) {
      taskViewHistory.add(subtask);
    }
    return subtask;
  }

  @Override
  public Task addTask(Task task) {
    task.setId(generateId());
    tasks.put(task.getId(), task);
    return task;
  }

  @Override
  public Epic addEpic(Epic epic) {
    epic.setId(generateId());
    epics.put(epic.getId(), epic);
      for (Subtask subtask : epic.getSubtasks()) {
        subtask.setEpicId(epic.getId());
        addSubtask(subtask);
      }
    return epic;
  }

  @Override
  public Subtask addSubtask(Subtask subtask) {
    int epicId = subtask.getEpicId();
    if (!epics.containsKey(epicId) ) {
      return null;
    }
    subtask.setId(generateId());
    epics.get(epicId).addSubtask(subtask);
    subtasks.put(subtask.getId(), subtask);
    return subtask;
  }

  @Override
  public void updateTask(Task task) {
    if (!tasks.containsKey(task.getId())) {
      return;
    }
    tasks.put(task.getId(), task);
  }

  @Override
  public void updateEpic(Epic epic) {
    if (!epics.containsKey(epic.getId())) {
      return;
    }
    epics.put(epic.getId(), epic);
    for (Subtask subtask : epic.getSubtasks()) {
      if (subtask.getId() == 0) {
        addSubtask(subtask);
      } else {
        updateSubtask(subtask);
      }
    }
  }

  @Override
  public void updateSubtask(Subtask subtask) {
    if (!subtasks.containsKey(subtask.getId()) ||
        subtasks.get(subtask.getId()).getEpicId() != subtask.getEpicId()) {
      return;
    }
    subtasks.put(subtask.getId(), subtask);
    epics.get(subtask.getEpicId()).updateSubtask(subtask);
  }

  @Override
  public List<Task> getHistory() {
    return taskViewHistory.getHistory();
  }

  @Override
  public List<Subtask> getSubtasksByEpicId(int id) {
    return new ArrayList<>(epics.get(id).getSubtasks());
  }

}