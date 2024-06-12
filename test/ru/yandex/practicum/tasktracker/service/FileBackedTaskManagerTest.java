package ru.yandex.practicum.tasktracker.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
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
import ru.yandex.practicum.tasktracker.exception.ManagerLoadException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

  private File file;

  @BeforeEach
  @Override
  void setUp() {
    try {
      file = File.createTempFile("emptyFile", ".csv");
      taskManager = FileBackedTaskManager.loadFromFile(file);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  void exceptionShouldBeThrownWhenFileIsInvalid() {
    final String filePath = " ";
    final String expectedExceptionMessage = "An Error occurred during reading the file";

    final Exception actualException = Assertions.assertThrows(ManagerLoadException.class, () -> {
      FileBackedTaskManager.loadFromFile(Path.of(filePath).toFile());
    });

    Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
        "Exception message should contain the expected text.");

  }

  @Test
  void loadFromFileCreatesNewObjectFromEmptyFile() {
    final int totalTasksNumber =
        taskManager.getTasks().size() + taskManager.getEpics().size()
            + taskManager.getSubtasks()
            .size() + taskManager.getHistory().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager, "TaskManager object should not be null."),
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
    addTasksToTheManager(taskManager, tasksData);
    final Map<TaskType, Integer> lastIds = getLastUsedIdsByType(taskManager);
    generateHistory(taskManager, lastIds);

    final List<Task> expectedTasks = taskManager.getTasks();
    expectedTasks.sort(Comparator.comparing(Task::getId));
    final List<Epic> expectedEpics = taskManager.getEpics();
    expectedEpics.sort(Comparator.comparing(Epic::getId));
    final List<Subtask> expectedSubtasks = taskManager.getSubtasks();
    expectedSubtasks.sort(Comparator.comparing(Subtask::getId));
    final List<Task> expectedHistory = new ArrayList<>(taskManager.getHistory());
    expectedHistory.sort(Comparator.comparing(Task::getId));
    final List<Task> expectedPrioritized = taskManager.getPrioritizedTasks();

    /* AND a new taskManager created from the file of previous one */
    final TaskManager newManager = FileBackedTaskManager.loadFromFile(file);
    final List<Task> actualTasks = newManager.getTasks();
    actualTasks.sort(Comparator.comparing(Task::getId));
    final List<Epic> actualEpics = newManager.getEpics();
    actualEpics.sort(Comparator.comparing(Epic::getId));
    final List<Subtask> actualSubtasks = newManager.getSubtasks();
    actualSubtasks.sort(Comparator.comparing(Subtask::getId));
    final List<Task> actualHistory = new ArrayList<>(newManager.getHistory());
    actualHistory.sort(Comparator.comparing(Task::getId));
    final List<Task> actualPrioritized = newManager.getPrioritizedTasks();

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
        () -> Assertions.assertIterableEquals(expectedPrioritized, actualPrioritized,
            "Prioritized list should be same."),
        () -> Assertions.assertFalse(expectedTasks.isEmpty(), "Task list has not restored."),
        () -> Assertions.assertFalse(expectedEpics.isEmpty(), "Epic list has not restored."),
        () -> Assertions.assertFalse(expectedSubtasks.isEmpty(), "Subtask list has not restored."),
        () -> Assertions.assertFalse(expectedHistory.isEmpty(), "History list has not restored."),
        () -> Assertions.assertFalse(expectedPrioritized.isEmpty(),
            "Prioritized list has not restored.")
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
        Task t = taskManager.addTask(testTask);
        epic = " ";
      }
      case EPIC -> {
        Epic e = taskManager.addEpic((Epic) testTask);
        epic = " ";
      }
      case SUBTASK -> {
        epicForId = taskManager.addEpic(TestDataBuilder.buildEpic("epic", "eee"));
        Subtask s = (Subtask) testTask;
        int epicId = epicForId.getId();
        s.setEpicId(epicId);
        taskManager.addSubtask(s);
        epicForId = taskManager.getEpics().stream().filter((e) -> e.getId() == epicId)
            .findFirst()
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
          prioritized
          %s,%s,%s,%s,%s,%s,%s,%s
          """.formatted(epicForId.getId(), epicForId.getType(), epicForId.getTitle(),
          epicForId.getStatus(), epicForId.getDescription(), epicForId.getDuration().toMinutes(),
          epicForId.getStartTime(), " ",
          testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic, testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic);
    } else if (TaskType.TASK == testTask.getType()) {
      expected = """
          id,type,name,status,description,duration,startTime,epic
          %s,%s,%s,%s,%s,%s,%s,%s
          history
          prioritized
          %s,%s,%s,%s,%s,%s,%s,%s
          """.formatted(testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic, testTask.getId(), testTask.getType(), testTask.getTitle(),
          testTask.getStatus(),
          testTask.getDescription(), testTask.getDuration().toMinutes(), testTask.getStartTime(),
          epic);
    } else {
      expected = """
          id,type,name,status,description,duration,startTime,epic
          %s,%s,%s,%s,%s,%s,%s,%s
          history
          prioritized
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

  private void addTasksToTheManager(final TaskManager manager, final List<Task> tasksData) {
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
    final int maxTask = manager.getTasks().stream().map(Task::getId)
        .max(Comparator.naturalOrder()).orElse(0);
    final int maxEpic = manager.getEpics().stream().map(Epic::getId)
        .max(Comparator.naturalOrder()).orElse(0);
    final int maxSubtask = manager.getSubtasks().stream().map(Subtask::getId)
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