package main.java.ru.yandex.practicum.tasktracker.service;
/**
 * Утилитарный класс Managers.
 * На нём будет лежать вся ответственность за создание менеджера задач.
 * Сам подбирает нужную реализацию TaskManager и возвращает объект правильного типа.
 * У Managers будет метод getDefault.
 * При этом вызывающему неизвестен конкретный класс — только то, что объект, который возвращает getDefault, реализует интерфейс TaskManager.
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
