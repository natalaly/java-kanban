package ru.yandex.practicum.tasktracker.service;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.exception.TaskPrioritizationException;
import ru.yandex.practicum.tasktracker.exception.TaskValidationException;
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
  protected final Set<Task> prioritizedTasks = new TreeSet<>(
      Comparator.comparing(Task::getStartTime));

  @Override
  public List<Task> getTasks() {
    return new ArrayList<>(tasks.values());
  }

  @Override
  public List<Epic> getEpics() {
    return new ArrayList<>(epics.values());
  }

  @Override
  public List<Subtask> getSubtasks() {
    return new ArrayList<>(subtasks.values());
  }

  @Override
  public List<Task> getPrioritizedTasks() {
    return new ArrayList<>(prioritizedTasks);
  }

  /**
   * Retrieves the history of tasks from the history manager. This method fetches the history list
   * from the history manager, resolves each task to its corresponding object from the appropriate
   * collection.
   *
   * @return a list of tasks in the history
   */
  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory().stream()
        .map(Task::getId)
        .map(id -> tasks.containsKey(id) ? tasks.get(id)
            : (epics.containsKey(id) ? epics.get(id)
                : (subtasks.getOrDefault(id, null))))
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public void clearTasks() {
    tasks.values().forEach(t -> {
      historyManager.remove(t.getId());
      removePrioritized(t);
    });
    tasks.clear();
  }

  @Override
  public void clearEpics() {
    subtasks.values().forEach(st -> {
      historyManager.remove(st.getId());
      removePrioritized(st);
    });
    epics.keySet().forEach(historyManager::remove);
    subtasks.clear();
    epics.clear();
  }

  @Override
  public void clearSubtasks() {
    epics.values().forEach(Epic::clearSubtasks);
    subtasks.values().forEach(st -> {
      historyManager.remove(st.getId());
      removePrioritized(st);
    });
    subtasks.clear();
  }

  @Override
  public void deleteTask(final int id) {
    Task removedTask = tasks.remove(id);
    if (removedTask == null) {
      throw new TaskNotFoundException("Task with Id " + id + " was not found.");
    }
    historyManager.remove(id);
    removePrioritized(removedTask);
  }

  @Override
  public void deleteEpic(final int id) {
    final Epic epic = epics.remove(id);
    if (epic == null) {
      throw new TaskNotFoundException("Task with Id " + id + " was not found.");
    }
    epic.getSubtasks().forEach(s -> {
      subtasks.remove(s.getId());
      historyManager.remove(s.getId());
      removePrioritized(s);
    });
    historyManager.remove(id);
  }

  @Override
  public void deleteSubtask(final int id) {
    final Subtask subtask = subtasks.remove(id);
    if (subtask == null) {
      throw new TaskNotFoundException("Task with Id " + id + " was not found.");
    }
    final int epicId = subtask.getEpicId();
    epics.get(epicId).removeSubtask(subtask);
    historyManager.remove(id);
    removePrioritized(subtask);
  }

  @Override
  public Task getTaskById(final int id) {
    final Optional<Task> t = Optional.ofNullable(tasks.get(id));
    t.ifPresent(historyManager::add);
    return t.orElseThrow(() -> new TaskNotFoundException("Task with Id " + id + " was not found."))
        .copy();
  }

  @Override
  public Epic getEpicById(final int id) {
    final Optional<Epic> epic = Optional.ofNullable(epics.get(id));
    epic.ifPresent(historyManager::add);
    return epic.orElseThrow(
        () -> new TaskNotFoundException("Epic with Id " + id + " was not found.")).copy();
  }

  @Override
  public Subtask getSubtaskById(final int id) {
    final Optional<Subtask> subtask = Optional.ofNullable(subtasks.get(id));
    subtask.ifPresent(historyManager::add);
    return subtask.orElseThrow(
        () -> new TaskNotFoundException("Subtask with Id " + id + " was not found.")).copy();
  }

  @Override
  public Set<Subtask> getSubtasksByEpicId(final int epicId) {
    final Optional<Epic> optionalEpic = Optional.ofNullable(epics.get(epicId));
    final Optional<Set<Subtask>> optionalSubtasks = optionalEpic.map(
        epic -> new HashSet<>(epic.getSubtasks()));
    return optionalSubtasks.orElseThrow(
        () -> new TaskNotFoundException("Epic with Id " + epicId + " was not found."));
  }

  @Override
  public Task addTask(final Task task) {
    task.setId(generateId());
    addPrioritized(task);
    tasks.put(task.getId(), task.copy());
    return task;
  }

  @Override
  public Epic addEpic(final Epic epic) {
    epic.setId(generateId());
    epics.put(epic.getId(), epic.copy());
    final Set<Subtask> subtasksFromNewEpic = new HashSet<>(epic.getSubtasks());
    epic.clearSubtasks();
    subtasksFromNewEpic.forEach(s -> {
      s.setEpicId(epic.getId());
      addSubtask(s);
    });
    return epic;
  }

  @Override
  public Subtask addSubtask(final Subtask subtask) {
    final int epicId = subtask.getEpicId();
    if (!epics.containsKey(epicId)) {
      throw new TaskValidationException("Invalid epic Id " + epicId);
    }
    subtask.setId(generateId());
    Subtask subtaskToAdd = subtask.copy();
    addPrioritized(subtask);
    epics.get(epicId).addSubtask(subtaskToAdd);
    subtasks.put(subtask.getId(), subtaskToAdd);
    return subtask;
  }

  @Override
  public void updateTask(final Task taskToUpdate) {
    final Task taskInMemory = tasks.get(taskToUpdate.getId());
    if (taskInMemory == null) {
      throw new TaskNotFoundException(
          "The task " + taskToUpdate + "does not exist in the TaskManager");
    }
    removePrioritized(taskInMemory);
    addPrioritized(taskToUpdate.copy());
    tasks.put(taskToUpdate.getId(), taskToUpdate.copy());
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
    final Subtask subtaskInMemory = subtasks.get(subtask.getId());
    if (subtaskInMemory == null ||
        subtaskInMemory.getEpicId() != subtask.getEpicId()) {
      throw new TaskNotFoundException(
          "The subtask " + subtask + "does not exist in the TaskManager");
    }
    removePrioritized(subtaskInMemory);
    final Subtask subtaskToUpdate = subtask.copy();
    addPrioritized(subtaskToUpdate);
    subtasks.put(subtask.getId(), subtaskToUpdate);
    epics.get(subtask.getEpicId()).updateSubtask(subtaskToUpdate);
  }

  private int generateId() {
    return ++counter;
  }

  /**
   * Adds a task to the {@link #prioritizedTasks} if it can be prioritized.
   * <p>This method first checks if the task can be prioritized using the
   * {@link #canBePrioritized(Task)} method.
   * <p>If the task cannot be prioritized (due to a null start time or a time conflict with
   * existing tasks), it will not be added to the set of prioritized tasks.
   *
   * @param taskToAdd
   * @throws TaskValidationException if the task has a time conflict with an existing task.
   */
  private void addPrioritized(final Task taskToAdd) {
    if (!canBePrioritized(taskToAdd)) {
      return;
    }
    prioritizedTasks.add(taskToAdd);
  }

  /**
   * Method checks for a valid start time and ensures no time conflicts before allowing a task to be
   * prioritized.
   *
   * @param taskToCheck
   * @return {@Code true} if {@code taskToCheck} meets above requirements
   * @throws TaskPrioritizationException if the task has a time conflict with an existing task.
   */
  private boolean canBePrioritized(final Task taskToCheck) {
    Objects.requireNonNull(taskToCheck);
    if (taskToCheck.getStartTime() == null) {
      return false;
    }
    final Task floor = ((TreeSet<Task>) prioritizedTasks).floor(taskToCheck);
    final Task ceiling = ((TreeSet<Task>) prioritizedTasks).ceiling(taskToCheck);

    if (hasTimeConflict(taskToCheck, floor)) {
      throw new TaskPrioritizationException(
          "Task has time conflict with existing task with ID " + floor.getId());
    }
    if (hasTimeConflict(taskToCheck, ceiling)) {
      throw new TaskPrioritizationException(
          "Task has time conflict with existing task with ID " + ceiling.getId());
    }
    return true;
  }

  private boolean hasTimeConflict(final Task task1, final Task task2) {
    return task2 != null && task1.getStartTime().isBefore(task2.getEndTime()) &&
        task1.getEndTime().isAfter(task2.getStartTime());
  }

  private void removePrioritized(Task task) {
    if (task != null && task.getStartTime() != null) {
      prioritizedTasks.remove(task);
    }

  }

}


