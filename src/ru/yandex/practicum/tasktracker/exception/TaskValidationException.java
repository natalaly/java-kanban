package ru.yandex.practicum.tasktracker.exception;

public class TaskValidationException extends RuntimeException {

  public TaskValidationException(String message) {
    super(message);
  }

}
