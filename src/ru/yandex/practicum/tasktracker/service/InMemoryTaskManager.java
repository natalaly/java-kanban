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

  @Override
  public void clearTasks() {
    tasks.values().forEach(t -> historyManager.remove(t.getId()));
    tasks.clear();
  }

  @Override
  public void clearEpics() {
    subtasks.values().forEach(st -> historyManager.remove(st.getId()));
    epics.values().forEach(e -> historyManager.remove(e.getId()));
    subtasks.clear();
    epics.clear();
  }

  @Override
  public void clearSubtasks() {
    epics.values().forEach(Epic::clearSubtasks);
    subtasks.values().forEach(st -> historyManager.remove(st.getId()));
    subtasks.clear();
  }

  @Override
  public void deleteTask(final int id) {
    final Task task = tasks.remove(id);
    if (task != null) {
      historyManager.remove(id);
    }
  }

  @Override
  public void deleteEpic(final int id) {
    final Epic epic = epics.remove(id);
    if (epic != null) {
      for (Subtask subtask : epic.getSubtasks()) {
        subtasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
      }
      historyManager.remove(id);
    }
  }

  @Override
  public void deleteSubtask(final int id) {
    final Subtask subtask = subtasks.remove(id);
    if (subtask != null) {
      final int epicId = subtask.getEpicId();
      epics.get(epicId).removeSubtask(subtask);
      historyManager.remove(id);
    }
  }

  @Override
  public Task getTaskById(final int id) {
    final Task task = tasks.get(id);
    historyManager.add(tasks.get(id));
    return task;
  }

  @Override
  public Epic getEpicById(final int id) {
    final Epic epic = epics.get(id);
    historyManager.add(epic);
    return epic;
  }

  @Override
  public Subtask getSubtaskById(final int id) {
    final Subtask subtask = subtasks.get(id);
    historyManager.add(subtask);
    return subtask;
  }

  @Override
  public Task addTask(final Task task) {
    task.setId(generateId());
    tasks.put(task.getId(), task.clone());
    return task;
  }

  @Override
  public Epic addEpic(final Epic epic) {
    epic.setId(generateId());
    epics.put(epic.getId(), epic.clone());
    final Set<Subtask> subtasksFromNewEpic = new HashSet<>(epic.getSubtasks());
    epic.clearSubtasks();
    for (Subtask subtask : subtasksFromNewEpic) {
      subtask.setEpicId(epic.getId());
      addSubtask(subtask);
    }
    return epic;
  }

  @Override
  public Subtask addSubtask(final Subtask subtask) {
    int epicId = subtask.getEpicId();
    if (!epics.containsKey(epicId)) {
      return null;
    }
    subtask.setId(generateId());
    Subtask subtaskToAdd = subtask.clone();
    epics.get(epicId).addSubtask(subtaskToAdd);
    subtasks.put(subtask.getId(), subtaskToAdd);
    return subtask;
  }

  @Override
  public void updateTask(final Task task) {
    if (!tasks.containsKey(task.getId())) {
      return;
    }
    tasks.put(task.getId(), task.clone());
  }

// updateEpic() allows update title, description only.
// It is not allowed to change the status manually, as it is calculated and depends on Subtasks statuses
//  to change any states of Subtasks - use updateSubtask()
  @Override
  public void updateEpic(final Epic epic) {
    if (!epics.containsKey(epic.getId())) { // if there is no such  epic in TM memory
      return;
    }
//    final Epic updatedEpic = epic.clone();
//    epics.put(epic.getId(), updatedEpic); // put cloned entered epic instead of existed in TM before updating
//    for (Subtask subtask : updatedEpic.getSubtasks()) { // if updated epic has subtasks in it:
//      if (!subtasks.containsKey(subtask.getId())) { // when subtask id new for TM but existed in the updated
//        subtask.setId(generateId());
//        subtasks.put(subtask.getId(), subtask);
//      } else {
//        subtasks.put(subtask.getId(), subtask);
//        epics.get(subtask.getEpicId()).updateSubtask(subtask);
//      }
//    }
    final Epic updatedEpic = epics.get(epic.getId());
    updatedEpic.setTitle(epic.getTitle());
    updatedEpic.setDescription(epic.getDescription());
  }

  @Override
  public void updateSubtask(final Subtask subtask) {
    if (!subtasks.containsKey(subtask.getId()) ||
        subtasks.get(subtask.getId()).getEpicId() != subtask.getEpicId()) {
      return;
    }
    final Subtask subtaskToUpdate = subtask.clone();
    subtasks.put(subtask.getId(), subtaskToUpdate);
    epics.get(subtask.getEpicId()).updateSubtask(subtaskToUpdate);
  }

  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory();
  }

  @Override
  public Set<Subtask> getSubtasksByEpicId(final int id) {
    if (!epics.containsKey(id)) {
      return null;
    }
    return new HashSet<>(epics.get(id).getSubtasks());
  }

  private int generateId() {
    return ++counter;
  }
}