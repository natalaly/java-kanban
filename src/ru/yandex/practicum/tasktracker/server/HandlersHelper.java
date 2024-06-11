package ru.yandex.practicum.tasktracker.server;

import com.google.gson.JsonObject;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskType;

public class HandlersHelper {

  public static  int parsePathID(String path) {
    String toExclude;
    if (path.contains("/tasks")) {
      toExclude = "/tasks/";
    } else if (path.contains("/subtasks")) {
      toExclude = "/subtasks/";
    } else if (path.contains("/epics")) {
      toExclude = "/epics/";
    } else {
      toExclude ="";
    }
    final String pathId = path.replaceFirst(toExclude, "").replaceAll("/subtasks","");
    try {
      return Integer.parseInt(pathId);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public static <T extends Task> boolean isValidJsonBody(JsonObject jsonObject, TaskType type) {
    switch (type) {
      case TASK -> {
        return isValidJsonTaskBody(jsonObject);
      }
      case EPIC-> {
        return isValidJsonEpicBody(jsonObject);
      }
      case SUBTASK -> {
        return isValidJsonSubtaskBody(jsonObject);
      }
      default -> {
        return false;
      }
    }


  }

  private static boolean isValidJsonSubtaskBody(JsonObject jsonObject) {    return jsonObject.has("epicId") &&
      jsonObject.has("id") &&
      jsonObject.has("title") &&
      jsonObject.has("description") &&
      jsonObject.has("status") &&
      jsonObject.has("duration") &&
      jsonObject.has("startTime");

  }

  private static boolean isValidJsonEpicBody(JsonObject jsonObject) {
    return jsonObject.has("id") &&
        jsonObject.has("title") &&
        jsonObject.has("description");
  }

  private static boolean isValidJsonTaskBody(JsonObject jsonObject) {
    return jsonObject.has("id") &&
        jsonObject.has("title") &&
        jsonObject.has("description") &&
        jsonObject.has("status") &&
        jsonObject.has("duration") &&
        jsonObject.has("startTime");
  }



}
