package ru.yandex.practicum.tasktracker.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;

class FileBackedTaskManagerTest {

  private static final LocalDateTime BASE_TEST_TIME = LocalDateTime.parse(
      "2024-05-21T19:51:24.211613");
  private TaskManager manager;
  private File file;

  @BeforeEach
  void setUp() throws IOException {
    file = File.createTempFile("emptyFile", ".csv");
    manager = FileBackedTaskManager.loadFromFile(file);
  }

  @Test
  void loadFromFileCreatesNewObjectFromEmptyFile() {
    final int totalTasksNumber =
        manager.getAllTasks().size() + manager.getAllEpics().size() + manager.getAllSubtasks()
            .size() + manager.getHistory().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(manager, "TaskManager object should not be null."),
        () -> Assertions.assertEquals(0, totalTasksNumber,
            "All lists of Task, Epics, Subtasks and history should be empty.")
    );
  }

  @Test
  void fileBackedTaskManagerSavesAndRestoresDataSuccessfully() {
    /* GIVEN  empty taskManager, and testData - list of tasks of all types: Task, Epic, and Subtask */
    final List<Task> tasksData = TestDataBuilder.buildTasks();

    /* WHEN add all tasks to the given taskManager
     * AND view Epic */
    addTasksToManager(manager, tasksData);
    final Map<TaskType, Integer> lastIds = getLastUsedIdsByType(manager);
    generateHistory(manager, lastIds);
    final List<Task> expectedTasks = manager.getAllTasks();
    expectedTasks.sort(Comparator.comparing(Task::getId));
    final List<Epic> expectedEpics = manager.getAllEpics();
    expectedEpics.sort(Comparator.comparing(Epic::getId));
    final List<Subtask> expectedSubtasks = manager.getAllSubtasks();
    expectedSubtasks.sort(Comparator.comparing(Subtask::getId));
    final List<Task> expectedHistory = manager.getHistory();
    expectedHistory.sort(Comparator.comparing(Task::getId));

    /* AND a new taskManager created from the file of previous one */
    final TaskManager newManager = FileBackedTaskManager.loadFromFile(file);
    final List<Task> actualTasks = newManager.getAllTasks();
    actualTasks.sort(Comparator.comparing(Task::getId));
    final List<Epic> actualEpics = newManager.getAllEpics();
    actualEpics.sort(Comparator.comparing(Epic::getId));
    final List<Subtask> actualSubtasks = newManager.getAllSubtasks();
    actualSubtasks.sort(Comparator.comparing(Subtask::getId));
    final List<Task> actualHistory = newManager.getHistory();
    actualHistory.sort(Comparator.comparing(Task::getId));

    /* THEN all tasks should be saved in the file and restored */
    Assertions.assertAll(
        () -> Assertions.assertIterableEquals(expectedTasks, actualTasks,
            "Task list should be same."),
        () -> Assertions.assertIterableEquals(expectedEpics, actualEpics,
            "Epic list should be same."),
        () -> Assertions.assertIterableEquals(expectedSubtasks, actualSubtasks,
            "Subtask list should be same."),
        () -> Assertions.assertIterableEquals(expectedHistory, actualHistory,
            "History list should be same."),
        () -> Assertions.assertFalse(expectedTasks.isEmpty(), "Task list has not restored."),
        () -> Assertions.assertFalse(expectedEpics.isEmpty(), "Epic list has not restored."),
        () -> Assertions.assertFalse(expectedSubtasks.isEmpty(), "Subtask list has not restored."),
        () -> Assertions.assertFalse(expectedHistory.isEmpty(), "History list has not restored.")
    );
  }

  @ParameterizedTest
  @MethodSource("provideDifferentTypesTasks")
  <T extends Task> void saveSavesTasksToTheFileInCSVFormat(T testTask) throws IOException {
    String expected = "";
    String epic = " ";
    Epic epicForId = new Epic();

    switch (testTask.getType()) {
      case TASK -> {
        Task t = manager.addTask(testTask);
        epic = " ";
      }
      case EPIC -> {
        Epic e = manager.addEpic((Epic) testTask);
        epic = " ";
      }
      case SUBTASK -> {
        epicForId = manager.addEpic(TestDataBuilder.buildEpic("epic", "eee"));
        Subtask s = (Subtask) testTask;
        int epicId = epicForId.getId();
        s.setEpicId(epicId);
        manager.addSubtask(s);
        epicForId = manager.getAllEpics().stream().filter((e) -> e.getId() == epicId).findFirst()
            .orElse(null);
        epic = String.valueOf(epicId);
      }
    }
    if (TaskType.SUBTASK == testTask.getType()) {
      assert epicForId != null;
      expected = """
          id,type,name,status,description,duration,startTime,epic
          %s,%s,%s,%s,%s,%s,%s,%s
          %s,%s,%s,%s,%s,%s,%s,%s
          history
          """.formatted(epicForId.getId(), epicForId.getType(), epicForId.getTitle(),
          epicForId.getStatus(), epicForId.getDescription(), epicForId.getDuration().toMinutes(),
          epicForId.getStartTime(), " ",
          testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic);
    } else {
      expected = """
          id,type,name,status,description,duration,startTime,epic
          %s,%s,%s,%s,%s,%s,%s,%s
          history
          """.formatted(testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic);
    }

    final String actual = Files.readString(file.toPath());

    Assertions.assertEquals(expected, actual, "Wrong format saved.");
  }

  private static List<Task> provideDifferentTypesTasks() {
    final Task task = TestDataBuilder.buildTask(0, "task", "d", TaskStatus.NEW,
        Duration.ofMinutes(15), BASE_TEST_TIME);
    final Epic epic = TestDataBuilder.buildEpic("epic", "description");
    final Subtask subtask = TestDataBuilder.buildSubtask("subtask", "notes", 2,
        Duration.ofMinutes(15), BASE_TEST_TIME.plusMinutes(15));
    return new ArrayList<>(Arrays.asList(task, epic, subtask));
  }

  private void addTasksToManager(final TaskManager manager, final List<Task> tasksData) {
    for (Task task : tasksData) {
      switch (task.getType()) {
        case TASK -> manager.addTask(task);
        case EPIC -> manager.addEpic((Epic) task);
        case SUBTASK -> manager.addSubtask((Subtask) task);
      }
    }
  }

  private Map<TaskType, Integer> getLastUsedIdsByType(final TaskManager manager) {
    final Map<TaskType, Integer> lastIds = new HashMap<>();
    final int maxTask = manager.getAllTasks().stream().map(Task::getId)
        .max(Comparator.naturalOrder()).orElse(0);
    final int maxEpic = manager.getAllEpics().stream().map(Epic::getId)
        .max(Comparator.naturalOrder()).orElse(0);
    final int maxSubtask = manager.getAllSubtasks().stream().map(Subtask::getId)
        .max(Comparator.naturalOrder()).orElse(0);
    lastIds.put(TaskType.TASK, maxTask);
    lastIds.put(TaskType.EPIC, maxEpic);
    lastIds.put(TaskType.SUBTASK, maxSubtask);
    return lastIds;
  }

  private void generateHistory(TaskManager manager, Map<TaskType, Integer> ids) {
    int idToView = ids.get(TaskType.EPIC);
    manager.getEpicById(idToView);
  }

}