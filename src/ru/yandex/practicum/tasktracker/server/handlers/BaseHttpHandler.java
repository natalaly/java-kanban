package ru.yandex.practicum.tasktracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Common to all HTTP handlers class. Handles incoming HTTP requests by delegating and provides
 * general methods for reading and sending data:
 * <ul>
 *   <li>{@link #sendText200(HttpExchange, String)} - sends a general response with a 200 status code if successful;</li>
 *   <li>{@link #sendSuccess200(HttpExchange)} - sends a response with a 200 status code indicating success;</li>
 *   <li>{@link #sendCreated201(HttpExchange)} - sends a response with a 201 status code indicating that the resource was successfully created;</li>
 *   <li>{@link #sendCreated201(HttpExchange, String)} - sends a response with a 201 status code and text indicating that the resource was successfully created;</li>
 *   <li>{@link #sendBadRequest400(HttpExchange)} - sends a response with a 400 status code indicating a bad request;</li>
 *   <li>{@link #sendNotFound404(HttpExchange)} - sends a response with a 404 status code if the object was not found;</li>
 *   <li>{@link #sendNotAllowed405(HttpExchange)} - sends a response with a 405 status code indicating that the method is not allowed;</li>
 *   <li>{@link #sendHasInteractions406(HttpExchange)} - sends a response with a 406 status code if a task has a time conflict with existing ones;</li>
 *   <li>{@link #readText(HttpExchange)} - reads the request body as a string using the default charset.</li>
 *   <li>{@link #handle(HttpExchange)} - handles incoming HTTP requests by delegating to appropriate method handlers based on the request method.</li>
 * </ul>
 *
 * @see TaskManager
 * @see TasksHandler
 * @see EpicsHandler
 * @see SubtasksHandler
 * @see HistoryHandler
 * @see PrioritizedHandler
 */
public abstract class BaseHttpHandler implements HttpHandler {

  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  protected final TaskManager taskManager;
  protected final Gson gson;

  protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
    this.taskManager = taskManager;
    this.gson = gson;
  }


  /**
   * Handles incoming HTTP requests by delegating to appropriate method handlers based on the
   * request method. Supported methods are GET, POST, and DELETE.
   *
   * @param exchange the HttpExchange containing the request from the client and used to send the
   *                 response
   */
  @Override
  public void handle(HttpExchange exchange) {
    try {
      final String path = exchange.getRequestURI().getPath();
      final String requestMethod = exchange.getRequestMethod();
      switch (requestMethod) {
        case "GET": {
          handleGet(exchange, path);
          break;
        }
        case "POST": {
          handlePost(exchange, path);
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

  protected abstract void handleDelete(HttpExchange exchange, String path) throws IOException;

  protected abstract void handlePost(HttpExchange exchange, String path) throws IOException;

  protected abstract void handleGet(HttpExchange exchange, String path) throws IOException;


  public void sendText200(HttpExchange h, String text) throws IOException {
    sendResponse(h, text, 200);
  }

  public void sendSuccess200(HttpExchange h) throws IOException {
    sendResponse(h, 200);
  }

  public void sendCreated201(HttpExchange h) throws IOException {
    sendResponse(h, 201);
  }

  public void sendCreated201(HttpExchange h, String text) throws IOException {
    sendResponse(h, text, 201);
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
