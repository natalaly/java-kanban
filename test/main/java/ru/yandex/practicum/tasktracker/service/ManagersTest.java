package main.java.ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

  @Test
  void byDefaultShouldReturnInMemoryTaskManagerObject() {
    TaskManager taskManager = Managers.getDefault();
    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager, "The object of TaskManager was not created."),
        () -> Assertions.assertInstanceOf(InMemoryTaskManager.class, taskManager, "Created incorrect type of TaskManager")
    );
  }

  @Test
  void getDefaultHistoryShouldReturnInMemoryHistoryManagerObject() {
    HistoryManager historyManager = Managers.getDefaultHistory();
    Assertions.assertAll(
        () -> Assertions.assertNotNull(historyManager, "The object of HistoryManager was not created."),
        () -> Assertions.assertInstanceOf(InMemoryHistoryManager.class,historyManager ,"Created incorrect type of HistoryManager")
    );
  }
}