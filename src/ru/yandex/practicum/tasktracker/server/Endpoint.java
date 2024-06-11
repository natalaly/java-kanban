package ru.yandex.practicum.tasktracker.server;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Endpoint {
  /* GET endpoints */
  GET_TASKS("^/tasks$"),
  GET_TASKS_ID("^/tasks/\\d+$"),

  GET_SUBTASKS("^/subtasks$"),
  GET_SUBTASKS_ID("^/subtasks/\\d+$"),

  GET_EPICS("^/epics$"),
  GET_EPICS_ID("^/epics/\\d+$"),
  GET_EPICS_ID_SUBTASKS("^/epics/\\d+/subtasks$"),

  GET_HISTORY("^/history$"),
  GET_PRIORITIZED("^/prioritized$"),

  /* POST endpoints */
  POST_TASKS("^/tasks$"),
  POST_TASKS_ID("^/tasks/\\d+$"),

  POST_SUBTASKS("^/subtasks$"),
  POST_SUBTASKS_ID("^/subtasks/\\d+$"),

  POST_EPIC("^/epics$"),

  /* DELETE endpoints */
  DELETE_TASKS_ID("^/tasks/\\d+$"),
  DELETE_SUBTASKS_ID("^/subtasks/\\d+$"),
  DELETE_EPICS_ID("^/epics/\\d+$"),

  UNKNOWN(
      "^(?!/(tasks|epics|subtasks|history|prioritized)(?:$|/)).*");

  private String path;

  Endpoint(String path) {
    this.path = path;
  }

  String getPath() {
    return path;
  }

  public static Endpoint getEndpoint(String method, String path) {
    return Arrays.stream(Endpoint.values())
        .filter(e -> e.matches(method, path))
        .findFirst()
        .orElse(UNKNOWN);
  }

  private boolean matches(String method, String path) {
    return this.name().startsWith(method.toUpperCase()) &&
        Pattern.matches(this.path, path);
  }


}
