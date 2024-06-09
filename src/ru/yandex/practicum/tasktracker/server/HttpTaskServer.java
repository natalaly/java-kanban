package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.Managers;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Listens to 8080 port and accepts requests. This is main class of the application.It contains main
 * method which will be launched to start working with the application. To accept and handle user
 * requests {@link HttpServer} is used. Its instance starts when application launches.
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
  public HttpTaskServer(final TaskManager taskManager) throws IOException {
    this.taskManager = taskManager;
    gson = Managers.getGson();
    server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
    server.createContext("/tasks", new TasksHandler(this.taskManager, this.gson));
    server.createContext("/subtasks",new SubtasksHandler(this.taskManager, this.gson));
    server.createContext("/epics",new EpicHandler(this.taskManager, this.gson));
//    server.createContext("/history",new HistoryHandler());
//    server.createContext("/prioritized",new PrioritizedHandler());
  }

  public static void main(String[] args) throws IOException {
    HttpTaskServer taskServer = new HttpTaskServer(Managers.getFileBackedTaskManager(Path.of("resources/test/httpTasksData.csv").toFile()));
    taskServer.start();
//    taskServer.stop();
  }

  public void start() throws IOException {
    System.out.println("Started TaskServer " + PORT);
    System.out.println("http://localhost:" + PORT + "/tasks");
    server.start();
  }

  public void stop() {
    System.out.println("Stopped server on port " + PORT);
    server.stop(0);
  }
}
