package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import main.java.ru.yandex.practicum.tasktracker.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class InMemoryHistoryManagerTest {
  private static final int HISTORY_CAPACITY = 10;
  private HistoryManager history;


  @BeforeEach
  void setUp() {
    history = new InMemoryHistoryManager();
  }

  @AfterEach
  void tearDown() {
    history = null;
  }

  @ParameterizedTest
  @MethodSource("provideTasks")
  <T extends Task> void addShouldAcceptTaskEpicSubtaskTypesForSaving(T task) {
    List<Task> expectedHistory = new ArrayList<>(Arrays.asList(task));
    history.add(task);

    List<Task> actualHistory = history.getHistory();

    Assertions.assertIterableEquals(expectedHistory, actualHistory, "Returned list should contain the same task.");
  }

  @Test
  void addShouldRemoveOldestTaskToAddNewTaskToTheEndWhenCapacityIsReached() {
    for (int i = 0; i < HISTORY_CAPACITY; i++) {
      Task task = TestDataBuilder.buildTask(i + 1, "task", "d", TaskStatus.NEW);
      history.add(task);
    }
    Epic epic = TestDataBuilder.buildEpic(13, "epic", "description");

    history.add(epic);

    Assertions.assertAll(
        () -> Assertions.assertEquals(HISTORY_CAPACITY, history.getHistory().size(),
            "History List should not exceed " + HISTORY_CAPACITY + " items."),
        () -> Assertions.assertEquals(2, history.getHistory().get(0).getId(),
            "First task doesn't have id = 2 (second element before)."),
        () -> Assertions.assertEquals(epic, history.getHistory().get(HISTORY_CAPACITY - 1),
            "Last element is not epic.")
    );
  }

  private static List<Task> provideTasks() {
    Task task = TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW);
    Epic epic = TestDataBuilder.buildEpic(2, "epic", "description");
    Subtask subtask = TestDataBuilder.buildSubtask(3, "subtask", "notes", 2);
    return new ArrayList<>(Arrays.asList(task, epic, subtask));
  }
}