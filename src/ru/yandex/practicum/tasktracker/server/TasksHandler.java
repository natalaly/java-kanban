package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.exception.TaskPrioritizationException;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/tasks"
 */
public class TasksHandler extends BaseHttpHandler implements HttpHandler {

  private static final String TASKS_PATH = "^/tasks$";
  private static final String TASKS_ID_PATH = "^/tasks/\\d+$";
  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private final TaskManager taskManager;
  private final Gson gson;

  public TasksHandler(final TaskManager taskManager, final Gson gson) {
    this.taskManager = taskManager;
    this.gson = gson;
  }

  @Override
  public void handle(HttpExchange exchange) {
    try {
      final String path = exchange.getRequestURI().getPath();
      final String requestMethod = exchange.getRequestMethod();

      switch (requestMethod) {
        case "GET": {
          handleGET(exchange, path);
          break;
        }
        case "POST": {
          handlePOST(exchange, path);
          break;
        }
        case "DELETE": {
          handleDelete(exchange, path);
          break;
        }
        default: {
          System.out.println(
              "Only GET, PUT or DELETE methods can be accepted but received: " + requestMethod);
          sendNotAllowed405(exchange);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      exchange.close();
    }


  }

  private void handlePOST(HttpExchange exchange, String path) throws IOException {
    if (Pattern.matches(TASKS_PATH, path)) {
      handleAddTask(exchange);
    } else if (Pattern.matches(TASKS_ID_PATH, path)) {
      handleUpdateTask(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  private void handleAddTask(HttpExchange exchange) throws IOException {
    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!isValidJsonTask(jsonBody)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body -405");
      return;
    }

    Task task = gson.fromJson(requestBody, Task.class);
    try {
      String response = gson.toJson(taskManager.addTask(task));
      sendCreated201(exchange);
      System.out.println("task was added to the TM");
    } catch (Exception e) {
      sendHasInteractions406(exchange);
      System.out.println("Task has time conflict with existing task - 406");
    }
  }

  private void handleUpdateTask(HttpExchange exchange, String path) throws IOException {
    final String pathId = path.replaceFirst("/tasks/", "");
    final int id = parsePathID(pathId);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong Id format - 405");
      return;
    }

    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!isValidJsonTask(jsonBody)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body -405");
      return;
    }
    Task task = gson.fromJson(requestBody, Task.class);
    if (task.getId() != id) {
      sendBadRequest400(exchange);
      System.out.println("Id in the path and body requests are different - 400");
      return;
    }
    try {
      taskManager.updateTask(task);
      sendCreated201(exchange);
      System.out.println("Task updated - 201, id = " + id);
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found task with id - 404");
    } catch (TaskPrioritizationException e) {
      sendHasInteractions406(exchange);
      System.out.println("Task has time conflict with existing task - 406");
    }
  }

  private boolean isValidJsonTask(JsonObject jsonObject) {
    return jsonObject.has("id") &&
        jsonObject.has("title") &&
        jsonObject.has("description") &&
        jsonObject.has("status") &&
        jsonObject.has("duration") &&
        jsonObject.has("startTime");
  }

  private void handleGET(HttpExchange exchange, String path) throws IOException {
   if (Pattern.matches(TASKS_PATH, path)) {
     handleGetTasks(exchange,path);
   } else if (Pattern.matches(TASKS_ID_PATH, path)) {
     handleGetTaskById(exchange,path);
   } else {
     System.out.println("Wrong path - 400");
     sendBadRequest400(exchange);
   }
  }

  private void handleGetTasks(HttpExchange exchange, String path) throws IOException {
    String response = gson.toJson(taskManager.getTasks());
    sendText200(exchange, response);
    System.out.println("Get Task List - 200");
  }

  private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
    final String pathId = path.replaceFirst("/tasks/", "");
    final int id = parsePathID(pathId);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong Id format - 405");
      return;
    }
    try {
      String response = gson.toJson(taskManager.getTaskById(id));
      System.out.println("Viewed Task with id =" + id);
      sendText200(exchange, response);
      System.out.println("get task by id - 200 ");
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found task with id - 404");
    }
  }

  //TODO should we handle case when id is valid but not exist in TM?
  private void handleDelete(HttpExchange exchange, String path) throws IOException {
    if (!Pattern.matches(TASKS_ID_PATH, path)) {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
      return;
    }
    final String pathId = path.replaceFirst("/tasks/", "");
    final int id = parsePathID(pathId);
    if (id <= 0) {
      System.out.println("Invalid Id format - 405");
      sendNotAllowed405(exchange);
      return;
    }
    taskManager.deleteTask(id);
    System.out.println("Task was deleted, id =" + id);
    sendSuccess200(exchange);
  }

  //  TODO make PathHandler class?
  private int parsePathID(String path) {
    try {
      return Integer.parseInt(path);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
