package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/tasks"
 */
public class HistoryHandler extends BaseHttpHandler {

  private final TaskManager taskManager;
  private final Gson gson;

  public HistoryHandler(final TaskManager taskManager, final Gson gson) {
    this.taskManager = taskManager;
    this.gson = gson;
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (Endpoint.GET_HISTORY.equals(endpoint)) {
      handleGetHistory(exchange);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method for History");
    sendNotAllowed405(exchange);
  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method for History");
    sendNotAllowed405(exchange);
  }

  private void handleGetHistory(HttpExchange exchange) throws IOException {
    String response = gson.toJson(taskManager.getHistory());
    sendText200(exchange, response);
    System.out.println("Get History List - 200");
  }

}
