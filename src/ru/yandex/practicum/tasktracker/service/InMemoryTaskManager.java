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

/**
 * The {@code InMemoryTaskManager} class implements the {@link TaskManager} interface and provides
 * an in-memory storage solution for managing tasks, epics, and subtasks.
 * <p>
 * This class maintains three maps to store tasks, epics, and subtasks respectively. It also
 * utilizes a {@link HistoryManager} to keep track of the history of viewing operations performed on
 * tasks.
 * <p>
 * Operations such as adding, updating, and deleting tasks, epics, and subtasks are supported.
 * Additionally, methods are provided to retrieve all tasks, epics, and subtasks, clear all tasks,
 * epics, and subtasks, retrieve tasks, epics, and subtasks by their IDs, retrieve subtasks
 * associated with a specific epic ID, and retrieve the history of viewing operations performed on
 * tasks.
 *
 * @see TaskManager
 * @see Task
 * @see Epic
 * @see Subtask
 * @see HistoryManager
 */
public class InMemoryTaskManager implements TaskManager {

  protected static int counter = 0;
  protected final Map<Integer, Task> tasks = new HashMap<>();
  protected final Map<Integer, Epic> epics = new HashMap<>();
  protected final Map<Integer, Subtask> subtasks = new HashMap<>();
  protected final HistoryManager historyManager = Managers.getDefaultHistory();


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
    return task == null ? null : new Task(task);
  }

  @Override
  public Epic getEpicById(final int id) {
    final Epic epic = epics.get(id);
    historyManager.add(epic);
    return epic == null ? null : new Epic(epic);
  }

  @Override
  public Subtask getSubtaskById(final int id) {
    final Subtask subtask = subtasks.get(id);
    historyManager.add(subtask);
    return subtask == null ? null : new Subtask(subtask);
  }

  @Override
  public Task addTask(final Task task) {
    task.setId(generateId());
    tasks.put(task.getId(), new Task(task));
    return task;
  }

  @Override
  public Epic addEpic(final Epic epic) {
    epic.setId(generateId());
    epics.put(epic.getId(), new Epic(epic));
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
    Subtask subtaskToAdd = new Subtask(subtask);
    epics.get(epicId).addSubtask(subtaskToAdd);
    subtasks.put(subtask.getId(), subtaskToAdd);
    return subtask;
  }

  @Override
  public void updateTask(final Task task) {
    if (!tasks.containsKey(task.getId())) {
      return;
    }
    tasks.put(task.getId(), new Task(task));
  }

  /**
   * Method {@code updateEpic(Epic)} allows update title, description only.
   * <p>
   * It is not allowed to change the status manually, as it is calculated and depends on its
   * Subtasks statuses.
   * <p>
   * To make any changes in its Subtasks - use {@link #updateSubtask(Subtask)}.
   */
  @Override
  public void updateEpic(final Epic epic) {
    if (!epics.containsKey(epic.getId())) {
      return;
    }
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
    final Subtask subtaskToUpdate = new Subtask(subtask);
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