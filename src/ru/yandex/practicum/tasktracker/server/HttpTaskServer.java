package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import ru.yandex.practicum.tasktracker.service.Managers;
import  ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Listens to 8080 port and accepts requests.
 * This is main class of the application.
 * It contains main method for the starting of the TaskManager.
 *
 */
public class HttpTaskServer {
// Listens to 8080 port
public static final int PORT = 8080;

private HttpServer server;
private Gson gson;

private TaskManager taskManager;

  public HttpTaskServer() throws IOException {
    this(Managers.getDefault());
  }
//TODO
   public HttpTaskServer (final TaskManager taskManager) throws IOException {
    this.taskManager = taskManager;
    gson = Managers.getGson();
    server = HttpServer.create(new InetSocketAddress("localhost",PORT),0);
//     server.createContext("/tasks", new TaskHandler());
//     server.createContext("/subtasks",new SubtaskHandler());
//     server.createContext("/epics",new EpicHandler());
//     server.createContext("/history",new HistoryHandler());
//     server.createContext("/prioritized",new PrioritizedHandler());
   }

  public static void main(String[] args) {

  }
  public void start() throws IOException {
    server.start();
  }

  public void stop() {
    server.stop(0);
  }
}
