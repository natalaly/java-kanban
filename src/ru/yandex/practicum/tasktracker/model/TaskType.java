package ru.yandex.practicum.tasktracker.model;

public enum TaskType {
  TASK,
  EPIC,
  SUBTASK;

  public static TaskType getType(Task task) {
    try {
      return switch (task.getClass().getSimpleName()) {
        case "Task" -> TASK;
        case "Epic" -> EPIC;
        case "Subtask" -> SUBTASK;
        default -> null;
      };
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    return null;
  }
}
