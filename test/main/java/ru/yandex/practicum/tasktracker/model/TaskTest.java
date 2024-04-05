package main.java.ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.*;
//TODO delete commented code

class TaskTest {

  @Test
  void verifyTasksAreEqualsWhenIdsAreEquals() {
    Task task1 = TestDataBuilder.buildTaskWithId(1, "Task1", "Description1", TaskStatus.NEW);
    Task task2 = TestDataBuilder.buildTaskWithId(task1.getId(), "a", "s", TaskStatus.IN_PROGRESS);
    Assertions.assertEquals(task1, task2, "Tasks with the same ID are expected to be equal, but they are not.");
  }

  @Test
  void verifyTasksAreNotEqualsWhenIdsAreDifferentAndFieldsAreSame() {
    Task task1 = TestDataBuilder.buildTaskWithId(1, "Task1", "Description1", TaskStatus.NEW);
    Task task2 = TestDataBuilder.buildTaskWithId(3, task1.getTitle(), task1.getDescription(), task1.getStatus());
    Assertions.assertNotEquals(task1, task2, "Tasks with different ID are expected to be not equal, but they are.");
  }

//  private Task buildTask(int id, String title, String description, TaskStatus status) {
//    Task task = new Task();
//    task.setId(id);
//    task.setTitle(title);
//    task.setDescription(description);
//    task.setStatus(status);
//    return task;
//  }

}