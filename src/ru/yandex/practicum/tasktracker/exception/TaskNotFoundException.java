package ru.yandex.practicum.tasktracker.exception;

public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException(String message) {
    super(message);
  }

}
