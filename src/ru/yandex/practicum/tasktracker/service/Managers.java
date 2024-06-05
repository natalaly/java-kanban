package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.time.LocalDateTime;
import ru.yandex.practicum.tasktracker.adapter.LocaleDateTimeAdapter;

/**
 * Utility class, is used for getting default implementations of various managers. Provides methods
 * to retrieve default instances of {@link TaskManager}, {@link HistoryManager}, and create
 * {@link FileBackedTaskManager} instances from the specified file.
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

  public static TaskManager getFileBackedTaskManager(final File file) {
    return FileBackedTaskManager.loadFromFile(file);
  }

  public static Gson getGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocaleDateTimeAdapter())
        .serializeNulls();;
    return gsonBuilder.create();
  }
}
