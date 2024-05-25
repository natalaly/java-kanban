package ru.yandex.practicum.tasktracker.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.exception.TaskValidationException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

public abstract class TaskManagerTest<T extends TaskManager> {

  protected static final LocalDateTime BASE_TEST_TIME = LocalDateTime.parse(
      "2024-05-21T19:51:24.211613");
  protected T taskManager;

  @BeforeEach
  abstract void setUp();

  @Test
  @DisplayName("getAllTasks() - should return a list of tasks.")
  public void getAllTaskShouldReturnList() {
    final Task task1 = TestDataBuilder.buildTask("t1", "d1");
    final Task task2 = TestDataBuilder.buildTask("t2", "d2");
    taskManager.addTask(task1);
    taskManager.addTask(task2);
    final List<Task> expected = new ArrayList<>(List.of(task1, task2));
    expected.sort(Comparator.comparing(Task::getId));

    final List<Task> actual = taskManager.getAllTasks();
    actual.sort(Comparator.comparing(Task::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getAllTasks() - returns an empty List when task manager is new.")
  public void getAllTaskShouldReturnEmptyListWhenThereIsNoTasks() {
    final List<Task> expected = List.of();

    final List<Task> actual = taskManager.getAllTasks();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getAllEpics() - should return a list of epics.")
  public void getAllEpicsShouldReturnList() {
    final Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
    final Epic epic2 = TestDataBuilder.buildEpic("e2", "d2");
    taskManager.addEpic(epic1);
    taskManager.addEpic(epic2);
    final List<Epic> expected = new ArrayList<>(List.of(epic1, epic2));
    expected.sort(Comparator.comparing(Epic::getId));

    final List<Epic> actual = taskManager.getAllEpics();
    actual.sort(Comparator.comparing(Epic::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getAllEpics() - returns an empty List when task manager is new.")
  public void getAllEpicShouldReturnEmptyListWhenThereIsNoEpic() {
    final List<Epic> expected = List.of();

    final List<Epic> actual = taskManager.getAllEpics();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getAllSubtasks() - should return a list of subtasks.")
  public void getAllSubtasksShouldReturnList() {
    final Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
    taskManager.addEpic(epic1);
    final Subtask subtask1 = TestDataBuilder.buildSubtask("st1", "d1", epic1.getId());
    final Subtask subtask2 = TestDataBuilder.buildSubtask("st2", "d2", epic1.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    final List<Subtask> expected = new ArrayList<>(List.of(subtask1, subtask2));
    expected.sort(Comparator.comparing(Subtask::getId));

    final List<Subtask> actual = taskManager.getAllSubtasks();
    actual.sort(Comparator.comparing(Subtask::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getAllSubtasks() - returns an empty List when task manager is new.")
  public void getAllSubtasksShouldReturnEmptyListWhenThereIsNoSubtasks() {
    final List<Subtask> expected = List.of();

    final List<Subtask> actual = taskManager.getAllSubtasks();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );
  }

  @Test
  @DisplayName("getHistory() - returns up to dated viewed tasks.")
  public void tasksInHistoryShouldUpdateTheirStateAfterUpdatingThemInTaskManager() {
    final Task addedTask = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
    int taskId = addedTask.getId();
    taskManager.getTaskById(taskId);
    final TaskStatus expectedStatus = TaskStatus.DONE;// taskManager.getHistory()
    // .get(taskManager.getHistory().size() - 1);

    addedTask.setStatus(expectedStatus);
    taskManager.updateTask(addedTask);
    final Task actual = taskManager.getHistory().get(taskManager.getHistory().size() - 1);

    Assertions.assertEquals(addedTask, actual, "Id is different");
    Assertions.assertEquals(addedTask.getTitle(), actual.getTitle(), "Title is different");
    Assertions.assertEquals(addedTask.getDescription(), actual.getDescription(),
        "Description is different");
    Assertions.assertEquals(addedTask.getStatus(), actual.getStatus(), "Status is different");
  }

  @Test
  @DisplayName("clearTasks() - deletes all tasks from the task manager.")
  public void clearTaskShouldDeleteAllTasksFromTheMemory() {
    final Task task1 = TestDataBuilder.buildTask("t1", "d1");
    final Task task2 = TestDataBuilder.buildTask("t2", "d2");
    taskManager.addTask(task1);
    taskManager.addTask(task2);

    taskManager.clearTasks();

    Assertions.assertEquals(List.of(), taskManager.getAllTasks(), "Tasks were not deleted.");
  }

  @Test
  @DisplayName("clearTasks() - deletes all tasks from the history.")
  public void clearTaskShouldDeleteAllTasksFromTheHistory() {
    buildHistoryInTaskManager();
    final List<Integer> taskIdsInMemory = taskManager.getAllTasks().stream().map(Task::getId)
        .toList();
    final List<Task> historyBeforeClear = taskManager.getHistory();

    taskManager.clearTasks();
    final List<Task> historyAfterClear = taskManager.getHistory();
    final List<Task> actualTasksInHistory = historyAfterClear.stream()
        .filter(t -> taskIdsInMemory.contains(t.getId())).toList();

    Assertions.assertAll(
        () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
            "History should be reduced"),
        () -> Assertions.assertEquals(0, actualTasksInHistory.size(),
            "Deleted tasks should not remain in the history")
    );
  }

  @Test
  @DisplayName("clearEpics() - deletes all epics from the task manager.")
  public void clearEpicsShouldDeleteAllEpicsAndSubtasksFromTheMemory() {
    Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
    Epic epic2 = TestDataBuilder.buildEpic("e2", "d2");
    taskManager.addEpic(epic1);
    taskManager.addEpic(epic2);

    taskManager.clearEpics();

    Assertions.assertAll(
        () -> Assertions.assertEquals(List.of(), taskManager.getAllEpics(),
            "Epics was not deleted."),
        () -> Assertions.assertEquals(List.of(), taskManager.getAllSubtasks(),
            "Subtasks were not deleted.")
    );
  }

  @Test
  @DisplayName("clearEpics() - deletes all epics from the history.")
  public void clearEpicShouldDeleteAllEpicsAndSubtasksFromTheHistory() {
    buildHistoryInTaskManager();
    final List<Integer> epicsIdsInMemory = taskManager.getAllEpics().stream().map(Epic::getId)
        .toList();
    final List<Integer> subtasksIdsInMemory = taskManager.getAllSubtasks().stream()
        .map(Subtask::getId).toList();
    final List<Task> historyBeforeClear = taskManager.getHistory();

    taskManager.clearEpics();
    final List<Task> historyAfterClear = taskManager.getHistory();
    final List<Task> actualEpicsInHistory = historyAfterClear.stream()
        .filter(t -> epicsIdsInMemory.contains(t.getId())).toList();
    final List<Task> actualSubtasksInHistory = historyAfterClear.stream()
        .filter(t -> subtasksIdsInMemory.contains(t.getId())).toList();

    Assertions.assertAll(
        () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
            "History should be reduced"),
        () -> Assertions.assertEquals(0, actualEpicsInHistory.size(),
            "Deleted epics should not remain in the history"),
        () -> Assertions.assertEquals(0, actualSubtasksInHistory.size(),
            "Deleted subtasks should not remain in the history")
    );
  }

  @Test
  @DisplayName("clearSubtasks() - deletes all subtasks from the task manager and from the corresponding epics.")
  public void clearSubtasksShouldDeleteAllSubtasksFromTheMemoryAndFromEpics() {
    Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
    taskManager.addEpic(epic1);
    Subtask subtask1 = TestDataBuilder.buildSubtask("st1", "d1", epic1.getId());
    Subtask subtask2 = TestDataBuilder.buildSubtask("st2", "d2", epic1.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);

    taskManager.clearSubtasks();
    ArrayList<Subtask> subtasksFromEpics = new ArrayList<>();
    for (Epic epic : taskManager.getAllEpics()) {
      subtasksFromEpics.addAll(taskManager.getSubtasksByEpicId(epic.getId()));
    }

    Assertions.assertAll(
        () -> Assertions.assertEquals(List.of(), taskManager.getAllSubtasks(),
            "Subtasks was not deleted."),
        () -> Assertions.assertEquals(List.of(), subtasksFromEpics,
            "Subtasks were not deleted from Epics.")
    );
  }

  @Test
  @DisplayName("clearSubtasks() - deletes all subtasks from the history.")
  public void clearSubtasksShouldDeleteAllSubtasksFromTheHistory() {
    buildHistoryInTaskManager();
    //given
    final List<Integer> subtasksIdsInMemory = taskManager.getAllSubtasks().stream()
        .map(Subtask::getId).toList(); // size = 2, [3,5]
    final List<Task> historyBeforeClear = taskManager.getHistory();
    //when
    taskManager.clearEpics();
    final List<Task> historyAfterClear = taskManager.getHistory();
    final List<Task> actualSubtasksInHistory = historyAfterClear.stream()
        .filter(t -> subtasksIdsInMemory.contains(t.getId())).toList();
    //then
    Assertions.assertAll(
        () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
            "History should be reduced"),
        () -> Assertions.assertEquals(0, actualSubtasksInHistory.size(),
            "Deleted subtasks should not remain in the history")
    );
  }


  @Test
  @DisplayName("deleteTask(int id) - removes task by id from the task manager and from the history.")
  public void deleteTaskShouldDeleteTaskByIdFromTheMemoryAndHistory() {
    final Task task1 = TestDataBuilder.buildTask("t1", "d1");
    final Task task2 = TestDataBuilder.buildTask("t2", "d2");
    taskManager.addTask(task1);
    final Task taskToDelete = taskManager.addTask(task2);
    final int idToDelete = taskToDelete.getId();
    taskManager.getTaskById(idToDelete);
    final int historySizeBefore = taskManager.getHistory().size();
    final String expectedExceptionMessage = "Task with Id " + idToDelete + " was not found.";

    taskManager.deleteTask(idToDelete);
    final boolean isDeletedFromHistory = !taskManager.getHistory().contains(taskToDelete);
    final int actualHistorySize = taskManager.getHistory().size();

    TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> {
          taskManager.getTaskById(idToDelete);
        });

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
            "Task was not deleted."),
        () -> Assertions.assertTrue(isDeletedFromHistory, "Task was not deleted from the history."),
        () -> Assertions.assertTrue(actualHistorySize < historySizeBefore,
            "History should reduce its size.")
    );
  }

  @Test
  @DisplayName("deleteEpic(int id) - removes epic by id and its subtasks from the task manager and from the history.")
  public void deleteEpicShouldDeleteEpicsByIdAndItsSubtasksFromTheMemoryAndHistory() {
    taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d1"));
    final Epic epicToDelete = taskManager.addEpic(TestDataBuilder.buildEpic("e2", "d2"));
    final Subtask sbToDelete = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("sb1", "d", epicToDelete.getId()));
    taskManager.getEpicById(epicToDelete.getId());
    taskManager.getSubtaskById(sbToDelete.getId());
    final List<Task> historyBeforeDeleting = taskManager.getHistory();
    final String expectedExceptionMessage =
        "Epic with Id " + epicToDelete.getId() + " was not found.";

    taskManager.deleteEpic(epicToDelete.getId());
    final List<Task> actualHistory = taskManager.getHistory();
    final TaskNotFoundException actualException = Assertions.assertThrows(
        TaskNotFoundException.class, () -> taskManager.getEpicById(epicToDelete.getId()));
    final List<Subtask> actualSubtasksInMemory = taskManager.getAllSubtasks().stream()
        .filter(st -> st.getEpicId() == epicToDelete.getId()).toList();
    final boolean isEpicDeletedFromHistory = !actualHistory.contains(epicToDelete);
    final boolean isSubtaskDeletedFromHistory = !actualHistory.contains(sbToDelete);

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
            "Exception message should contain the expected text."),
        () -> Assertions.assertEquals(List.of(), actualSubtasksInMemory,
            "Subtasks were not deleted."),
        () -> Assertions.assertTrue(actualHistory.size() < historyBeforeDeleting.size(),
            "History had not reduced it's size."),
        () -> Assertions.assertTrue(isEpicDeletedFromHistory,
            "Epic was not deleted from the history."),
        () -> Assertions.assertTrue(isSubtaskDeletedFromHistory,
            "Corresponding subtask was not deleted from the history.")
    );
  }

  @Test
  @DisplayName("deleteSubtask(int id) - removes subtasks from the task manager, history and from the corresponding epic. ")
  public void deleteSubtaskShouldDeleteSubtaskFromTheMemoryFromItsEpicAndFromTheHistory() {
    final Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d1"));
    taskManager.addSubtask(TestDataBuilder.buildSubtask("st1", "d1", epicInMemory.getId()));
    final Subtask subtaskToDelete = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("st2", "d2", epicInMemory.getId()));
    final int idSubtaskToDelete = subtaskToDelete.getId();
    taskManager.getSubtaskById(idSubtaskToDelete);
    final List<Task> historyBeforeDeleting = taskManager.getHistory();

    taskManager.deleteSubtask(idSubtaskToDelete);
    final List<Task> actualHistory = taskManager.getHistory();
    final Set<Subtask> subtasksFromEpic = taskManager.getSubtasksByEpicId(epicInMemory.getId());
    boolean isDeletedFromEpic = subtasksFromEpic.stream()
        .noneMatch((st) -> st.getId() == idSubtaskToDelete);
    boolean isDeletedFromTheHistory = !actualHistory.contains(idSubtaskToDelete);

    Assertions.assertAll(
        () -> Assertions.assertThrows(TaskNotFoundException.class,
            () -> taskManager.getSubtaskById(idSubtaskToDelete),
            "Subtask was not deleted."),
        () -> Assertions.assertTrue(isDeletedFromEpic, "Subtask was not deleted from Epic."),
        () -> Assertions.assertTrue(actualHistory.size() < historyBeforeDeleting.size(),
            "History had not reduced it's size."),
        () -> Assertions.assertTrue(isDeletedFromTheHistory,
            "Subtask was not deleted from the history.")
    );
  }

  @Test
  @DisplayName("getTaskById(int id) - with valid id , returns task.")
  public void getTaskByIdShouldReturnTaskWhenIdIsValid() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
    int taskInMemoryId = taskInMemory.getId();

    Task returnedTask = taskManager.getTaskById(taskInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedTask, "Task was not returned."),
        () -> Assertions.assertEquals(taskInMemory, returnedTask, "Returned wrong task. ")
    );
  }

  @Test
  @DisplayName("getTaskById(int id) - throws an exception when id is not valid.")
  public void getTaskByIdShouldThrowAnExceptionWhenIdIsNotValid() {
    final String expectedExceptionMessage = "Task with Id " + 0 + " was not found.";

    TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getTaskById(0));

    Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
        "Exception message should contain the expected text.");
  }

  @Test
  @DisplayName("getTaskById(int id) - saved task to the history.")
  public void getTaskByIdShouldSaveTaskToHistory() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
    int taskInMemoryId = taskInMemory.getId();
    List<Task> expected = List.of(taskInMemory);

    taskManager.getTaskById(taskInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
  }

  @Test
  @DisplayName("getTaskById(int id) - with invalid id, does not save task to the viewing history.")
  public void getTaskByInvalidIdShouldNotSaveTaskToHistory() {
    int taskId = 0;
    List<Task> expected = List.of();

    TaskNotFoundException exception = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getTaskById(taskId));
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }

  @Test
  @DisplayName("getEpicById(int id) - returns epic when exist in the task manager.")
  public void getEpicByIdShouldReturnEpicWhenIdIsValid() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
    int epicInMemoryId = epicInMemory.getId();

    Epic returnedEpic = taskManager.getEpicById(epicInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedEpic, "Epic was not returned."),
        () -> Assertions.assertEquals(epicInMemory, returnedEpic, "Returned wrong epic. ")
    );
  }

  @Test
  @DisplayName("getEpicById(int id) - throws an exception when id is invalid.")
  public void getEpicByIdShouldThrowAnExceptionWhenIdIsNotValid() {
    final String expectedExceptionMessage = "Epic with Id " + 0 + " was not found.";
    TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getEpicById(0));

    Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
        "Exception message should contain the expected text.");
  }

  @Test
  @DisplayName("getEpicById(int id) - saved epic to the history id is correct.")
  public void getEpicByIdShouldSaveEpicToHistory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("t", "d"));
    int epicInMemoryId = epicInMemory.getId();
    List<Epic> expected = List.of(epicInMemory);

    taskManager.getEpicById(epicInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
  }

  @Test
  @DisplayName("getEpicById(int id) - does not save  to the history when id is invalid. ")
  public void getEpicByInvalidIdShouldNotSaveEpicToHistory() {
    int epicId = 0;
    List<Task> expected = List.of();

    TaskNotFoundException exception = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getEpicById(epicId));
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }

  @Test
  @DisplayName("getSubtaskById(int id) - returns a subtask when exist in task manager;")
  public void getSubtaskByIdShouldReturnSubtaskWhenIdIsValid() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
    int epicInMemoryId = epicInMemory.getId();
    Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("st", "d", epicInMemoryId));
    int subtaskInMemoryId = subtaskInMemory.getId();

    Subtask returnedSubtask = taskManager.getSubtaskById(subtaskInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedSubtask, "Subtask was not returned."),
        () -> Assertions.assertEquals(subtaskInMemory, returnedSubtask, "Returned wrong subtask. ")
    );
  }

  @Test
  @DisplayName("getSubtaskById(int id) - throws an exception when id in invalid. ")
  public void getSubtaskByIdShouldThrowAnExceptionWhenIdIsNotValid() {
    final String expectedExceptionMessage = "Subtask with Id " + 0 + " was not found.";

    TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getSubtaskById(0));

    Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage(),
        "Exception message should contain the expected text.");
  }

  @Test
  @DisplayName("getSubtaskById(int id) - saves subtask to the history when id is correct.")
  public void getSubtaskByIdShouldSaveSubtaskToHistory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
    int epicInMemoryId = epicInMemory.getId();
    Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("st", "d", epicInMemoryId));
    int subtaskInMemoryId = subtaskInMemory.getId();
    List<Subtask> expected = List.of(subtaskInMemory);

    taskManager.getSubtaskById(subtaskInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Subtask was not added to the history.");
  }

  @Test
  @DisplayName("getSubtaskById(int id) - does not save to the history when id is not valid.")
  public void getSubtaskByInvalidIdShouldNotSaveSubtaskToHistory() {
    int subtaskId = 0;
    List<Subtask> expected = List.of();

    TaskNotFoundException exception = Assertions.assertThrows(TaskNotFoundException.class,
        () -> taskManager.getSubtaskById(subtaskId));
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }


  @Test
  @DisplayName("addTask(Task) - saves task to the task manager.")
  void addTaskShouldSaveTaskInTaskManager() {
    Task taskToAdd = TestDataBuilder.buildTask("task", "d");
    int expectedNumberOfTasks = taskManager.getAllTasks().size() + 1;

    Task task = taskManager.addTask(taskToAdd);
    int actualNumberOfTasks = taskManager.getAllTasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getTaskById(task.getId()),
            "Task was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllTasks(), "Tasks are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfTasks, actualNumberOfTasks,
            "Incorrect number of tasks.")
    );
  }

  @Test
  @DisplayName("addTask(Task) - does not change task when saving.")
  void addedTaskRemainsUnchangedWhenAddedToTheTaskManager() {
    Task taskToAdd = TestDataBuilder.buildTask("task", "d");

    Task task = taskManager.addTask(taskToAdd);
    Task taskSaved = taskManager.getTaskById(task.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskSaved, "Task was not saved."),
        () -> Assertions.assertEquals(taskToAdd.getTitle(), taskSaved.getTitle(),
            "Title was changed."),
        () -> Assertions.assertEquals(taskToAdd.getDescription(), taskSaved.getDescription(),
            "Description was changed."),
        () -> Assertions.assertEquals(taskToAdd.getStatus(), taskSaved.getStatus(),
            "Status was changed.")
    );
  }

  @Test
  @DisplayName("addTask(Task) - generates new ID.")
  void addTaskShouldGenerateNewIdWhenSavingInTaskManagerTaskWithId() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
    int taskInMemoryId = taskInMemory.getId();
    Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);
    int expectedNumberOfTasks = taskManager.getAllTasks().size() + 1;

    Task taskAdded = taskManager.addTask(taskToAdd);
    int actualNumberOfTasks = taskManager.getAllTasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(taskInMemoryId, taskAdded.getId(),
            "Id was not generated."),
        () -> Assertions.assertEquals(expectedNumberOfTasks, actualNumberOfTasks,
            "New task was not added.")
    );
  }

  @Test
  @DisplayName("addTask(Task) - does not update existed task in the task manager.")
  void addTaskWithExistedInTaskManagerIdShouldNotUpdateTaskInMemory() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
    int taskInMemoryId = taskInMemory.getId();
    Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);

    taskManager.addTask(taskToAdd);

    Assertions.assertAll(
        () -> Assertions.assertEquals(taskInMemory.getTitle(),
            taskManager.getTaskById(taskInMemoryId).getTitle(),
            "Task title in memory was changed."),
        () -> Assertions.assertEquals(taskInMemory.getDescription(),
            taskManager.getTaskById(taskInMemoryId).getDescription(),
            "Task description in memory was changed."),
        () -> Assertions.assertEquals(taskInMemory.getStatus(),
            taskManager.getTaskById(taskInMemoryId).getStatus(),
            "Task status in memory was changed.")
    );
  }

  @Test
  @DisplayName("addTask(Task) - avoids leaking references.")
  void addTaskSavesThreeDifferentIssuesFromTheSameSourceUsedThreeTimes() {
    Task taskToUse = TestDataBuilder.buildTask("Task", "Same task different id");
    final Set<String> expectedTaskTitles = Set.of("Task");
    final Set<String> expectedTaskDescriptions = Set.of("Same task different id");
    final int expectedTasksIdsNumbers = 3;
    final int expectedSize = 3;

    taskManager.addTask(taskToUse);
    taskManager.addTask(taskToUse);
    taskManager.addTask(taskToUse);

    final int actualSize = taskManager.getAllTasks().size();
    final Set<String> actualTaskTitles = taskManager.getAllTasks().stream().map(Task::getTitle)
        .collect(Collectors.toSet());
    final Set<String> actualTaskDescriptions = taskManager.getAllTasks().stream()
        .map(Task::getDescription).collect(Collectors.toSet());
    final int actualTasksIdsNumbers = taskManager.getAllTasks().stream().map(Task::getId)
        .collect(Collectors.toSet()).size();

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedSize, actualSize,
            "The total number of tasks is not correct."),
        () -> Assertions.assertIterableEquals(expectedTaskTitles, actualTaskTitles,
            "Titles should be same."),
        () -> Assertions.assertIterableEquals(expectedTaskDescriptions, actualTaskDescriptions,
            "Description should be same."),
        () -> Assertions.assertEquals(expectedTasksIdsNumbers, actualTasksIdsNumbers,
            "Ids should be same.")
    );
  }

  @Test
  @DisplayName("addEpic(Epic) - saves an epic to the task manager.")
  void addEpicShouldSaveEpicInTaskManager() {
    Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");
    int expectedNumberOfEpic = taskManager.getAllEpics().size() + 1;

    Epic epic = taskManager.addEpic(epicToAdd);
    int actualNumberOfEpics = taskManager.getAllEpics().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getEpicById(epic.getId()),
            "Epic was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllEpics(), "Epics are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfEpic, actualNumberOfEpics,
            "Incorrect number of epics.")
    );
  }

  @Test
  @DisplayName("addEpic(Epic) - does not change when is saved.")
  void addedEpicRemainsUnchangedWhenAddedToTheTaskManager() {
    Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");

    taskManager.addEpic(epicToAdd);
    Task epicSaved = taskManager.getEpicById(epicToAdd.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(epicSaved, "Epic was not saved"),
        () -> Assertions.assertEquals(epicToAdd.getTitle(), epicSaved.getTitle(),
            "Title was changed."),
        () -> Assertions.assertEquals(epicToAdd.getDescription(),
            epicSaved.getDescription(), "Description was changed.")
    );
  }

  @Test
  @DisplayName("addEpic(Epic) - generates an ID.")
  void addEpicShouldGenerateNewIdWhenSavingInTaskManagerEpicWithId() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "e2", "d");
    int expectedNumberOfEpics = taskManager.getAllEpics().size() + 1;

    Epic epicAdded = taskManager.addEpic(epicToAdd);
    int actualNumberOfEpics = taskManager.getAllEpics().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(epicInMemoryId, epicAdded.getId(),
            "Id was not generated."),
        () -> Assertions.assertEquals(expectedNumberOfEpics, actualNumberOfEpics,
            "New epic was not added.")
    );
  }

  @Test
  @DisplayName("addEpic(Epic) - does not update existed in the task manager epic.")
  void addEpicWithExistedInTaskManagerIdShouldNotUpdateEpicInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    TaskStatus epicInMemoryStatus = epicInMemory.getStatus();
    Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "t2", "d");

    taskManager.addEpic(epicToAdd);

    Assertions.assertAll(
        () -> Assertions.assertEquals(epicInMemory.getTitle(),
            taskManager.getEpicById(epicInMemoryId).getTitle(),
            "Task title in memory was changed."),
        () -> Assertions.assertEquals(epicInMemory.getDescription(),
            taskManager.getEpicById(epicInMemoryId).getDescription(),
            "Task description in memory was changed."),
        () -> Assertions.assertEquals(epicInMemoryStatus,
            taskManager.getEpicById(epicInMemoryId).getStatus(),
            "Task status in memory was changed.")
    );
  }

  @Test
  @DisplayName("addEpic(Epic) - can not be a subtask for itself.")
  void EpicThrowsExceptionWhenAddingItselfAsSubtask() {
    Task epic = TestDataBuilder.buildEpic("Epic", "d");

    taskManager.addEpic((Epic) epic);

    Assertions.assertThrows(ClassCastException.class, () -> {
      taskManager.addSubtask((Subtask) epic);
    });
  }

  @Test
  @DisplayName("addSubtask(Subtask) - saved a subtask to the task manager.")
  void addSubtaskShouldSaveSubtaskInTaskManager() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d", epicInMemory.getId());
    int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

    Subtask savedSubtask = taskManager.addSubtask(subtaskToAdd);
    int actualNumberOfSubtasks = taskManager.getAllSubtasks().size();
    boolean subtaskExistInEpic = taskManager.getSubtasksByEpicId(savedSubtask.getEpicId())
        .contains(savedSubtask);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(savedSubtask, "Subtask was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllSubtasks(), "Subtasks are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks, actualNumberOfSubtasks,
            "Incorrect number of tasks."),
        () -> Assertions.assertTrue(subtaskExistInEpic, "Subtask was not saved inside epic.")
    );
  }

  @Test
  @DisplayName("addSubtask(Subtask) - does not change when is saved.")
  void addedSubtaskRemainsUnchangedWhenAddedToTheTaskManager() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d", epicInMemory.getId());

    taskManager.addSubtask(subtaskToAdd);
    Subtask savedSubtask = taskManager.getSubtaskById(subtaskToAdd.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(savedSubtask, "Subtask was not saved."),
        () -> Assertions.assertEquals(subtaskToAdd.getTitle(), savedSubtask.getTitle(),
            "Title was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getDescription(), savedSubtask.getDescription(),
            "Description was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getStatus(), savedSubtask.getStatus(),
            "Status was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getEpicId(), savedSubtask.getEpicId(),
            "Corresponding epic was changed.")
    );
  }

  @Test
  @DisplayName("addSubtask(Subtask) - generated a new ID.")
  void addSubtaskShouldGenerateNewIdWhenSavingInTaskManagerSubtaskWithId() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("t1", "d", epicInMemory.getId()));
    int subtaskInMemoryId = subtaskInMemory.getId();
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "t2", "d",
        subtaskInMemory.getEpicId());
    int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

    Subtask subtaskAdded = taskManager.addSubtask(subtaskToAdd);
    int actualNumberOfTasks = taskManager.getAllSubtasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(subtaskInMemoryId, subtaskAdded.getId(),
            "Id was not generated"),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks, actualNumberOfTasks,
            "New subtask was not added.")
    );
  }

  @Test
  @DisplayName("addSubtask(Subtask) - does not update existed in the task manager subtask.")
  void addSubtaskWithExistedInTaskManagerIdShouldNotUpdateSubtaskInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("st1", "d", epicInMemory.getId()));
    int subtaskInMemoryId = subtaskInMemory.getId();
    int expectedNumberSubtasksInEpic =
        taskManager.getSubtasksByEpicId(epicInMemory.getId()).size() + 1;
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "st2", "d",
        epicInMemory.getId());

    Subtask subtaskAdded = taskManager.addSubtask(subtaskToAdd);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getSubtaskById(subtaskAdded.getId()),
            "Subtask was not found."),
        () -> Assertions.assertEquals(subtaskInMemory.getTitle(),
            taskManager.getSubtaskById(subtaskInMemoryId).getTitle(),
            "Subtask title in memory was changed."),
        () -> Assertions.assertEquals(subtaskInMemory.getDescription(),
            taskManager.getSubtaskById(subtaskInMemoryId).getDescription(),
            "Subtask description in memory was changed."),
        () -> Assertions.assertEquals(subtaskInMemory.getStatus(),
            taskManager.getSubtaskById(subtaskInMemoryId).getStatus(),
            "Subtask status in memory was changed."),
        () -> Assertions.assertEquals(expectedNumberSubtasksInEpic,
            taskManager.getSubtasksByEpicId(epicInMemory.getId()).size(),
            "Subtask wasn't found in epic.")
    );
  }

  @Test
  @DisplayName("addSubtask(Subtask) - does not add a subtask to the task manager when epic id is invalid.")
  void addSubtaskShouldThrowAnExceptionAndNotAddSubtaskWithIncorrectEpicId() {
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("st1", "d", 0);
    final int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size();
    final String expectedMessage = "Invalid epic Id";

    TaskValidationException actualException = Assertions.assertThrows(TaskValidationException.class,
        () -> {
          taskManager.addSubtask(subtaskToAdd);
        });

    Assertions.assertAll(
        () -> Assertions.assertTrue(actualException.getMessage().contains(expectedMessage),
            "Exception message should contain the expected text."),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks, taskManager.getAllSubtasks().size(),
            "Number of subtasks should remain the same after exception.")
    );
  }

  @Test
  @DisplayName("addSubtask(Subtask) - can not add a subtask to the subtask.")
  void subtaskShouldNotBeAbleBecomeItsOwnSubtask() {
    final Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("Epic", "d"));
    final Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("Sb", "D", epicInMemory.getId()));
    final Subtask subtaskToAdd = TestDataBuilder.buildCopySubtask(subtaskInMemory);
    subtaskToAdd.setEpicId(subtaskInMemory.getId());
    final String expectedMessage = "Invalid epic Id";

    final TaskValidationException actualException = Assertions.assertThrows(
        TaskValidationException.class, () -> {
          taskManager.addSubtask(subtaskToAdd);
        });
    final boolean existsSubtaskAsSubtaskItself =
        taskManager.getAllSubtasks().stream().anyMatch(sb -> sb.getEpicId() == sb.getId());

    Assertions.assertAll(
        () -> Assertions.assertTrue(actualException.getMessage().contains(expectedMessage),
            "Exception message should contain the expected text."),
        () -> Assertions.assertThrows(TaskNotFoundException.class,
            () -> taskManager.getEpicById(subtaskInMemory.getId())),
        () -> Assertions.assertFalse(existsSubtaskAsSubtaskItself)
    );
  }

  @Test
  @DisplayName("updateTask(Task) - can update status.")
  void updateTaskShouldChangeStatusInMemory() {
    Task taskToAdd = TestDataBuilder.buildTask("task", "d");
    Task task = taskManager.addTask(taskToAdd);
    Task changes = TestDataBuilder.buildCopyTask(taskManager.getTaskById(task.getId()));
    changes.setStatus(TaskStatus.DONE);

    taskManager.updateTask(changes);
    TaskStatus actual = taskManager.getTaskById(task.getId()).getStatus();

    Assertions.assertEquals(TaskStatus.DONE, actual, "Status was not updated");
  }

  @Test
  @DisplayName("updateEpic(Epic) - do not change status of the epic.")
  void updateEpicShouldNotChangeStatusInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    TaskStatus expected = epicInMemory.getStatus();
    Epic changes = TestDataBuilder.buildCopyEpic(taskManager.getEpicById(epicInMemoryId));
    changes.setStatus(TaskStatus.DONE);

    taskManager.updateEpic(changes);

    Assertions.assertEquals(expected, taskManager.getEpicById(epicInMemoryId).getStatus(),
        "Status of epic was updated manually");
  }

  @Test
  @DisplayName("updateSubtask(Subtask) - can change status of the existed subtask. ")
  void updateSubtaskShouldChangeItsStatusInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    Subtask subtaskInMemory = taskManager.addSubtask(
        TestDataBuilder.buildSubtask("st1", "d", epicInMemoryId));
    int subtaskInMemoryId = subtaskInMemory.getId();
    Subtask changes = TestDataBuilder.buildCopySubtask(
        taskManager.getSubtaskById(subtaskInMemoryId));
    changes.setStatus(TaskStatus.IN_PROGRESS);

    taskManager.updateSubtask(changes);
    Set<Subtask> subtasksFromEpic = taskManager.getSubtasksByEpicId(epicInMemoryId);
    TaskStatus actualInEpic = subtasksFromEpic.stream()
        .filter((st) -> st.getId() == subtaskInMemoryId).findFirst().get().getStatus();

    Assertions.assertAll(
        () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS,
            taskManager.getSubtaskById(subtaskInMemoryId).getStatus()),//,
        () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualInEpic,
            "Status inside Epic was not updated.")
    );

  }

  @ParameterizedTest
  @DisplayName("Tasks with no time conflict should be added.")
  @MethodSource("provideTasksWithoutTimeConflict")
  void addTaskShouldSaveNonConflictingTaskInTaskManager(Task task1,Task task2, String message ) {
    Assertions.assertDoesNotThrow(() -> taskManager.addTask(task1),message);
    Assertions.assertDoesNotThrow(() -> taskManager.addTask(task2), message);
    Assertions.assertEquals(2, taskManager.getAllTasks().size(), "Tasks should be added.");
  }

  @ParameterizedTest
  @DisplayName("Adding tasks with time conflict throws an Exception.")
  @MethodSource("provideTasksWithTimeConflict")
  void addTaskShouldTrowWhenHaveTimeConflict(Task task1,Task task2, String message ) {
    final String expectedExceptionMessage ="Task has time conflict with existing task with ID ";
    task1 = taskManager.addTask(task1);
    final Exception actualException = Assertions.assertThrows(TaskValidationException.class,() -> {
     taskManager.addTask(task2);
   });
    Assertions.assertEquals(expectedExceptionMessage + task1.getId(), actualException.getMessage(), message);
    Assertions.assertEquals(1,taskManager.getPrioritizedTasks().size(), "Only one task should be added.");
  }

  private static Stream<Arguments> provideTasksWithTimeConflict() {
    final Duration duration = Duration.ofMinutes(15);
    return Stream.of(
        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, Duration.ofMinutes(45),BASE_TEST_TIME),
            "Task1 starts after Task2 starts and ends before task2 ends: should have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, Duration.ofMinutes(45),BASE_TEST_TIME),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            "Task2 starts after task1 starts and ends before task1 ends: should have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, Duration.ofMinutes(30),BASE_TEST_TIME),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            "Task1 starts before task2 starts and ends after task2 starts: should have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            TestDataBuilder.buildTask(0,"task1","d2",TaskStatus.NEW, Duration.ofMinutes(30),BASE_TEST_TIME),
            "Task1 starts before Task2 ends and ends after task2 ends: should have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME),
            "Task1 and Task2 starts and ends at the same time: should have conflict.")
    );
  }

  private static Stream<Arguments> provideTasksWithoutTimeConflict() {
    final Duration duration = Duration.ofMinutes(15);
    return Stream.of(
        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(30)),
            "Task1 starts and ends before Task2 starts: should not have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(30)),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME),
            "Task1 starts and ends after Task2 ends: should not have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            "Task2 starts at time when Task1 ends: should not have conflict."),

        Arguments.of(TestDataBuilder.buildTask(0,"task1","d1",TaskStatus.NEW, duration,BASE_TEST_TIME.plusMinutes(15)),
            TestDataBuilder.buildTask(0,"task2","d2",TaskStatus.NEW, duration,BASE_TEST_TIME),
            "Task1 starts at time when Task2 ends: should not have conflict.")
    );
  }

  private void buildHistoryInTaskManager() {
    final List<Task> tasks = TestDataBuilder.buildTasks();
    List<Integer> ids = new ArrayList<>();
    for (Task t : tasks) {
      if (t instanceof Epic) {
        ids.add(taskManager.addEpic((Epic) t).getId());
      } else if (t instanceof Subtask) {
        Subtask st = (Subtask) t;
        st.setEpicId(ids.get(0));
        ids.add(taskManager.addSubtask(st).getId());
      } else {
        ids.add(taskManager.addTask(t).getId());
      }
    }
    for (Task t : tasks) {
      if (t instanceof Epic) {
        taskManager.getEpicById(t.getId());
      } else if (t instanceof Subtask) {
        taskManager.getSubtaskById(t.getId());
      } else {
        taskManager.getTaskById(t.getId());
      }
    }
  }



}
