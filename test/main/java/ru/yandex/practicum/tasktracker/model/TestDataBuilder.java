package main.java.ru.yandex.practicum.tasktracker.model;

import main.java.ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Utility class
 */
public class TestDataBuilder {

  public static TaskManager buildTaskManager() {
    return new InMemoryTaskManager();
  }

  public static Task buildTaskWithId(int id, String title, String description, TaskStatus status) {
    Task task = new Task();
    task.setId(id);
    task.setTitle(title);
    task.setDescription(description);
    task.setStatus(status);
    return task;
  }

  static Epic buildEpic(String title, String description) {
    Epic epic = new Epic();
    epic.setTitle(title);
    epic.setDescription(description);
    return epic;
  }

  static Subtask buildSubtask(String title, String description, int epicId) {
    Subtask subtask = new Subtask();
    subtask.setTitle(title);
    subtask.setDescription(description);
    subtask.setEpicId(epicId);
    return subtask;
  }


}
