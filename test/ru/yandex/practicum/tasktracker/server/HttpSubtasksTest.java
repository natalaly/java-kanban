package ru.yandex.practicum.tasktracker.server;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.builder.TestDataBuilder;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;

public class HttpSubtasksTest extends HttpTaskManagerTest {

  @BeforeEach
  @Override
  void setUp() throws IOException {
    BASE_URL = "http://localhost:8080";
    BASE_ENDPOINT = BASE_URL + "/subtasks";
    super.setUp();
  }

  /* GET */

  @Test
  @Override
  @DisplayName("GET /subtasks should return an empty list from a new TaskManager")
  public void getReturnsAnEmptyListFromNewTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Subtask>> listTypeToken = new TypeToken<List<Subtask>>() {
    };
    final List<Subtask> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty list of Subtasks.");
  }

  @Test
  @Override
  @DisplayName("GET /subtasks should return list with subtasks from TaskManager")
  public void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    TestDataBuilder.addTaskDataToTheTaskManager(manager);
    final List<Subtask> expected = manager.getSubtasks();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    TypeToken<List<Subtask>> listTypeToken = new TypeToken<List<Subtask>>() {
    };
    final List<Subtask> actualTasks = parseTasksFromResponse(response, listTypeToken);
    Assertions.assertFalse(actualTasks.isEmpty(), "Should not return an empty list of Subatsks.");
    Assertions.assertIterableEquals(expected, actualTasks, "Should return same List of Subatsks.");
  }

  @Test
  @Override
  @DisplayName("GET /subatsks/{id} should return Subtask by valid ID from TaskManager")
  public void getWithIdReturnsAnValidTaskObjectFromManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic epic = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d e"));
    final Subtask expected = manager.addSubtask(
        TestDataBuilder.buildSubtask("subtask", "d", epic.getId()));
    final int id = expected.getId();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonObject(response);
    final TypeToken<Subtask> subtaskTypeToken = new TypeToken<Subtask>() {
    };
    Subtask actual = parseTaskFromResponse(response, subtaskTypeToken);
    Assertions.assertEquals(expected, actual, "Returned subtask is not correct.");

  }

  /* POST */
  @Test
  @Override
  @DisplayName("POST /subtasks should add Subtask to the TaskManager ")
  public void postShouldAddTaskObjectToTheTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic epic = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d e"));
    final Subtask expected = TestDataBuilder.buildSubtask("subtask", "d", epic.getId());
    int expectedNumberOfSubtasks = manager.getSubtasks().size() + 1;
    final HttpRequest request = createPostRequest(BASE_ENDPOINT, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 201);
    TypeToken<Subtask> subtaskTypeToken = new TypeToken<Subtask>() {
    };
    final Subtask actual = gson.fromJson(response.body(), subtaskTypeToken.getType());
    assertTaskFieldsAreSame(expected, actual);
    Assertions.assertEquals(expectedNumberOfSubtasks, manager.getSubtasks().size(),
        "The number of subtasks should have increased by 1.");
  }

  @Test
  @Override
  @DisplayName("POST /subtasks/{id} should update Subtask")
  public void postWithIdCallsUpdateTaskObjectWithNewStartTime()
      throws IOException, InterruptedException {
    /* Given */
    final Epic epic = manager.addEpic(TestDataBuilder.buildEpic(1, "epic", "d"));
    final Subtask existed = manager.addSubtask(
        TestDataBuilder.buildSubtask("subtask", "d", epic.getId()));
    final int id = existed.getId();
    final Subtask expected = existed.copy();
    expected.setStartTime(LocalDateTime.now());
    final HttpRequest request = createPostRequest(BASE_ENDPOINT + "/" + id, expected);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 201);
    final Subtask actual = manager.getSubtaskById(id);
    assertTaskFieldsAreSame(expected, actual);
    Assertions.assertEquals(expected, actual, "Id should be same.");
  }

  @Test
  @Override
  @DisplayName("DELETE /subtasks/{id} should delete existed in the TaskManager subtask")
  public void deleteWithIdShouldDeleteTaskObjectFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    final Epic epic = manager.addEpic(
        TestDataBuilder.buildEpic(1, "epic", "d e"));
    final Subtask existed = manager.addSubtask(
        TestDataBuilder.buildSubtask("subtask", "d", epic.getId()));
    final int id = existed.getId();
    final HttpRequest request = createDeleteRequest(BASE_ENDPOINT + "/" + id);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    final TaskNotFoundException actualException = Assertions.assertThrows(
        TaskNotFoundException.class,
        () -> {
          manager.getSubtaskById(id);
        });
  }


}



