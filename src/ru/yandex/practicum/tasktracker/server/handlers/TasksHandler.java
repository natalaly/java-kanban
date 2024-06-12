package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.exception.TaskPrioritizationException;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.server.HandlersHelper;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/tasks"
 */
public class TasksHandler extends BaseHttpHandler {

  public TasksHandler(final TaskManager taskManager, final Gson gson) {
    super(taskManager, gson);
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (Endpoint.GET_TASKS.equals(endpoint)) {
      handleGetTasks(exchange);
    } else if (Endpoint.GET_TASKS_ID.equals(endpoint)) {
      handleGetTaskById(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }

  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("POST", path);
    if (Endpoint.POST_TASKS.equals(endpoint)) {
      handleAddTask(exchange);
    } else if (Endpoint.POST_TASKS_ID.equals(endpoint)) {
      handleUpdateTask(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("DELETE", path);
    if (!Endpoint.DELETE_TASKS_ID.equals(endpoint)) {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
      return;
    }
    final int id = HandlersHelper.parsePathID(path);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    taskManager.deleteTask(id);
    System.out.println("Task was deleted, id =" + id);
    sendSuccess200(exchange);
  }

  private void handleGetTasks(HttpExchange exchange) throws IOException {
    String response = gson.toJson(taskManager.getTasks());
    sendText200(exchange, response);
    System.out.println("Get Task List - 200");
  }

  private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
    final int id = HandlersHelper.parsePathID(path);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    try {
      String response = gson.toJson(taskManager.getTaskById(id));
      System.out.println("Viewed Task with id =" + id);
      sendText200(exchange, response);
      System.out.println("get task by id - 200 ");
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found task with id " + id + "- 404");
    }
  }

  private void handleAddTask(HttpExchange exchange) throws IOException {
    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!HandlersHelper.isValidJsonBody(jsonBody, TaskType.TASK)) { //!isValidJsonTask(jsonBody)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body -405");
      return;
    }

    Task task = gson.fromJson(requestBody, Task.class);
    try {
      String response = gson.toJson(taskManager.addTask(task));
      sendCreated201(exchange, response);
      System.out.println("task was added to the TM");
    } catch (Exception e) {
      sendHasInteractions406(exchange);
      System.out.println("Task has time conflict with existing task - 406");
    }
  }

  private void handleUpdateTask(HttpExchange exchange, String path) throws IOException {
    final int id = HandlersHelper.parsePathID(path);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }

    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!HandlersHelper.isValidJsonBody(jsonBody, TaskType.TASK)) {
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

}
