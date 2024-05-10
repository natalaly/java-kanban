package ru.yandex.practicum.tasktracker.exception;

public class ManagerSaveException extends RuntimeException {

  public ManagerSaveException() {
  }

  public ManagerSaveException(String message) {
    super(message);
  }

  public ManagerSaveException(String message, Throwable cause) {
    super(message, cause);
  }
}
