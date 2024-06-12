package ru.yandex.practicum.tasktracker.builder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Utility class for preparing test data
 */
public class TestDataBuilder {

  private TestDataBuilder() {
  }

  public static TaskManager buildTaskManager() {
    return new InMemoryTaskManager();
  }

  public static Task buildTask(String title, String description) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription(description);
    return task;
  }

  public static Task buildTask(int id, String title, String description, TaskStatus status) {
    Task task = buildTask(title, description);
    task.setId(id);
    task.setStatus(status);
    return task;
  }

  public static Task buildTask(int id, String title, String description, TaskStatus status,
      Duration duration, LocalDateTime startTime) {
    Task task = buildTask(id, title, description, status);
    task.setDuration(duration);
    task.setStartTime(startTime);
    return task;
  }

  public static Epic buildEpic(String title, String description) {
    Epic epic = new Epic();
    epic.setTitle(title);
    epic.setDescription(description);
    return epic;
  }

  public static Epic buildEpic(int id, String title, String description) {
    Epic epic = buildEpic(title, description);
    epic.setId(id);
    epic.setDescription(description);
    return epic;
  }

  public static Subtask buildSubtask(String title, String description, int epicId) {
    Subtask subtask = new Subtask();
    subtask.setTitle(title);
    subtask.setDescription(description);
    subtask.setEpicId(epicId);
    return subtask;
  }

  public static Subtask buildSubtask(String title, String description, int epicId,
      Duration duration, LocalDateTime startTime) {
    Subtask subtask = buildSubtask(title, description, epicId);
    subtask.setDuration(duration);
    subtask.setStartTime(startTime);
    return subtask;
  }

  public static Subtask buildSubtask(int id, String title, String description, int epicId) {
    Subtask subtask = buildSubtask(title, description, epicId);
    subtask.setId(id);
    return subtask;
  }

  public static Subtask buildSubtask(int id, String title, String description, int epicId,
      TaskStatus status, Duration duration, LocalDateTime startTime) {
    Subtask subtask = buildSubtask(id, title, description, epicId);
    subtask.setStatus(status);
    subtask.setDuration(duration);
    subtask.setStartTime(startTime);
    return subtask;
  }

  public static Task buildCopyTask(Task task) {
    Task result = new Task();
    result.setId(task.getId());
    result.setTitle(task.getTitle());
    result.setDescription(task.getDescription());
    result.setStatus(task.getStatus());
    return result;
  }

  public static Epic buildCopyEpic(Epic epic) {
    Epic result = new Epic();
    result.setId(epic.getId());
    result.setTitle(epic.getTitle());
    result.setDescription(epic.getDescription());
    result.setStatus(epic.getStatus());
    for (Subtask sb : epic.getSubtasks()) {
      result.addSubtask(sb);
    }
    return result;
  }

  public static Subtask buildCopySubtask(Subtask subtask) {
    Subtask result = new Subtask();
    result.setId(subtask.getId());
    result.setTitle(subtask.getTitle());
    result.setDescription(subtask.getDescription());
    result.setStatus(subtask.getStatus());
    result.setEpicId(subtask.getEpicId());
    return result;
  }

  /**
   * Provides a {@code List<Task>}, which includes:
   * <ol>
   *   <li>Epic - id, title, description</li>
   *   <li>Task - id, title, description, status, duration, startTime </li>
   *   <li>Subtask - id, title, description, epicId</li>
   *   <li>Epic - id, title, description</li>
   *   <li>Subtask - id, title,description epicId,status,duration, startTine </li>
   *   <li>Task - id, title, description, status</li>
   * </ol>
   * All tasks have non-conflicting startTime and duration.
   *
   * @return {@code List<Task>}
   */
  public static List<Task> buildTasks() {
    return new LinkedList<>(
        List.of(buildEpic(1, "epic1", "description"),
            buildTask(2, "task1", "d", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now()),
            buildSubtask(3, "subtask1", "notes", 1),
            buildEpic(4, "epic2", "description"),
            buildSubtask(5, "subtask2", "notes", 1, TaskStatus.IN_PROGRESS, Duration.ofMinutes(15),
                LocalDateTime.now().plusMinutes(20)),
            buildTask(6, "task2", "d", TaskStatus.NEW)));
  }

  /**
   * Method adds set of Task objects of different types, which includes:
   * <ol>
   *   <li> Two Tasks, one with startTime - indices 1, 5</li>
   *   <li> One Epic with two Subtasks - index 0</li>
   *   <li> Two Subtasks for Epic< second one with startTime - indices 2, 4</li>
   *   <li> One Epic without Subtasks - index 3</li>
   * </ol>
   * All tasks have non-conflicting startTime and duration.
   *
   * @return {@code List<Task>}
   */
  public static List<Integer> addTaskDataToTheTaskManager(TaskManager taskManager) {
    List<Task> taskData = buildTasks();
    List<Integer> ids = new ArrayList<>();
    for (Task t : taskData) {
      TaskType type = t.getType();
      switch (type) {
        case EPIC -> ids.add(taskManager.addEpic((Epic) t).getId());
        case SUBTASK -> {
          ((Subtask) t).setEpicId(ids.get(0));
          ids.add(taskManager.addSubtask((Subtask) t).getId());
        }
        case TASK -> ids.add(taskManager.addTask(t).getId());
      }
    }
    return ids;
  }

}
