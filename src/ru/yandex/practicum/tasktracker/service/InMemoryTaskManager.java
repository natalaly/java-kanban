package ru.yandex.practicum.tasktracker.service;

import java.util.HashSet;
import java.util.Set;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    int epicId = subtasks.get(id).getEpicId();
    epics.get(epicId).removeSubtask(subtasks.get(id));
    subtasks.remove(id);
  }

  @Override
  public Task getTaskById(int id) {
    Task task = tasks.get(id);
    historyManager.add(task);
    return task;
  }

  @Override
  public Epic getEpicById(int id) {
    Epic epic = epics.get(id);
    historyManager.add(epic);
    return epic;
  }

  @Override
  public Subtask getSubtaskById(int id) {
    Subtask subtask = subtasks.get(id);
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
    Set<Subtask> subtasksFromEpic = new HashSet<>(epic.getSubtasks());
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