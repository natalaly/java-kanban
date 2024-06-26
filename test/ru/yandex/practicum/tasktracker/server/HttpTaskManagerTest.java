package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
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
  public abstract void getReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException;

  @Test
  @DisplayName("GET should return list with  tasks from TaskManager")
  public abstract void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException;

  @Test
  @DisplayName("GET with /{id} should return task by valid ID from TaskManager")
  public abstract void getWithIdReturnsAnValidTaskObjectFromManager()
      throws IOException, InterruptedException;

  /* POST */
  @Test
  @DisplayName("POST should add task to the  TaskManager with start time defined")
  public abstract void postShouldAddTaskObjectToTheTaskManager()
      throws IOException, InterruptedException;


  @Test
  @DisplayName("POST  with /{id} should update existed in the TaskManager task with new start time")
  public abstract void postWithIdCallsUpdateTaskObjectWithNewStartTime()
      throws IOException, InterruptedException;

  /* DELETE  */
  @Test
  @DisplayName("DELETE  with /{id} should delete existed in the TaskManager Task")
  public abstract void deleteWithIdShouldDeleteTaskObjectFromTaskManager()
      throws IOException, InterruptedException;


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

  protected <T extends List<?>> T parseTasksFromResponse(HttpResponse<String> response,
      TypeToken<T> typeToken) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    return gson.fromJson(jsonElement, typeToken.getType());
  }

  protected <T extends Task> T parseTaskFromResponse(HttpResponse<String> response,
      TypeToken<T> typeToken) {
    JsonElement jsonElement = JsonParser.parseString(response.body());
    return gson.fromJson(jsonElement, typeToken.getType());
  }

  protected List<Task> parseAllTaskTypesFromResponse(HttpResponse<String> response) {
    System.out.println("Response:" + response);
    JsonElement jsonElement = JsonParser.parseString(response.body());
    List<Task> result = new ArrayList<>();
    if (jsonElement.isJsonArray()) {
      JsonArray jsonArray = jsonElement.getAsJsonArray();
      jsonArray.forEach(eachElement -> {
        Task task = parseTaskFromJson(eachElement);
        if (task != null) {
          result.add(task);
        }
      });
    }
    return result;
  }

  protected Task parseTaskFromJson(JsonElement element) {
    if (!element.isJsonObject()) {
      return null;
    }
    final JsonObject jsonObject = element.getAsJsonObject();
    if (jsonObject.has("epicId")) {
      return gson.fromJson(element, Subtask.class);
    } else if (jsonObject.has("subtasks")) {
      return gson.fromJson(element, Epic.class);
    } else {
      return gson.fromJson(element, Task.class);
    }
  }
}



