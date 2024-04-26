package ru.yandex.practicum.tasktracker.service;

import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

class InMemoryHistoryManagerTest {

  private HistoryManager historyManager;


  @BeforeEach
  void setUp() {
    historyManager = new InMemoryHistoryManager();
  }

  @AfterEach
  void tearDown() {
    historyManager = null;
  }

  @ParameterizedTest
  @MethodSource("provideDifferentTypesTasks")
  <T extends Task> void addShouldAcceptTaskEpicSubtaskTypesForSaving(T task) {
    final List<Task> expectedHistory = new ArrayList<>(Arrays.asList(task));
    historyManager.add(task);

    final List<Task> actualHistory = historyManager.getHistory();

    Assertions.assertIterableEquals(expectedHistory, actualHistory,
        "Returned list should contain the same task.");
  }

  @Test
  void addShouldNotSaveNullToTheHistory() {
    Task nullTask = null;
    final int expectedHistorySize = historyManager.getHistory().size();

    historyManager.add(nullTask);
    final int actualHistorySize = historyManager.getHistory().size();

    Assertions.assertEquals(expectedHistorySize, actualHistorySize,
        "Should not save null to the history");
  }

  @Test
  void shouldKeepViewingTaskOrderFromOldToNew() {
    List<Task> expectedTasks = buildTasks();
    for (Task task : expectedTasks) {
      historyManager.add(task);
    }

    final List<Task> actual = historyManager.getHistory();

    Assertions.assertIterableEquals(expectedTasks, actual, "Order of elements should be same.");
  }

