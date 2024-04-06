package main.java.ru.yandex.practicum.tasktracker.model;

import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;
import org.junit.jupiter.api.*;
//TODO delete commented code

class EpicTest {
  private static TaskManager taskManager;
  private static Epic epic;
  private static Subtask subtask1;
  private static Subtask subtask2;


  @BeforeEach
    // set epic in TM, create subtask1 and subtask2 for epic, but outside TM for the start
  void setUp() {
    taskManager = TestDataBuilder.buildTaskManager();
    epic = TestDataBuilder.buildEpic("Epic", "You know what you need.");
    taskManager.addEpic(epic);
  }

  @AfterEach
  void tearDown() {
    taskManager = null;
    epic = null;
    subtask1 = null;
    subtask2 = null;
  }

  @Test
  void getStatusShouldReturnStatusNewWhenDoesNotHaveSubtasks() {
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.NEW, actualStatus, "Incorrect status when Epic without subtasks.");
  }

  @Test
  void getStatusShouldReturnStatusNewWhenAllSubtasksHaveStatusNew() {
    buildSubtask();
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.NEW, actualStatus, "Incorrect status when all subtasks are new.");
  }

  @Test
  void getStatusShouldReturnInProgressWhenAllSubtasksHasStatusInProgress() {
    buildSubtask();
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    subtask1.setStatus(TaskStatus.IN_PROGRESS);
    subtask2.setStatus(TaskStatus.IN_PROGRESS);
    taskManager.updateTask(subtask1);
    taskManager.updateTask(subtask2);
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus, "Incorrect status when all subtasks are in progress.");
  }

  @Test
  void getStatusShouldReturnStatusInProgressWhenOnlyOneSubtaskHasStatusDone() {
    buildSubtask();
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    subtask2.setStatus(TaskStatus.DONE);
    taskManager.updateTask(subtask2);
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus, "Incorrect status when only one of the subtasks has status Done.");
  }

  @Test
  void getStatusShouldReturnDoneWhenAllSubtasksHasStatusDone() {
    buildSubtask();
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
    subtask1.setStatus(TaskStatus.DONE);
    subtask2.setStatus(TaskStatus.DONE);
    taskManager.updateTask(subtask1);
    taskManager.updateTask(subtask2);
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.DONE, actualStatus, "Incorrect status when all subtasks are done.");
  }


  static void buildSubtask() {
    subtask1 = TestDataBuilder.buildSubtask("Step1", "Start", epic.getId());
    subtask2 = TestDataBuilder.buildSubtask("Step2", "Finish", epic.getId());
  }

}