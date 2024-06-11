package ru.yandex.practicum.tasktracker.server;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

public class HttpHistoryTest extends HttpTaskManagerTest {

  @BeforeEach
  @Override
  void setUp() throws IOException {
    BASE_URL = "http://localhost:8080";
    BASE_ENDPOINT = BASE_URL + "/history";
    super.setUp();
  }

  /* GET */

  @Test
  @Override
  @DisplayName("GET /history should return an empty list from a new TaskManager")
  public void getReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Task>> listTypeToken = new TypeToken<List<Task>>() {
    };
    final List<Task> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty list of task objects.");
  }

  @Test
  @Override
  @DisplayName("GET /history should return list with tasks from TaskManager")
  public void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    buildHistoryInTaskManager();
    final List<Task> expected = manager.getHistory();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    final List<Task> actual = parseAllTaskTypesFromResponse(response);
    Assertions.assertFalse(actual.isEmpty(), "Should not return an empty list of task objects.");
    Assertions.assertIterableEquals(expected, actual, "Should return same list of task objects.");
  }



  @Test
  @Override
  @DisplayName("GET /history/{id} should return 400 -wrong path")
  public void getWithIdReturnsAnValidTaskObjectFromManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic expected = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d e"));
    final int id = expected.getId();
    manager.getEpicById(id);
    final HttpRequest request = createGetRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 400);
    Assertions.assertTrue(response.body().isEmpty());
  }

  /* POST */
  @Test
  @Override
  @DisplayName("POST /history should return 405 Unsupported method")
  public void postShouldAddTaskObjectToTheTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic expected = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d e"));
    final HttpRequest request = createPostRequest(BASE_ENDPOINT, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 405);
    Assertions.assertTrue(response.body().isEmpty());
  }

  @Test
  @Override
  @DisplayName("POST /history/{id} returns 405 Unsupported method")
  public void postWithIdCallsUpdateTaskObjectWithNewStartTime()
      throws IOException, InterruptedException {
    /* Given */
    final Epic existed = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d"));
    final int id = existed.getId();
    final Epic expected = existed.copy();
    expected.setTitle("Updated");
    final HttpRequest request = createPostRequest(BASE_ENDPOINT + "/" + id, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 405);
    Assertions.assertTrue(response.body().isEmpty());
  }

  @Test
  @Override
  @DisplayName("DELETE /history/{id} returns 405 Unsupported method")
  public void deleteWithIdShouldDeleteTaskObjectFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic existed = manager.addEpic(
        TestDataBuilder.buildEpic(1, "epic", "d e"));
    final int id = existed.getId();
    final HttpRequest request = createDeleteRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 405);
    Assertions.assertTrue(response.body().isEmpty());
  }

  private void buildHistoryInTaskManager() {
    TestDataBuilder.addTaskDataToTheTaskManager(manager);
    manager.getTasks().stream().map(Task::getId).forEach(manager::getTaskById);
    manager.getEpics().stream().map(Epic::getId).forEach(manager::getEpicById);
    manager.getSubtasks().stream().map(Subtask::getId).forEach(manager::getSubtaskById);
  }

}



