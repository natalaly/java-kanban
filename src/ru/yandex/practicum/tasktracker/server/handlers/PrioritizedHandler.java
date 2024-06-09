package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/tasks"
 */
public class PrioritizedHandler extends BaseHttpHandler {

  private final TaskManager taskManager;
  private final Gson gson;

  public PrioritizedHandler(final TaskManager taskManager, final Gson gson) {
    this.taskManager = taskManager;
    this.gson = gson;
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (Endpoint.GET_PRIORITIZED.equals(endpoint)) {
      handleGetHistory(exchange);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method for Prioritized");
    sendNotAllowed405(exchange);
  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method for Prioritized");
    sendNotAllowed405(exchange);
  }

  private void handleGetHistory(HttpExchange exchange) throws IOException {
    String response = gson.toJson(taskManager.getPrioritizedTasks());
    sendText200(exchange, response);
    System.out.println("Get Prioritized List - 200");
  }

}
