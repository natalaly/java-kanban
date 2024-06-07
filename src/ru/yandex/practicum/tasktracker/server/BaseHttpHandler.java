package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Common to all HTTP handlers class. Contains general methods for reading and sending data:
 * <ul>
 *   <li>{@link #sendText200(HttpExchange, String)} - to send a general response if successful;</li>
 *   <li>{@link #sendCreated201(HttpExchange)} - to send a response indicating that the resource was successfully created;</li>
 *   <li>{@link #sendNotFound404(HttpExchange)} - to send a response if the object was not found;</li>
 *   <li>{@link #sendNotAllowed405(HttpExchange)} - to send a response indicating that the method is not allowed;</li>
 *   <li>{@link #sendHasInteractions406(HttpExchange)} - to send a response if, during creation or update, a task has a time conflict with existing ones.</li>
 * </ul>
 *
 * @see TaskManager
 */
public class BaseHttpHandler {
  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  public void sendText200(HttpExchange h, String text) throws IOException {
    sendResponse(h, text, 200);
  }

  public void sendSuccess200(HttpExchange h) throws IOException {
    sendResponse(h, 200);
  }

  public void sendCreated201(HttpExchange h) throws IOException {
    sendResponse(h,  201);
  }

  public void sendBadRequest400(HttpExchange h) throws IOException {
    sendResponse(h, 400);
  }

  public void sendNotFound404(HttpExchange h) throws IOException {
    sendResponse(h, 404);
  }

  public void sendNotAllowed405(HttpExchange h) throws IOException {
    sendResponse(h, 405);
  }


  public void sendHasInteractions406(HttpExchange h) throws IOException {
    sendResponse(h, 406);
  }

  public String readText(HttpExchange exchange) throws IOException {
    return new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
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
