package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/history"
 */
public class HistoryHandler extends BaseHttpHandler {
  protected Endpoint endpoint;

  public HistoryHandler(final TaskManager taskManager, final Gson gson) {
    super(taskManager,gson);
    endpoint = Endpoint.GET_HISTORY;
  }

  @Override
  protected void handleGet(HttpExchange exchange, String path) throws IOException {
    Endpoint endpoint = Endpoint.getEndpoint("GET", path);
    if (this.endpoint.equals(endpoint)) {
      handleGetPath(exchange);
    } else {
      System.out.println("Wrong path - 400");
      sendBadRequest400(exchange);
    }
  }

  @Override
  protected void handlePost(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method ");
    sendNotAllowed405(exchange);
  }

  @Override
  protected void handleDelete(HttpExchange exchange, String path) throws IOException {
    System.out.println("Unsupported method ");
    sendNotAllowed405(exchange);
  }

  protected void handleGetPath(HttpExchange exchange) throws IOException {
    String response = gson.toJson(taskManager.getHistory());
    sendText200(exchange, response);
    System.out.println("Get History List - 200");
  }

}
