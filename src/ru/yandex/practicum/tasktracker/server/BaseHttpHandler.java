package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Common to all HTTP handlers class. Contains general methods for reading and sending data:
 * <ul>
 *   <li>{@link #sendText(HttpExchange, String)} - to send a general response if successful;</li>
 *   <li>{@link #sendCreated(HttpExchange)} - to send a response indicating that the resource was successfully created;</li>
 *   <li>{@link #sendNotFound(HttpExchange)} - to send a response if the object was not found;</li>
 *   <li>{@link #sendNotAllowed(HttpExchange)} - to send a response indicating that the method is not allowed;</li>
 *   <li>{@link #sendHasInteractions(HttpExchange)} - to send a response if, during creation or update, a task has a time conflict with existing ones.</li>
 * </ul>
 *
 * @see TaskManager
 */
public class BaseHttpHandler {

  public void sendText(HttpExchange h, String text) throws IOException {
    sendResponse(h, text, 200);
  }

  public void sendCreated(HttpExchange h) throws IOException {
    sendResponse(h,  201);
  }


  public void sendNotFound(HttpExchange h) throws IOException {
    sendResponse(h, 404);
  }

  public void sendNotAllowed(HttpExchange h) throws IOException {
    sendResponse(h, 405);
  }


  public void sendHasInteractions(HttpExchange h) throws IOException {
    sendResponse(h, 406);
  }

  private void sendResponse(HttpExchange exchange, int responseCode) throws IOException {
    sendResponse(exchange, null, responseCode);
  }

  private void sendResponse(HttpExchange exchange, String responseText, int responseCode)
      throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

    final byte[] responseInBytes =
        responseText != null ? responseText.getBytes(StandardCharsets.UTF_8) : new byte[0];

    exchange.sendResponseHeaders(responseCode, responseInBytes.length);

    try (final OutputStream responseBody = exchange.getResponseBody()) {
      if (responseInBytes.length > 0) {
        responseBody.write(responseInBytes);
      }
    }
  }
}
