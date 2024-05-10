package ru.yandex.practicum.tasktracker.service;

import java.io.File;

/**
 * Utility class, is used for getting default implementations of various managers.
 * Provides methods to retrieve default instances of {@link TaskManager}, {@link HistoryManager},
 * and create {@link FileBackedTaskManager} instances from the specified file.
 */

public class Managers {

  private Managers() {

  } //Don't let anyone instantiate this class.

  public static TaskManager getDefault() {
    return new InMemoryTaskManager();
  }

  public static HistoryManager getDefaultHistory() {
    return new InMemoryHistoryManager();
  }

  public static FileBackedTaskManager getFileBackedTaskManager(File file) {
    return FileBackedTaskManager.loadFromFile(file);
  }
}
