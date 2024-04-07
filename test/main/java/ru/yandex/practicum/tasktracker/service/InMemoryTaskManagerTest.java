package main.java.ru.yandex.practicum.tasktracker.service;

import main.java.ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import main.java.ru.yandex.practicum.tasktracker.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class InMemoryTaskManagerTest {
  private TaskManager taskManager;

  @BeforeEach
  void setUp() {
    taskManager = new InMemoryTaskManager();
  }

  // getAllTasks();
  @Test
  public void getAllTaskShouldReturnList() {
    Task task1 = TestDataBuilder.buildTask("t1","d1");
    Task task2 = TestDataBuilder.buildTask("t2","d2");
    taskManager.addTask(task1);
    taskManager.addTask(task2);
    List<Task> expected = new ArrayList<>(List.of(task1, task2));
    expected.sort(Comparator.comparing(Task::getId));

    List<Task> actual = taskManager.getAllTasks();
    actual.sort(Comparator.comparing(Task::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  @Test
  public void getAllTaskShouldReturnEmptyListWhenThereIsNoTasks() {
    List<Task> expected = List.of();

    List<Task> actual = taskManager.getAllTasks();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  // getAllEpics();
  @Test
  public void getAllEpicsShouldReturnList() {
    Epic epic1 = TestDataBuilder.buildEpic("e1","d1");
    Epic epic2 = TestDataBuilder.buildEpic("e2","d2");
    taskManager.addEpic(epic1);
    taskManager.addEpic(epic2);
    List<Epic> expected = new ArrayList<>(List.of(epic1, epic2));
    expected.sort(Comparator.comparing(Epic::getId));

    List<Epic> actual = taskManager.getAllEpics();
    actual.sort(Comparator.comparing(Epic::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  @Test
  public void getAllEpicShouldReturnEmptyListWhenThereIsNoEpic() {
    List<Epic> expected = List.of();

    List<Epic> actual = taskManager.getAllEpics();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  // getAllSubtasks();
  @Test
  public void getAllSubtasksShouldReturnList() {
    Epic epic1 = TestDataBuilder.buildEpic("e1","d1");
    taskManager.addEpic(epic1);
    Subtask subtask1 = TestDataBuilder.buildSubtask("st1","d1", epic1.getId());
    Subtask subtask2 = TestDataBuilder.buildSubtask("st2","d2",epic1.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    List<Subtask> expected = new ArrayList<>(List.of(subtask1, subtask2));
    expected.sort(Comparator.comparing(Subtask::getId));

    List<Subtask> actual = taskManager.getAllSubtasks();
    actual.sort(Comparator.comparing(Subtask::getId));

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List was not returned."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  @Test
  public void getAllSubtasksShouldReturnEmptyListWhenThereIsNoSubtasks() {
    List<Subtask> expected = List.of();

    List<Subtask> actual = taskManager.getAllSubtasks();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(actual, "List should not be null."),
        () -> Assertions.assertIterableEquals(expected, actual, "List returned is not correct.")
    );

  }

  // clear...();
  @Test
  public void clearTaskShouldDeleteAllTasksFromTheMemory() {
    Task task1 = TestDataBuilder.buildTask("t1","d1");
    Task task2 = TestDataBuilder.buildTask("t2","d2");
    taskManager.addTask(task1);
    taskManager.addTask(task2);

    taskManager.clearTasks();

    Assertions.assertEquals(List.of(), taskManager.getAllTasks(), "Tasks was not deleted.");
  }

  @Test
  public void clearEpicsShouldDeleteAllEpicsAndSubtasksFromTheMemory() {
    Epic epic1 = TestDataBuilder.buildEpic("e1","d1");
    Epic epic2 = TestDataBuilder.buildEpic("e2","d2");
    taskManager.addEpic(epic1);
    taskManager.addEpic(epic2);

    taskManager.clearEpics();

    Assertions.assertAll(
        () -> Assertions.assertEquals(List.of(),taskManager.getAllEpics(), "Epics was not deleted."),
        () -> Assertions.assertEquals(List.of(),taskManager.getAllSubtasks(), "Subtasks was not deleted.")
    );
  }

  @Test
  public void clearSubtasksShouldDeleteAllSubtasksFromTheMemoryAndFromEpics() {
    Epic epic1 = TestDataBuilder.buildEpic("e1","d1");
    taskManager.addEpic(epic1);
    Subtask subtask1 = TestDataBuilder.buildSubtask("st1","d1", epic1.getId());
    Subtask subtask2 = TestDataBuilder.buildSubtask("st2","d2",epic1.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);

    taskManager.clearSubtasks();
    ArrayList<Subtask> subtasksFromEpics = new ArrayList<>();
    for (Epic epic : taskManager.getAllEpics()) {;
      subtasksFromEpics.addAll(taskManager.getSubtasksByEpicId(epic.getId()));
    }

    Assertions.assertAll(
        () -> Assertions.assertEquals(List.of(),taskManager.getAllSubtasks(), "Subtasks was not deleted."),
        () -> Assertions.assertEquals(List.of(),subtasksFromEpics, "Subtasks was not deleted from Epics.")
    );
  }

  // getTaskById();
  @Test
  public void getTaskByIdShouldReturnTaskWhenIdIsValid() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t","d"));
    int taskInMemoryId = taskInMemory.getId();

    Task returnedTask = taskManager.getTaskById(taskInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedTask,"Task was not returned."),
        () -> Assertions.assertEquals(taskInMemory,returnedTask, "Returned wrong task. ")
    );
  }

  @Test
  public void getTaskByIdShouldReturnNullWhenIdIsNotValid() {
    Task returnedTask = taskManager.getTaskById(0);

   Assertions.assertNull(returnedTask,"Task was not returned.");
  }

  @Test
  public void getTaskByIdShouldSaveTaskToHistory() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t","d"));
    int taskInMemoryId = taskInMemory.getId();
    List<Task> expected = List.of(taskInMemory);

    taskManager.getTaskById(taskInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
  }

  @Test
  public void getTaskByInvalidIdShouldNotSaveTaskToHistory() {
    int taskId = 0;
    List<Task> expected = List.of();

    taskManager.getTaskById(taskId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }

  // getEpicById();
  @Test
  public void getEpicByIdShouldReturnEpicWhenIdIsValid() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e","d"));
    int epicInMemoryId = epicInMemory.getId();

    Epic returnedEpic = taskManager.getEpicById(epicInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedEpic,"Epic was not returned."),
        () -> Assertions.assertEquals(epicInMemory,returnedEpic, "Returned wrong epic. ")
    );
  }

  @Test
  public void getEpicByIdShouldReturnNullWhenIdIsNotValid() {
    Epic returnedEpic = taskManager.getEpicById(0);

    Assertions.assertNull(returnedEpic,"Epic was not returned.");
  }

  @Test
  public void getEpicByIdShouldSaveEpicToHistory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("t","d"));
    int epicInMemoryId = epicInMemory.getId();
    List<Epic> expected = List.of(epicInMemory);

    taskManager.getEpicById(epicInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
  }

  @Test
  public void getEpicByInvalidIdShouldNotSaveEpicToHistory() {
    int epicId = 0;
    List<Task> expected = List.of();

    taskManager.getEpicById(epicId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }

  // getSubtaskById();
  @Test
  public void getSubtaskByIdShouldReturnSubtaskWhenIdIsValid() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e","d"));
    int epicInMemoryId = epicInMemory.getId();
    Subtask subtaskInMemory = taskManager.addSubtask(TestDataBuilder.buildSubtask("st","d", epicInMemoryId));
    int subtaskInMemoryId = subtaskInMemory.getId();

    Subtask returnedSubtask = taskManager.getSubtaskById(subtaskInMemoryId);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(returnedSubtask,"Subtask was not returned."),
        () -> Assertions.assertEquals(subtaskInMemory,returnedSubtask, "Returned wrong subtask. ")
    );
  }

  @Test
  public void getSubtaskByIdShouldReturnNullWhenIdIsNotValid() {
    Subtask returnedSubtask = taskManager.getSubtaskById(0);

    Assertions.assertNull(returnedSubtask,"Subbtask was not returned.");
  }

  @Test
  public void getSubtaskByIdShouldSaveSubtaskToHistory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e","d"));
    int epicInMemoryId = epicInMemory.getId();
    Subtask subtaskInMemory = taskManager.addSubtask(TestDataBuilder.buildSubtask("st","d", epicInMemoryId));
    int subtaskInMemoryId = subtaskInMemory.getId();
    List<Subtask> expected = List.of(subtaskInMemory);

    taskManager.getSubtaskById(subtaskInMemoryId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, "Subtask was not added to the history.");
  }

  @Test
  public void getSubtaskByInvalidIdShouldNotSaveSubtaskToHistory() {
    int subtaskId = 0;
    List<Subtask> expected = List.of();

    taskManager.getSubtaskById(subtaskId);
    List<Task> actual = taskManager.getHistory();

    Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
  }


  // addTask();
  @Test
  void addTaskShouldSaveTaskInTaskManager() {
    Task taskToAdd = TestDataBuilder.buildTask("task", "d");
    int expectedNumberOfTasks = taskManager.getAllTasks().size() + 1;

    Task task = taskManager.addTask(taskToAdd);
    int actualNumberOfTasks = taskManager.getAllTasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getTaskById(task.getId()), "Task was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllTasks(), "Tasks are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfTasks, actualNumberOfTasks, "Incorrect number of tasks.")
    );
  }

  @Test
  void addedTaskRemainsUnchangedWhenAddedToTheTaskManager() {
    Task taskToAdd = TestDataBuilder.buildTask("task", "d");

    Task task = taskManager.addTask(taskToAdd);
    Task taskSaved = taskManager.getTaskById(task.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskSaved, "Task was not saved."),
        () -> Assertions.assertEquals(taskToAdd.getTitle(), taskSaved.getTitle(), "Title was changed."),
        () -> Assertions.assertEquals(taskToAdd.getDescription(), taskSaved.getDescription(), "Description was changed."),
        () -> Assertions.assertEquals(taskToAdd.getStatus(), taskSaved.getStatus(), "Status was changed.")
    );
  }

  @Test
  void addTaskShouldGenerateNewIdWhenSavingInTaskManagerTaskWithId() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
    int taskInMemoryId = taskInMemory.getId();
    Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);
    int expectedNumberOfTasks = taskManager.getAllTasks().size() + 1;

    Task taskAdded = taskManager.addTask(taskToAdd);
    int actualNumberOfTasks = taskManager.getAllTasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(taskInMemoryId,taskAdded.getId(),  "Id was not generated."),
        () -> Assertions.assertEquals(expectedNumberOfTasks, actualNumberOfTasks, "New task was not added.")
    );
  }

  @Test
  void addTaskWithExistedInTaskManagerIdShouldNotUpdateTaskInMemory() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
    int taskInMemoryId = taskInMemory.getId();
    Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);

    taskManager.addTask(taskToAdd);

    Assertions.assertAll(
        () -> Assertions.assertEquals(taskInMemory.getTitle(),
            taskManager.getTaskById(taskInMemoryId).getTitle(), "Task title in memory was changed."),
        () -> Assertions.assertEquals(taskInMemory.getDescription(),
            taskManager.getTaskById(taskInMemoryId).getDescription(), "Task description in memory was changed."),
        () -> Assertions.assertEquals(taskInMemory.getStatus(),
            taskManager.getTaskById(taskInMemoryId).getStatus(), "Task status in memory was changed.")
    );


  }

  // addEpic();
  @Test
  void addEpicShouldSaveEpicInTaskManager() {
    Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");
    int expectedNumberOfEpic = taskManager.getAllEpics().size() + 1;

    Epic epic = taskManager.addEpic(epicToAdd);
    int actualNumberOfEpics = taskManager.getAllEpics().size();

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getEpicById(epic.getId()), "Epic was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllEpics(), "Epics are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfEpic, actualNumberOfEpics, "Incorrect number of epics.")
    );
  }

  @Test
  void addedEpicRemainsUnchangedWhenAddedToTheTaskManager() {
    Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");

    taskManager.addEpic(epicToAdd);
    Task epicSaved = taskManager.getEpicById(epicToAdd.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(epicSaved, "Epic was not saved"),
        () -> Assertions.assertEquals(epicToAdd.getTitle(), epicSaved.getTitle(), "Title was changed."),
        () -> Assertions.assertEquals(epicToAdd.getDescription(),
            epicSaved.getDescription(), "Description was changed.")
    );
  }

  @Test
  void addEpicShouldGenerateNewIdWhenSavingInTaskManagerEpicWithId() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "e2", "d");
    int expectedNumberOfEpics = taskManager.getAllEpics().size() + 1;

    Epic epicAdded = taskManager.addEpic(epicToAdd);
    int actualNumberOfEpics = taskManager.getAllEpics().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(epicInMemoryId,epicAdded.getId(),  "Id was not generated."),
        () -> Assertions.assertEquals(expectedNumberOfEpics, actualNumberOfEpics, "New epic was not added.")
    );
  }

  @Test
  void addEpicWithExistedInTaskManagerIdShouldNotUpdateEpicInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    int epicInMemoryId = epicInMemory.getId();
    TaskStatus epicInMemoryStatus = epicInMemory.getStatus();
    Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "t2", "d");

    taskManager.addEpic(epicToAdd);

    Assertions.assertAll(
        () -> Assertions.assertEquals(epicInMemory.getTitle(), taskManager.getEpicById(epicInMemoryId).getTitle(), "Task title in memory was changed."),
        () -> Assertions.assertEquals(epicInMemory.getDescription(), taskManager.getEpicById(epicInMemoryId).getDescription(), "Task description in memory was changed."),
        () -> Assertions.assertEquals(epicInMemoryStatus, taskManager.getEpicById(epicInMemoryId).getStatus(), "Task status in memory was changed.")
    );
  }

  @Test
  void EpicThrowsExceptionWhenAddingItselfAsSubtask() {
    Task epic = TestDataBuilder.buildEpic("Epic", "d");

    taskManager.addEpic((Epic) epic);

    Assertions.assertThrows(ClassCastException.class, () -> {
      taskManager.addSubtask((Subtask) epic);
    });
  }

  // addSubtask();
  @Test
  void addSubtaskShouldSaveSubtaskInTaskManager() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d", epicInMemory.getId());
    int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

    Subtask savedSubtask = taskManager.addSubtask(subtaskToAdd);
    int actualNumberOfSubtasks = taskManager.getAllSubtasks().size();
    boolean subtaskExistInEpic = taskManager.getSubtasksByEpicId(savedSubtask.getEpicId()).contains(savedSubtask);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(savedSubtask, "Subtask was not found."),
        () -> Assertions.assertNotNull(taskManager.getAllSubtasks(), "Subtasks are not returned."),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks, actualNumberOfSubtasks, "Incorrect number of tasks."),
        () -> Assertions.assertTrue(subtaskExistInEpic, "Subtask was not saved inside epic.")
    );
  }

  @Test
  void addedSubtaskRemainsUnchangedWhenAddedToTheTaskManager() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d",epicInMemory.getId());

    taskManager.addSubtask(subtaskToAdd);
    Subtask savedSubtask = taskManager.getSubtaskById(subtaskToAdd.getId());

    Assertions.assertAll(
        () -> Assertions.assertNotNull(savedSubtask, "Subtask was not saved."),
        () -> Assertions.assertEquals(subtaskToAdd.getTitle(), savedSubtask.getTitle(), "Title was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getDescription(), savedSubtask.getDescription(), "Description was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getStatus(), savedSubtask.getStatus(), "Status was changed."),
        () -> Assertions.assertEquals(subtaskToAdd.getEpicId(),savedSubtask.getEpicId(),"Corresponding epic was changed.")
    );
  }

  @Test
  void addSubtaskShouldGenerateNewIdWhenSavingInTaskManagerSubtaskWithId() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskInMemory = taskManager.addSubtask(TestDataBuilder.buildSubtask("t1", "d",epicInMemory.getId()));
    int subtaskInMemoryId = subtaskInMemory.getId();
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "t2", "d", subtaskInMemory.getEpicId());
    int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

    Subtask subtaskAdded = taskManager.addSubtask(subtaskToAdd);
    int actualNumberOfTasks = taskManager.getAllSubtasks().size();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(subtaskInMemoryId,subtaskAdded.getId(),"Id was not generated"),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks, actualNumberOfTasks, "New subtask was not added.")
    );
  }

  @Test
  void addSubtaskWithExistedInTaskManagerIdShouldNotUpdateSubtaskInMemory() {
    Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
    Subtask subtaskInMemory = taskManager.addSubtask(TestDataBuilder.buildSubtask("st1", "d",epicInMemory.getId()));
    int subtaskInMemoryId = subtaskInMemory.getId();
    int expectedNumberSubtasksInEpic = taskManager.getSubtasksByEpicId(epicInMemory.getId()).size() + 1;
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "st2", "d", epicInMemory.getId());

    Subtask subtaskAdded = taskManager.addSubtask(subtaskToAdd);

    Assertions.assertAll(
        () -> Assertions.assertNotNull(taskManager.getSubtaskById(subtaskAdded.getId()),"Subtask was not found."),
        () -> Assertions.assertEquals(subtaskInMemory.getTitle(), taskManager.getSubtaskById(subtaskInMemoryId).getTitle(), "Subtask title in memory was changed."),
        () -> Assertions.assertEquals(subtaskInMemory.getDescription(), taskManager.getSubtaskById(subtaskInMemoryId).getDescription(), "Subtask description in memory was changed."),
        () -> Assertions.assertEquals(subtaskInMemory.getStatus(), taskManager.getSubtaskById(subtaskInMemoryId).getStatus(), "Subtask status in memory was changed."),
        () -> Assertions.assertEquals(expectedNumberSubtasksInEpic, taskManager.getSubtasksByEpicId(epicInMemory.getId()).size(), "Subtask wasn't found in epic.")
    );
  }

  @Test
  void addSubtaskShouldNotAddSubtaskWithIncorrectEpicId() {
    Subtask subtaskToAdd = TestDataBuilder.buildSubtask("st1","d",1);
    int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size();

    Subtask subtaskAdded = taskManager.addSubtask(subtaskToAdd);

    Assertions.assertAll(
        () -> Assertions.assertNull(subtaskAdded,"Subtask added."),
        () -> Assertions.assertEquals(expectedNumberOfSubtasks,taskManager.getAllSubtasks().size(),"Subtask added.")
    );
  }

  @Test
  void subtaskShouldNotBeAbleBecomeItsOwnSubtask() {
    Epic epicInMemory =taskManager.addEpic( TestDataBuilder.buildEpic("Epic", "d"));
    Subtask subtaskInMemory = taskManager.addSubtask(TestDataBuilder.buildSubtask("Sb", "D", epicInMemory.getId()));
    Subtask subtaskToAdd = TestDataBuilder.buildCopySubtask(subtaskInMemory);
    subtaskToAdd.setEpicId(subtaskInMemory.getId());

    taskManager.addSubtask(subtaskToAdd);
    boolean existsSubtaskAsSubtaskItself =
        taskManager.getAllSubtasks().stream().anyMatch(sb -> sb.getEpicId() == sb.getId());

    Assertions.assertAll(
        () -> Assertions.assertNull(taskManager.getEpicById(subtaskInMemory.getId())),
        () -> Assertions.assertFalse(existsSubtaskAsSubtaskItself)
    );
  }

  // getHistory();
  @Test
  public void tasksInHistoryShouldKeepTheirStateAfterUpdatingThemInTaskManager() {
    Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t","d"));
    int taskInMemoryId = taskInMemory.getId();
    taskManager.getTaskById(taskInMemoryId);
    Task expected = taskManager.getHistory().get(taskManager.getHistory().size() - 1);

    taskInMemory.setStatus(TaskStatus.DONE);
    taskManager.updateTask(taskInMemory);
    Task actual = taskManager.getHistory().get(taskManager.getHistory().size() - 1);

    Assertions.assertEquals(expected, actual, "Id is different");
    Assertions.assertEquals(expected.getTitle(), actual.getTitle(), "Id is different");
    Assertions.assertEquals(expected.getDescription(), actual.getDescription(), "Description is different");
    Assertions.assertEquals(expected.getStatus(), actual.getStatus(), "Status is different");

  }



}