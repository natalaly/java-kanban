package ru.yandex.practicum.tasktracker.server;

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



}
