package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
  private final HashMap<Integer, Task> tasks = new HashMap<>();
  private final HashMap<Integer, Epic> epics = new HashMap<>();
  private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
  private static int counter = 0;

  public ArrayList<Task> getAllTasks() {
    return new ArrayList<>(tasks.values());
  }

  public ArrayList<Epic> getAllEpics() {
    return new ArrayList<>(epics.values());
  }

  public ArrayList<Subtask> getAllSubtasks() {
    return new ArrayList<>(subtasks.values());
  }

  public void clearTasks() {
    tasks.clear();
  }

  public void clearEpics() {
    subtasks.clear();
    epics.clear();
  }

  public void clearSubtasks() {
    for (Epic epic : epics.values()) {
      epic.clearSubtasks();
    }
    subtasks.clear();
  }

  public void deleteTask(int id) {
    tasks.remove(id);
  }

  public void deleteEpic(int id) {
    for (Subtask subtask : getSubtasksByEpicId(id)) {
      subtasks.remove(subtask.getId());
    }
    getSubtasksByEpicId(id).clear();
    epics.remove(id);
  }

  public void deleteSubtask(int id) {
      int epicId = getSubtaskById(id).getEpicId();
      epics.get(epicId).removeSubtask(subtasks.get(id));
      subtasks.remove(id);
  }

  public Task getTaskById(int id) {
    return tasks.get(id);
  }

  public Epic getEpicById(int id) {
    epics.get(id).getStatus();
    return epics.get(id);
  }

  public Subtask getSubtaskById(int id) {
    return subtasks.get(id);
  }

  public Task addTask(Task task) {
    task.setId(generateId());
    tasks.put(task.getId(), task);
    return task;
  }

  public Epic addEpic(Epic epic) {
    epic.setId(generateId());
    epics.put(epic.getId(), epic);
      for (Subtask subtask : epic.getSubtasks()) {
        subtask.setEpicId(epic.getId());
        addSubtask(subtask);
      }
    return epic;
  }

  public void addSubtask(Subtask subtask) {
    int epicId = subtask.getEpicId();
    if (!epics.containsKey(epicId) ) {
      return;
    }
    subtask.setId(generateId());
    getEpicById(epicId).addSubtask(subtask);
    subtasks.put(subtask.getId(), subtask);
  }

  public void updateTask(Task task) {
    if (task == null || !tasks.containsKey(task.getId())) {
      return;
    }
    tasks.put(task.getId(), task);
  }

  public void updateEpic(Epic epic) {
    if (epic == null || !epics.containsKey(epic.getId())) {
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

  public void updateSubtask(Subtask subtask) {
    if (subtask == null ||
        !subtasks.containsKey(subtask.getId()) ||
        getSubtaskById(subtask.getId()).getEpicId() != subtask.getEpicId()) {
      return;
    }
    subtasks.put(subtask.getId(), subtask);
    epics.get(subtask.getEpicId()).updateSubtask(subtask);
  }

  public ArrayList<Subtask> getSubtasksByEpicId(int id) {
    return new ArrayList<>(epics.get(id).getSubtasks());
  }

  private int generateId() {
    return ++counter;
  }
}