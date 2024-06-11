package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import ru.yandex.practicum.tasktracker.server.Endpoint;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * HTTP handler class for the path "/prioritized"
 */
public class PrioritizedHandler extends HistoryHandler {

  public PrioritizedHandler(final TaskManager taskManager, final Gson gson) {
    super(taskManager, gson);
    endpoint = Endpoint.GET_PRIORITIZED;
  }

  @Override
  protected void handleGetPath(HttpExchange exchange) throws IOException {
    String response = gson.toJson(taskManager.getPrioritizedTasks());
    sendText200(exchange, response);
    System.out.println("Get Prioritized List - 200");
  }

}
