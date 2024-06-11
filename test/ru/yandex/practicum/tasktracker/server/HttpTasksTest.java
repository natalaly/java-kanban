package ru.yandex.practicum.tasktracker.server;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

public class HttpTasksTest extends HttpTaskManagerTest{

  @BeforeEach
  @Override
  void setUp() throws IOException {
    BASE_URL = "http://localhost:8080";
    BASE_ENDPOINT = BASE_URL + "/tasks";
    super.setUp();
  }

  /* GET */

  @Test
  @Override
  @DisplayName("GET /tasks should return an empty list from a new TaskManager")
  public void getReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Task>> listTypeToken = new TypeToken<List<Task>>(){};
    final List<Task> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty list of Tasks");
  }

  @Test
  @Override
  @DisplayName("GET /tasks should return list with  from TaskManager")
  public void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    TestDataBuilder.addTaskDataToTheTaskManager(manager);
    final List<Task> expected = manager.getTasks();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Task>> listTypeToken = new TypeToken<List<Task>>(){};
    final List<Task> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertFalse(actualTasks.isEmpty(), "Should not return an empty list of Tasks.");
    Assertions.assertIterableEquals(expected, actualTasks, "Should return same List of tasks.");

  }

  @Test
  @Override
  @DisplayName("GET /tasks/{id} should return Task by valid ID from TaskManager")
  public void getWithIdReturnsAnValidTaskObjectFromManager()
      throws IOException, InterruptedException {
    /* Given */
    final Task expectedTask = manager.addTask(
        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
            LocalDateTime.now()));
    final int id = expectedTask.getId();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonObject(response);
    TypeToken<Task> taskTypeToken = new TypeToken<Task>() {};
    final Task actualTask = parseTaskFromResponse(response, taskTypeToken  );
    Assertions.assertEquals(expectedTask, actualTask, "Returned task is not correct.");

  }

  /* POST */
  @Test
  @Override
  @DisplayName("POST /tasks should add Task to the TaskManager with start time defined")
  public void postShouldAddTaskObjectToTheTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Task expectedTask = TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW,
        Duration.ofMinutes(15), LocalDateTime.now());
    int expectedNumberOfTasks = manager.getTasks().size() + 1;
    final HttpRequest request = createPostRequest(BASE_ENDPOINT, expectedTask);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 201);
    final Task actual = gson.fromJson(response.body(), Task.class);
    assertTaskFieldsAreSame(expectedTask, actual);
    Assertions.assertEquals(expectedNumberOfTasks, manager.getTasks().size(),
        "The number of tasks should have increased by 1.");
  }

  @Test
  @Override
  @DisplayName("POST /tasks/{id} should update existed in the TaskManager Task with new start time")
  public void postWithIdCallsUpdateTaskObjectWithNewStartTime()
      throws IOException, InterruptedException {
    /* Given */
    final Task existedTask = manager.addTask(
        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
            LocalDateTime.now()));
    final int id = existedTask.getId();
    final Task expected = existedTask.copy();
    expected.setStartTime(existedTask.getStartTime().plusYears(1));
    final HttpRequest request = createPostRequest(BASE_ENDPOINT + "/" + id, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 201);
    final Task actual = manager.getTaskById(id);
    assertTaskFieldsAreSame(expected, actual);
    Assertions.assertEquals(expected, actual, "Id should be same.");
  }

  @Test
  @Override
  @DisplayName("DELETE /tasks/{id} should delete existed in the TaskManager Task")
  public void deleteWithIdShouldDeleteTaskObjectFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Task existedTask = manager.addTask(
        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
            LocalDateTime.now()));
    final int id = existedTask.getId();
    final HttpRequest request = createDeleteRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    final TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> {
          manager.getTaskById(id);
        });
  }


}



