package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.service.Managers;
import ru.yandex.practicum.tasktracker.service.TaskManager;

public abstract class HttpTaskManagerTest {

  protected String BASE_URL;
  protected String BASE_ENDPOINT;

  protected TaskManager manager;
  protected HttpTaskServer taskServer;
  protected Gson gson;

  @BeforeEach
  void setUp() throws IOException {
    manager = Managers.getDefault();
    gson = Managers.getGson();
    taskServer = new HttpTaskServer(manager);
    taskServer.start();
  }

  @AfterEach
  void tearDown() {
    taskServer.stop();
  }

  /* GET */

  @Test
  @DisplayName("GET should return an empty list from a new TaskManager")
  public abstract void getTasksReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException;//{
//    /* Given */
//    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 200);
//    assertResponseBodyIsJsonArray(response);
//    final List<Task> actualTasks = parseTasksFromResponse(response);
//    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty list of Tasks");
//  }

  @Test
  @DisplayName("GET should return list with  tasks from TaskManager")
  public abstract void getTasksReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException;//{
//    /* Given */
//    TestDataBuilder.addTaskDataToTheTaskManager(manager);
//    final List<Task> expected = manager.getTasks();
//    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 200);
//    assertResponseBodyIsJsonArray(response);
//    final List<Task> actualTasks = parseTasksFromResponse(response);
//    Assertions.assertFalse(actualTasks.isEmpty(), "Should return an empty list of Tasks.");
//    Assertions.assertIterableEquals(expected, actualTasks, "Should return same List of tasks.");
//
//  }

  @Test
  @DisplayName("GET with /{id} should return task by valid ID from TaskManager")
  public abstract void getTasksByIdReturnsAnValidTaskFromManager()
      throws IOException, InterruptedException;//{
//    /* Given */
//    final Task expectedTask = manager.addTask(
//        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
//            LocalDateTime.now()));
//    final int id = expectedTask.getId();
//    final HttpRequest request = createGetRequest(BASE_ENDPOINT + "/" + id);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 200);
//    assertResponseBodyIsJsonObject(response);
//    final Task actualTask = parseTaskFromResponse(response);
//    Assertions.assertEquals(expectedTask, actualTask, "Returned task is not correct.");
//
//  }

  /* POST */
  @Test
  @DisplayName("POST  should add task to the  TaskManager with start time defined")
  public abstract void postTasksShouldAddTaskToTheTaskManager()
      throws IOException, InterruptedException;//{
//    /* Given */
//    final Task expectedTask = TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW,
//        Duration.ofMinutes(15), LocalDateTime.now());
//    int expectedNumberOfTasks = manager.getTasks().size() + 1;
//    final HttpRequest request = createPostRequest(BASE_ENDPOINT, expectedTask);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 201);
//    final Task actual = gson.fromJson(response.body(), Task.class);
//    assertTaskFieldsAreSame(expectedTask, actual);
//    Assertions.assertEquals(expectedNumberOfTasks, manager.getTasks().size(),
//        "The number of tasks should have increased by 1.");
//  }

  @Test
  @DisplayName("POST  with /{id} should update existed in the TaskManager task with new start time")
  public abstract void postTasksByIdShouldUpdateTaskWithNewStartTime()
      throws IOException, InterruptedException;//{
//    /* Given */
//    final Task existedTask = manager.addTask(
//        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
//            LocalDateTime.now()));
//    final int id = existedTask.getId();
//    final Task expected = existedTask.copy();
//    expected.setStartTime(existedTask.getStartTime().plusYears(1));
//    final HttpRequest request = createPostRequest(BASE_ENDPOINT + "/" + id, expected);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 201);
//    final Task actual = manager.getTaskById(id);
//    assertTaskFieldsAreSame(expected, actual);
//    Assertions.assertEquals(expected, actual, "Id should be same.");
//  }

  @Test
  @DisplayName("DELETE  with /{id} should delete existed in the TaskManager Task")
  public abstract void deleteTasksByIdShouldDeleteTaskFromTaskManager()
      throws IOException, InterruptedException;//{
//    /* Given */
//    final Task existedTask = manager.addTask(
//        TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW, Duration.ofMinutes(15),
//            LocalDateTime.now()));
//    final int id = existedTask.getId();
//    final HttpRequest request = createDeleteRequest(BASE_ENDPOINT + "/" + id);
//    /* When */
//    final HttpResponse<String> response = sendRequest(request);
//    /* Then */
//    assertStatusCode(response, 200);
//    final TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
//        () -> {
//          manager.getTaskById(id);
//        });
//  }

  protected HttpRequest createGetRequest(String uri) {
    return HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(uri))
        .build();
  }

  protected HttpRequest createPostRequest(String uri, Task body) {
    return HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body, body.getClass())))
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .build();
  }

  protected HttpRequest createDeleteRequest(String uri) {
    return HttpRequest.newBuilder()
        .DELETE()
        .uri(URI.create(uri))
        .build();
  }

  protected HttpResponse<String> sendRequest(HttpRequest request)
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  protected void assertStatusCode(HttpResponse<String> response, int expectedStatusCode) {
    Assertions.assertEquals(expectedStatusCode, response.statusCode(),
        "HTTP status code should be " + expectedStatusCode + ".");
  }

  protected void assertResponseBodyIsJsonArray(HttpResponse<String> response) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    Assertions.assertTrue(jsonElement.isJsonArray(), "Response body should be a JSON array.");
  }

  protected void assertTaskFieldsAreSame(Task expected, Task actual) {
    Assertions.assertAll(
        () -> Assertions.assertEquals(expected.getTitle(), actual.getTitle(),
            "The added task's title should match the expected title."),
        () -> Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
            "The added task's description should match the expected description."),
        () -> Assertions.assertEquals(expected.getStatus(), actual.getStatus(),
            "The added task's status should match the expected status."),
        () -> Assertions.assertEquals(expected.getDuration(), actual.getDuration(),
            "The added task's duration should match the expected duration."),
        () -> Assertions.assertEquals(expected.getStartTime(), actual.getStartTime(),
            "The added task's start time should match the expected start time."),
        () -> {
          if (TaskType.EPIC.equals(expected.getType())) {
            Assertions.assertIterableEquals(((Epic) expected).getSubtasks(),
                ((Epic) actual).getSubtasks(),
                "The added task's should have same subtasks.");
          }
        },
        () -> {
          if (TaskType.SUBTASK.equals(expected.getType())) {
            Assertions.assertEquals(((Subtask) expected).getEpicId(),
                ((Subtask) actual).getEpicId(),
                "The added task's should have same epic.");
          }
        }
    );

  }

  protected void assertResponseBodyIsJsonObject(HttpResponse<String> response) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    Assertions.assertTrue(jsonElement.isJsonObject(), "Response body should be a JSON object.");
  }

  protected List<Task> parseTasksFromResponse(HttpResponse<String> response) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    return gson.fromJson(jsonElement, new TypeToken<List<Task>>() {
    }.getType());
  }

  protected Task parseTaskFromResponse(HttpResponse<String> response) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    return gson.fromJson(jsonElement, Task.class);
  }
}



