package ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.service.TaskManager;

class EpicTest {

  private TaskManager taskManager;
  private Epic epic;
  private Subtask subtask1;
  private Subtask subtask2;

  @BeforeEach
  void setUp() {
    taskManager = TestDataBuilder.buildTaskManager();
    setUpEpicWithoutSubtasksInTaskManager();
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

    Assertions.assertEquals(TaskStatus.NEW, actualStatus,
        "Incorrect status when Epic without subtasks.");
  }

  @Test
  void getStatusShouldReturnStatusNewWhenAllSubtasksHaveStatusNew() {
    addNewSubtasksToEpic();

    TaskStatus actualStatus = epic.getStatus();

    Assertions.assertEquals(TaskStatus.NEW, actualStatus,
        "Incorrect status when all subtasks are new.");
  }

  @Test
  void getStatusShouldReturnInProgressWhenAllSubtasksHasStatusInProgress() {
    addNewSubtasksToEpic();
    updateSubtasksWithStatus(TaskStatus.IN_PROGRESS);

    TaskStatus actualStatus = epic.getStatus();

    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus,
        "Incorrect status when all subtasks are in progress.");
  }

  @Test
  void getStatusShouldReturnStatusInProgressWhenOnlyOneSubtaskHasStatusDone() {
    addNewSubtasksToEpic();
    subtask2.setStatus(TaskStatus.DONE);
    taskManager.updateTask(subtask2);

    TaskStatus actualStatus = epic.getStatus();

    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus,
        "Incorrect status when only one of the subtasks has status Done.");
  }

  @Test
  void getStatusShouldReturnDoneWhenAllSubtasksHasStatusDone() {
    addNewSubtasksToEpic();
    updateSubtasksWithStatus(TaskStatus.DONE);
    TaskStatus actualStatus = epic.getStatus();
    Assertions.assertEquals(TaskStatus.DONE, actualStatus,
        "Incorrect status when all subtasks are done.");
  }

  void setUpEpicWithoutSubtasksInTaskManager() {
    epic = TestDataBuilder.buildEpic("Epic", "You know what you need.");
    taskManager.addEpic(epic);
  }

  void addNewSubtasksToEpic() {
    subtask1 = TestDataBuilder.buildSubtask("Step1", "Start", epic.getId());
    subtask2 = TestDataBuilder.buildSubtask("Step2", "Finish", epic.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
  }

  void updateSubtasksWithStatus(TaskStatus status) {
    subtask1.setStatus(status);
    subtask2.setStatus(status);
    taskManager.updateTask(subtask1);
    taskManager.updateTask(subtask2);
  }

}