package main.java.ru.yandex.practicum.tasktracker.service;
/**
 * Utility class
 */
public class Managers {

  private Managers() {

  } //Don't let anyone instantiate this class.

  public static TaskManager getDefault() {
    return new InMemoryTaskManager();
  }

  public static HistoryManager getDefaultHistory() {
    return new InMemoryHistoryManager();
  }

}
