package ru.yandex.practicum.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.service.TaskManager;

class EpicTest {

  private TaskManager taskManager;
  private Epic epic;
  private Subtask subtask1;
  private Subtask subtask2;
  private static final LocalDateTime BASE_TEST_TIME = LocalDateTime.parse(
      "2024-05-21T01:00:00.00000");

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
  @DisplayName("getStatus() - when epic does not have subtasks.")
  void getStatusShouldReturnStatusNewWhenDoesNotHaveSubtasks() {
    TaskStatus actualStatus = epic.getStatus();

    Assertions.assertEquals(TaskStatus.NEW, actualStatus,
        "Incorrect status when Epic without subtasks.");
  }

  @Test
  @DisplayName("getStatus() - when all subtasks has status NEW.")
  void getStatusShouldReturnStatusNewWhenAllSubtasksHaveStatusNew() {
    addNewSubtasksToTheEpic();

    TaskStatus actualStatus = epic.getStatus();

    Assertions.assertEquals(TaskStatus.NEW, actualStatus,
        "Incorrect status when all subtasks are new.");
  }

  @Test
  @DisplayName("getStatus() - when subtasks has statuses NEW and IN_PROGRESS.")
  void getStatusShouldReturnInProgressWhenAllSubtasksHasStatusInProgress() {
    addNewSubtasksToTheEpic();
    updateSubtasksWithStatus(TaskStatus.IN_PROGRESS);

    TaskStatus actualStatus = taskManager.getEpicById(epic.getId()).getStatus();

    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus,
        "Incorrect status when all subtasks are in progress.");
  }

  @Test
  @DisplayName("getStatus() - when subtasks have statuses NEW and DONE.")
  void getStatusShouldReturnStatusInProgressWhenOnlyOneSubtaskHasStatusDone() {
    addNewSubtasksToTheEpic();
    subtask2.setStatus(TaskStatus.DONE);
    taskManager.updateSubtask(subtask2);

    TaskStatus actualStatus = taskManager.getEpicById(epic.getId()).getStatus();

    Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualStatus,
        "Incorrect status when only one of the subtasks has status Done.");
  }

  @Test
  @DisplayName("getStatus() - when all subtasks has status DONE.")
  void getStatusShouldReturnDoneWhenAllSubtasksHasStatusDone() {
    TaskStatus epicStatusAtStart = taskManager.getEpicById(epic.getId()).getStatus();
    addNewSubtasksToTheEpic();
    updateSubtasksWithStatus(TaskStatus.DONE);
    TaskStatus actualStatus = taskManager.getEpicById(epic.getId()).getStatus();
    Assertions.assertNotEquals(epicStatusAtStart, actualStatus,
        "Status should be other than it was before updating.");
    Assertions.assertEquals(TaskStatus.DONE, actualStatus,
        "Incorrect status when all subtasks are done.");
  }

  @ParameterizedTest
  @DisplayName("duration, startTime, and endTime calculations.")
  @MethodSource("provideDataForTimeCalculation")
  void getDurationCalculatesDurationStartTimeAndEndTimeBasedOnSubtasksTotalDuration(
      final long durationToExpect, final LocalDateTime startTime, final LocalDateTime endTime,
      final int choiceNumber, final List<String> message) {
    final Duration expectedDuration = Duration.ofMinutes(durationToExpect);
    final LocalDateTime expectedStartTime = startTime;
    final LocalDateTime expectedEndTime = endTime;
    givenData(choiceNumber);

    final Duration actuaDuration = taskManager.getEpicById(epic.getId()).getDuration();
    final LocalDateTime actualStartTime = taskManager.getEpicById(epic.getId()).getStartTime();
    final LocalDateTime actualEndTime = taskManager.getEpicById(epic.getId()).getEndTime();

    Assertions.assertAll(
        () -> Assertions.assertEquals(expectedDuration, actuaDuration,
            message.get(0)),
        () -> Assertions.assertEquals(expectedStartTime, actualStartTime,
            message.get(1)),
        () -> Assertions.assertEquals(expectedEndTime, actualEndTime,
            message.get(2))
    );
  }

  static Stream<Arguments> provideDataForTimeCalculation() {
    final Map<Integer, List<String>> messages = new HashMap<>();
    messages.put(1, List.of("Epic without subtasks - duration should be zero.",
        "Epic without subtasks - start time should be null.",
        "Epic without subtasks - end time should be null."));

    messages.put(2,
        List.of("Epic with subtasks with start time was not set - duration should be zero.",
            "Epic with subtasks with start time was not set - start time should be null.",
            "Epic with subtasks with start time was not set - end time should be null."));

    messages.put(3, List.of(
        "Epic with subtasks where just one has start time set - duration should be calculated.",
        "Epic with subtasks where just one has start time set - start time should be calculated.",
        "Epic with subtasks where just one has start time set - end time should be calculated."));

    messages.put(4,
        List.of("Epic with subtasks with all has start time set - duration should be calculated.",
            "Epic with subtasks with all has start time set - start time should be calculated.",
            "Epic with subtasks with all has start time set - end time should be calculated."));

    return Stream.of(
        Arguments.of(0, null, null, 1, messages.get(1)),
        Arguments.of(0, null, null, 2, messages.get(2)),
        Arguments.of(20, BASE_TEST_TIME, BASE_TEST_TIME.plusMinutes(20), 3, messages.get(3)),
        Arguments.of(45, BASE_TEST_TIME, BASE_TEST_TIME.plusMinutes(50), 4, messages.get(4)));
  }

  void givenData(final int choiceNumber) {
    switch (choiceNumber) {
      case 1 -> {
      }
      case 2 -> addNewSubtasksToTheEpic();
      case 3 -> {
        addNewSubtasksToTheEpic();
        updateSubtaskWithTimeValues(subtask2, BASE_TEST_TIME, 20);
      }
      case 4 -> {
        addNewSubtasksToTheEpic();
        updateSubtaskWithTimeValues(subtask1, BASE_TEST_TIME, 25);
        updateSubtaskWithTimeValues(subtask2, BASE_TEST_TIME.plusMinutes(30), 20);
      }
    }
  }

  void updateSubtaskWithTimeValues(final Subtask subtask, final LocalDateTime startTime,
      final long duration) {
    subtask.setStartTime(startTime);
    subtask.setDuration(Duration.ofMinutes(duration));
    taskManager.updateSubtask(subtask);
  }

  void setUpEpicWithoutSubtasksInTaskManager() {
    epic = TestDataBuilder.buildEpic("Epic", "You know what you need.");
    taskManager.addEpic(epic);
  }

  void addNewSubtasksToTheEpic() {
    subtask1 = TestDataBuilder.buildSubtask("Step1", "Start", epic.getId());
    subtask2 = TestDataBuilder.buildSubtask("Step2", "Finish", epic.getId());
    taskManager.addSubtask(subtask1);
    taskManager.addSubtask(subtask2);
  }

  void updateSubtasksWithStatus(final TaskStatus status) {
    subtask1.setStatus(status);
    subtask2.setStatus(status);
    taskManager.updateSubtask(subtask1);
    taskManager.updateSubtask(subtask2);
  }

}