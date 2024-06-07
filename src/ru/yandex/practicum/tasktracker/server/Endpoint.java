package ru.yandex.practicum.tasktracker.server;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Endpoint {
  /* GET endpoints */
  GET_TASKS("^/tasks$") ,
  GET_TASKS_ID("^/tasks/\\d+$"),
  GET_SUBTASKS(""),
  GET_SUBTASKS_ID(""),
  GET_EPICS(""),
  GET_EPICS_ID(""),
  GET_EPICS_ID_SUBTASKS(""),
  GET_HISTORY(""),
  GET_PRIORITIZED(""),

  /* POST endpoints */
  POST_TASKS("^/tasks$"),
  POST_TASKS_ID("^/tasks/\\d+$"),
  POST_SUBTASKS(""),
  POST_SUBTASKS_ID(""),
  POST_EPIC(""),

  /* DELETE endpoints */
  DELETE_TASKS_ID("^/tasks/\\d+$"),
  DELETE_SUBTASKS_ID(""),
  DELETE_EPICS_ID(""),

  UNKNOWN("");

  private String path;

  Endpoint(String path) {
    this.path = path;
  }
  String getPath() {
    return path;
  }

  static Endpoint getEndpoint(String method, String path) {
   return  Arrays.stream(Endpoint.values())
       .filter(e -> e.matches(method, path))
       .findFirst()
       .orElse(UNKNOWN);
  }

  private  boolean matches(String method, String path) {
    return this.name().startsWith(method.toUpperCase()) &&
        Pattern.matches(this.path, path);
  }


}
