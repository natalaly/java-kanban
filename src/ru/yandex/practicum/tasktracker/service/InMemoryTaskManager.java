package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

  private static int counter = 0;
  private final Map<Integer, Task> tasks = new HashMap<>();
  private final Map<Integer, Epic> epics = new HashMap<>();
  private final Map<Integer, Subtask> subtasks = new HashMap<>();
  private final HistoryManager historyManager = Managers.getDefaultHistory();


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

  //  TODO add test - removed from the history
  @Override
  public void clearTasks() {
    for (Task task : tasks.values()) {
      historyManager.remove(task.getId());
    }
    tasks.clear();
  }

  //  TODO add test - removed from the history
  @Override
  public void clearEpics() {
    for (Subtask subtask : subtasks.values()) {
      historyManager.remove(subtask.getId());
    }
    for (Epic epic : epics.values()) {
      historyManager.remove(epic.getId());
    }
    subtasks.clear();
    epics.clear();
  }

  //  TODO add test - removed from the history
  @Override
  public void clearSubtasks() {
    for (Epic epic : epics.values()) {
      epic.clearSubtasks();
    }
    for (Subtask subtask : subtasks.values()) {
      historyManager.remove(subtask.getId());
    }
    subtasks.clear();
  }

  //  TODO add test - removed from the history
  @Override
  public void deleteTask(int id) {
    final Task task = tasks.remove(id);
    if (task != null) {
      historyManager.remove(id);
    }
  }

  //  TODO add test - removed from the history
  @Override
  public void deleteEpic(int id) {
    final Epic epic = epics.remove(id);
    if (epic != null) {
      for (Subtask subtask : epic.getSubtasks()) {
        subtasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
      }
      historyManager.remove(id);
    }
  }

  //  TODO add test - removed from the history
  @Override
  public void deleteSubtask(int id) {
    final Subtask subtask = subtasks.remove(id);
    if (subtask != null) {
      int epicId = subtask.getEpicId();
      epics.get(epicId).removeSubtask(subtask);
      historyManager.remove(id);
    }
  }

  @Override
  public Task getTaskById(int id) {
    final Task task = tasks.get(id);
    historyManager.add(tasks.get(id));
    return task;
  }

  @Override
  public Epic getEpicById(int id) {
    final Epic epic = epics.get(id);
    historyManager.add(epic);
    return epic;
  }

  @Override
  public Subtask getSubtaskById(int id) {
    final Subtask subtask = subtasks.get(id);
    historyManager.add(subtask);
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
    final Set<Subtask> subtasksFromEpic = new HashSet<>(epic.getSubtasks());
    epic.clearSubtasks();
    for (Subtask subtask : subtasksFromEpic) {
      subtask.setEpicId(epic.getId());
      addSubtask(subtask);
    }
    return epic;
  }

  @Override
  public Subtask addSubtask(Subtask subtask) {
    int epicId = subtask.getEpicId();
    if (!epics.containsKey(epicId)) {
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
      if (!subtasks.containsKey(subtask.getId())) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
      } else {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtask(subtask);
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
    return historyManager.getHistory();
  }

  @Override
  public Set<Subtask> getSubtasksByEpicId(int id) {
    if (!epics.containsKey(id)) {
      return null;
    }
    return new HashSet<>(epics.get(id).getSubtasks());
  }

  private int generateId() {
    return ++counter;
  }

}