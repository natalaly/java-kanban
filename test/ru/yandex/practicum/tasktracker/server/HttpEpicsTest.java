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
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

public class HttpEpicsTest extends HttpTaskManagerTest{

  @BeforeEach
  @Override
  void setUp() throws IOException {
    BASE_URL = "http://localhost:8080";
    BASE_ENDPOINT = BASE_URL + "/epics";
    super.setUp();
  }

  /* GET */

  @Test
  @Override
  @DisplayName("GET /epics should return an empty list from a new TaskManager")
  public void getReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Epic>> listTypeToken = new TypeToken<List<Epic>>(){};
    final List<Epic> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty list of Tasks");
  }

  @Test
  @Override
  @DisplayName("GET /epics should return list with epics from TaskManager")
  public void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    TestDataBuilder.addTaskDataToTheTaskManager(manager);
    final List<Epic> expected = manager.getEpics();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Epic>> listTypeToken = new TypeToken<List<Epic>>(){};
    final List<Epic> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertFalse(actualTasks.isEmpty(), "Should not return an empty list of Epics.");
    Assertions.assertIterableEquals(expected, actualTasks, "Should return same List of Epics.");
  }

  @Test
  @Override
  @DisplayName("GET /epics/{id} should return Epic by valid ID from TaskManager")
  public void getWithIdReturnsAnValidTaskObjectFromManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic expected = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d e"));
    final int id = expected.getId();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonObject(response);
    TypeToken<Epic> epicTypeToken = new TypeToken<Epic>() {};
    Epic actual = parseTaskFromResponse(response, epicTypeToken);
    Assertions.assertEquals(expected, actual, "Returned task is not correct.");

  }

  /* POST */
  @Test
  @Override
  @DisplayName("POST /epics should add Epic to the TaskManager ")
  public void postShouldAddTaskObjectToTheTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic expected = TestDataBuilder.buildEpic(1, "epic", "d e");
    int expectedNumberOfEpics = manager.getEpics().size() + 1;
    final HttpRequest request = createPostRequest(BASE_ENDPOINT, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 201);
    TypeToken<Epic> epicTypeToken = new TypeToken<Epic>() {};
    final Epic actual = gson.fromJson(response.body(), epicTypeToken.getType());
    assertTaskFieldsAreSame(expected, actual);
    Assertions.assertEquals(expectedNumberOfEpics, manager.getEpics().size(),
        "The number of tasks should have increased by 1.");
  }

  @Test
  @Override
  @DisplayName("POST /epics/{id} does not update Epic ")
  public void postWithIdCallsUpdateTaskObjectWithNewStartTime()
      throws IOException, InterruptedException {
    /* Given */
    final Epic expected  = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d"));
    final int id = expected.getId();
    final Epic epicForUpdating = expected.copy();
    epicForUpdating.setDescription("Updated");
    final HttpRequest request = createPostRequest(BASE_ENDPOINT + "/" + id, epicForUpdating);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 400);
    assertTaskFieldsAreSame(expected,manager.getEpicById(id));
  }

  @Test
  @Override
  @DisplayName("DELETE /epics/{id} should delete existed in the TaskManager Task")
  public void deleteWithIdShouldDeleteTaskObjectFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic existedEpic = manager.addEpic(
        TestDataBuilder.buildEpic(1, "epic", "d e"));
    final int id = existedEpic.getId();
    final HttpRequest request = createDeleteRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    final TaskNotFoundException actualException = Assertions.assertThrows(TaskNotFoundException.class,
        () -> {
          manager.getEpicById(id);
        });
  }


}



