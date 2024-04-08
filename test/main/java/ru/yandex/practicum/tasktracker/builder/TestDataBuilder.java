package main.java.ru.yandex.practicum.tasktracker.builder;

import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;
import main.java.ru.yandex.practicum.tasktracker.model.TaskStatus;
import main.java.ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Utility class for preparing test data
 */
public class TestDataBuilder {
  private TestDataBuilder(){}

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
    Task task = buildTask( title,  description);
    task.setId(id);
    task.setStatus(status);
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

  public static Subtask buildSubtask(int id, String title, String description, int epicId) {
    Subtask subtask = buildSubtask(title, description, epicId);
    subtask.setId(id);
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

}