  @Test
  void addShouldReplaceExistedTaskWithANewVersionAndMoveItToTheEndOfTheList() {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size();
    int expectedPosition = historyManager.getHistory().size() - 1;
    Task taskToAdd = TestDataBuilder.buildCopyTask(historyManager.getHistory().get(1));
    taskToAdd.setStatus(TaskStatus.DONE);

    historyManager.add(taskToAdd);
    int actualHistorySize = historyManager.getHistory().size();
    final Task actualTask = historyManager.getHistory().get(expectedPosition);

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
            "Size of history should not expand."),
        () -> Assertions.assertEquals(taskToAdd, actualTask, "Tasks should be same"),
        () -> Assertions.assertEquals(taskToAdd.getTitle(), actualTask.getTitle(),
            "Titles should be same."),
        () -> Assertions.assertEquals(taskToAdd.getDescription(), actualTask.getDescription(),
            "Descriptions should be same."),
        () -> Assertions.assertEquals(taskToAdd.getStatus(), actualTask.getStatus(),
            "Statuses should be same.")
    );
  }

  @Test
  void removeShouldDeleteTaskFromBeginningOfTheHistoryList() {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size() - 1;
    int positionToDelete = 0;
    final Task taskToDelete = historyManager.getHistory().get(positionToDelete);
    final Task expectedFirstElement = historyManager.getHistory().get(positionToDelete + 1);

    historyManager.remove(taskToDelete.getId());
    final Task actualFirstElement = historyManager.getHistory().get(positionToDelete);
    int actualHistorySize = historyManager.getHistory().size();
    boolean isDeleted = !historyManager.getHistory().contains(taskToDelete);

    Assertions.assertAll(
        () -> Assertions.assertTrue(isDeleted, "Task was not removed."),
        () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
            "History size should reduce by 1."),
        () -> Assertions.assertEquals(expectedFirstElement, actualFirstElement,
            "Incorrect first element in the history.")
    );
  }

  @Test
  void removeShouldDeleteTaskFromTheEndOfTheHistoryList() {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size() - 1;
    int positionToDelete = historyManager.getHistory().size() - 1;
    final Task taskToDelete = historyManager.getHistory().get(positionToDelete);
    final Task expectedLastElement = historyManager.getHistory().get(positionToDelete - 1);

    historyManager.remove(taskToDelete.getId());
    final Task actualLastElement = historyManager.getHistory().get(positionToDelete - 1);
    int actualHistorySize = historyManager.getHistory().size();
    boolean isDeleted = !historyManager.getHistory().contains(taskToDelete);

    Assertions.assertAll(
        () -> Assertions.assertTrue(isDeleted, "Task was not removed."),
        () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
            "History size should reduce by 1."),
        () -> Assertions.assertEquals(expectedLastElement, actualLastElement,
            "Incorrect last element in the history.")
    );
  }

  @Test
  void removeShouldDeleteTaskFromTheMiddleOfTheHistoryList() {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size() - 1;
    int positionToDelete = historyManager.getHistory().size() / 2;
    final Task taskToDelete = historyManager.getHistory().get(positionToDelete);
    final Task expectedMiddleElement = historyManager.getHistory().get(positionToDelete + 1);

    historyManager.remove(taskToDelete.getId());
    final Task actualMiddleElement = historyManager.getHistory().get(positionToDelete);
    int actualHistorySize = historyManager.getHistory().size();
    boolean isDeleted = !historyManager.getHistory().contains(taskToDelete);

    Assertions.assertAll(
        () -> Assertions.assertTrue(isDeleted, "Task was not removed."),
        () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
            "History size should reduce by 1."),
        () -> Assertions.assertEquals(expectedMiddleElement, actualMiddleElement,
            "Incorrect middle element in the history.")
    );
  }

  @ParameterizedTest
  @MethodSource("provideDeletionPositions")
  void removeShouldDeleteTaskFromDifferentPositionsOfTheHistoryList(int positionToDelete,
      int shiftForExpected, int shiftForActual, String deletionPosition) {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size() - 1;
    final Task taskToDelete = historyManager.getHistory().get(positionToDelete);
    final Task expectedMiddleElement = historyManager.getHistory()
        .get(positionToDelete + shiftForExpected);

    historyManager.remove(taskToDelete.getId());
    final Task actualMiddleElement = historyManager.getHistory()
        .get(positionToDelete + shiftForActual);
    int actualHistorySize = historyManager.getHistory().size();
    boolean isDeleted = !historyManager.getHistory().contains(taskToDelete);

    Assertions.assertAll(
        () -> Assertions.assertTrue(isDeleted, "Task was not removed."),
        () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
            "History size should reduce by 1."),
        () -> Assertions.assertEquals(expectedMiddleElement, actualMiddleElement,
            "Incorrect " + deletionPosition + " element in the history.")
    );
  }

  @Test
  void removeShouldDoNothingWhenIdIsNotExistInTheHistory() {
    fillHistoryManager();
    int expectedHistorySize = historyManager.getHistory().size();
    int idToDelete = 187;
    final Task taskToDelete = TestDataBuilder.buildTask(idToDelete, "t","d",TaskStatus.NEW);
    boolean isNotValidId = !historyManager.getHistory().contains(taskToDelete);

    historyManager.remove(idToDelete);
    boolean isNotSavedInHistory = !historyManager.getHistory().contains(taskToDelete);
    int actualHistorySize = historyManager.getHistory().size();

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedHistorySize,actualHistorySize, "History size should not change."),
        () -> Assertions.assertEquals(isNotValidId, isNotSavedInHistory, "Remove should mot save tasks.")
    );
  }

  @Test
  void shouldReturnEmptyList() {
    int expectedHistorySize = 0;

    final List<Task> actualHistory = historyManager.getHistory();
    int actualHistorySize = actualHistory.size();

    Assertions.assertEquals(expectedHistorySize, actualHistorySize, "Should be empty list.");
  }

  private static Object[][] provideDeletionPositions() {
    return new Object[][]{
        {0, 1, 0, "beginning"},
        {2, 1, 0, "middle"},
        {4, -1, -1, "end"}
    };
  }

  private static List<Task> provideDifferentTypesTasks() {
    Task task = TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW);
    Epic epic = TestDataBuilder.buildEpic(2, "epic", "description");
    Subtask subtask = TestDataBuilder.buildSubtask(3, "subtask", "notes", 2);
    return new ArrayList<>(Arrays.asList(task, epic, subtask));
  }

  private void fillHistoryManager() {
    List<Task> tasks = buildTasks();
    for (Task task : tasks) {
      historyManager.add(task);
    }
  }

  private List<Task> buildTasks() {
    return new LinkedList<>(
        List.of(TestDataBuilder.buildEpic(1, "epic1", "description"),
            TestDataBuilder.buildTask(2, "task1", "d", TaskStatus.NEW),
            TestDataBuilder.buildSubtask(3, "subtask1", "notes", 1),
            TestDataBuilder.buildEpic(4, "epic1", "description"),
            TestDataBuilder.buildSubtask(5, "subtask2", "notes", 1)));
  }
}