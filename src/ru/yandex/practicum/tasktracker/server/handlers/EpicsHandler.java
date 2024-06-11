package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.exception.TaskNotFoundException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.server.HandlersHelper;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/epics"
 */
public class EpicsHandler extends BaseHttpHandler {

  public EpicsHandler(final TaskManager taskManager, final Gson gson) {
    super(taskManager,gson);
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (Endpoint.GET_EPICS.equals(endpoint)) {
      handleGetEpics(exchange, path);
    } else if (Endpoint.GET_EPICS_ID.equals(endpoint)) {
      handleGetEpicById(exchange, path);
    } else if (Endpoint.GET_EPICS_ID_SUBTASKS.equals(endpoint)) {
      handleGetSubtasksByEpicId(exchange, path);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("POST", path);
    if (!Endpoint.POST_EPIC.equals(endpoint)) {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
      return;
    }
      handleAddEpic(exchange);
  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("DELETE", path);
    if (!Endpoint.DELETE_EPICS_ID.equals(endpoint)) {
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
    taskManager.deleteEpic(id);
    System.out.println("Epic was deleted, id =" + id);
    sendSuccess200(exchange);
  }

  private void handleGetEpics(HttpExchange exchange, String path) throws IOException {
    String response = gson.toJson(taskManager.getEpics());
    sendText200(exchange, response);
    System.out.println("Get Epics List - 200");
  }

  private void handleGetEpicById(HttpExchange exchange, String path) throws IOException {
    final int id = HandlersHelper.parsePathID(path);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    try {
      String response = gson.toJson(taskManager.getEpicById(id));

      sendText200(exchange, response);
      System.out.println("Get epic by id = " + id + "- 200 ");
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found epic with id " + id + "- 404");
    }
  }

  private void handleGetSubtasksByEpicId(HttpExchange exchange, String path) throws IOException {
        final int id = HandlersHelper.parsePathID(path);
    if (id <= 0) {
      sendNotAllowed405(exchange);
      System.out.println("Invalid Id format - 405");
      return;
    }
    try {
      String response = gson.toJson(taskManager.getSubtasksByEpicId(id));
      sendText200(exchange, response);
      System.out.println("Viewed Subtasks of Epic with id = " + id + "- 200 ");
    } catch (TaskNotFoundException e) {
      sendNotFound404(exchange);
      System.out.println("Not found epic with id " + id + "- 404");
    }
  }

  private void handleAddEpic(HttpExchange exchange) throws IOException {
    final String requestBody = readText(exchange);
    final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
    if (!HandlersHelper.isValidJsonBody(jsonBody, TaskType.EPIC)) {
      sendNotAllowed405(exchange);
      System.out.println("Wrong set of fields in req body - 405");
      return;
    }

    Epic epic = gson.fromJson(requestBody, Epic.class);
      String response = gson.toJson(taskManager.addEpic(epic));
      sendCreated201(exchange,response);
      System.out.println("Epic was added to the TM");
  }


}
