package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.exception.TaskPrioritizationException;
import ru.yandex.practicum.tasktracker.exception.TaskValidationException;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/tasks"
 */
public class SubtasksHandler extends BaseHttpHandler {

  private final TaskManager taskManager;
  private final Gson gson;

  public SubtasksHandler(final TaskManager taskManager, final Gson gson) {
    this.taskManager = taskManager;
    this.gson = gson;
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (Endpoint.GET_SUBTASKS.equals(endpoint)) {
      handleGetSubtasks(exchange, path);
    } else if (Endpoint.GET_SUBTASKS_ID.equals(endpoint)) {
      handleGetSubtaskById(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("POST", path);
    if (Endpoint.POST_SUBTASKS.equals(endpoint)) {
      handleAddSubtask(exchange);
    } else if (Endpoint.POST_SUBTASKS_ID.equals(endpoint)) {
      handleUpdateSubtask(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }

  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("DELETE", path);
    if (!Endpoint.DELETE_SUBTASKS_ID.equals(endpoint)) {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
      return;
    }
//    final int id = HandlersHelper.parsePathID(path);
    final String pathId = path.replaceFirst("/subtasks/", "");
    final int id = parsePathID(pathId);
//
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    taskManager.deleteSubtask(id);
    System.out.println("Subtask was deleted, id =" + id);
    sendSuccess200(exchange);
  }

  private void handleGetSubtasks(HttpExchange exchange, String path) throws IOException {
    String response = gson.toJson(taskManager.getSubtasks());
    sendText200(exchange, response);
    System.out.println("Get Subtask List - 200");
  }

  private void handleGetSubtaskById(HttpExchange exchange, String path) throws IOException {
//    final int id = HandlersHelper.parsePathID(path);
    final String pathId = path.replaceFirst("/subtasks/", "");
    final int id = parsePathID(pathId);
//
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    try {
      String response = gson.toJson(taskManager.getSubtaskById(id));
      System.out.println("Viewed Subtask with id =" + id);
      sendText200(exchange, response);
      System.out.println("get Subtask with id =" + id + "- 200 ");
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found subtask with id " + id + "- 404");
    }
  }

  private void handleAddSubtask(HttpExchange exchange) throws IOException {
    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!isValidJsonTask(jsonBody)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body -405");
      return;
    }

    Subtask subtask = gson.fromJson(requestBody, Subtask.class);
    try {
      String response = gson.toJson(taskManager.addSubtask(subtask));
      sendCreated201(exchange);
      System.out.println("Subtask was added to the TM");
    } catch (TaskValidationException e) {
      sendNotFound404(exchange);
      System.out.println("Not found corresponding Epic for Subtask  - 404");
    } catch (TaskPrioritizationException e) {
      sendHasInteractions406(exchange);
      System.out.println("Subtask has time conflict with existing task - 406");
    }
  }

  private void handleUpdateSubtask(HttpExchange exchange, String path) throws IOException {
//    final int id = HandlersHelper.parsePathID(path);
    final String pathId = path.replaceFirst("/subtasks/", "");
    final int id = parsePathID(pathId);
//
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }

    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!isValidJsonTask(jsonBody)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body -405");
      return;
    }
    Subtask subtask = gson.fromJson(requestBody, Subtask.class);
    if (subtask.getId() != id) {
      sendBadRequest400(exchange);
      System.out.println("Id in the path and body requests are different - 400");
      return;
    }
    try {
      taskManager.updateSubtask(subtask);
      sendCreated201(exchange);
      System.out.println("Subtask updated - 201, id = " + id);
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found Subtask with id - 404");
    } catch (TaskValidationException e) {
      sendNotFound404(exchange);
      System.out.println("Not found corresponding Epic for Subtask  - 404");
    } catch (TaskPrioritizationException e) {
      sendHasInteractions406(exchange);
      System.out.println("Subtask has time conflict with existing task - 406");
    }
  }

  private boolean isValidJsonTask(JsonObject jsonObject) {
    return jsonObject.has("epicId") &&
        jsonObject.has("id") &&
        jsonObject.has("title") &&
        jsonObject.has("description") &&
        jsonObject.has("status") &&
        jsonObject.has("duration") &&
        jsonObject.has("startTime");
  }

//  TODO use HandlersHelper.parsePathID(path);
  private int parsePathID(String path) {
    try {
      return Integer.parseInt(path);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
