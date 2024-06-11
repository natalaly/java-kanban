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
import ru.yandex.practicum.tasktracker.model.Task;

public class HttpPrioritizedTest extends HttpHistoryTest {

  @BeforeEach
  @Override
  void setUp() throws IOException {
    super.setUp();
    BASE_ENDPOINT = BASE_URL + "/prioritized";
  }

  /* GET */

  @Test
  @Override
  @DisplayName("GET /prioritized should return an empty list from a new TaskManager")
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
    Assertions.assertTrue(actualTasks.isEmpty(), "Should return an empty history list.");
  }

  @Test
  @Override
  @DisplayName("GET /prioritized should return list with tasks from TaskManager")
  public void getReturnsAnValidListFromTaskManager()
      throws IOException, InterruptedException {
    /* Given */
    TestDataBuilder.addTaskDataToTheTaskManager(manager);
    final List<Task> expected = manager.getPrioritizedTasks();
    final HttpRequest request = createGetRequest(BASE_ENDPOINT);
    /* When */
    final HttpResponse<String> response = sendRequest(request);
    /* Then */
    assertStatusCode(response, 200);
    assertResponseBodyIsJsonArray(response);
    final List<Task> actual = parseAllTaskTypesFromResponse(response);
    Assertions.assertFalse(actual.isEmpty(), "Should not return an empty  list of task objects .");
    Assertions.assertIterableEquals(expected, actual, "Should return same list of task objects.");
  }

}



