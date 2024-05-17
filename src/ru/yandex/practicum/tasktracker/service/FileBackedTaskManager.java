package ru.yandex.practicum.tasktracker.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import ru.yandex.practicum.tasktracker.exception.ManagerLoadException;
import ru.yandex.practicum.tasktracker.exception.ManagerSaveException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;

/**
 * Task manager extends {@link InMemoryTaskManager} and provides automatic <b>saving</b> and
 * <b>loading</b> of data to/from a <b>file</b>.
 * <p>
 * The {@link FileBackedTaskManager} receives a file for auto-saving in its constructor and stores
 * it in a field. The {@link #save()} method saves the current state of the manager to the specified
 * file after each modifying operation.
 * <p>
 * In addition to the save method, there is a static factory method {@link #loadFromFile(File)} that
 * restores the manager's data from the file each time the program is launched.
 * <p>
 * This class is an implementation of the {@link TaskManager} interface, which manages tasks of
 * different types including {@link Task}, {@link Epic}, and {@link Subtask}. It provides
 * functionalities for storing, retrieving, updating, and deleting tasks.
 *
 * @see InMemoryTaskManager
 * @see TaskManager
 * @see Task
 * @see Epic
 * @see Subtask
 */

public class FileBackedTaskManager extends InMemoryTaskManager {

  private static final String TASKS_CSV_HEADER = "id,type,name,status,description,epic";
  private static final String HISTORY_HEADER = "history";
  private static final String TASKS_LINE_FORMAT = "^([^,\"']+,){5}[^,\"']+$";// Format used for ensuring that each task line in the file will contain exactly 6 fields separated by commas.
  private final File file;


  private FileBackedTaskManager(final File file) {
    super();
    this.file = file;
    load();
  }

  public static FileBackedTaskManager loadFromFile(final File file) {
    Objects.requireNonNull(file);
    return new FileBackedTaskManager(file);
  }

  @Override
  public void clearTasks() {
    super.clearTasks();
    save();
  }

  @Override
  public void clearEpics() {
    super.clearEpics();
    save();
  }

  @Override
  public void clearSubtasks() {
    super.clearSubtasks();
    save();
  }

  @Override
  public void deleteTask(final int id) {
    super.deleteTask(id);
    save();
  }

  @Override
  public void deleteEpic(final int id) {
    super.deleteEpic(id);
    save();
  }

  @Override
  public void deleteSubtask(final int id) {
    super.deleteSubtask(id);
    save();
  }

  @Override
  public Task getTaskById(final int id) {
    Task task = super.getTaskById(id);
    save();
    return task;
  }

  @Override
  public Epic getEpicById(final int id) {
    Epic epic = super.getEpicById(id);
    save();
    return epic;
  }

  @Override
  public Subtask getSubtaskById(final int id) {
    Subtask subtask = super.getSubtaskById(id);
    save();
    return subtask;
  }

  @Override
  public Task addTask(final Task task) {
    Task t = super.addTask(task);
    save();
    return t;
  }

  @Override
  public Epic addEpic(final Epic epic) {
    final Epic e = super.addEpic(epic);
    save();
    return e;
  }

  @Override
  public Subtask addSubtask(final Subtask subtask) {
    final Subtask st = super.addSubtask(subtask);
    save();
    return st;
  }

  @Override
  public void updateTask(final Task task) {
    super.updateTask(task);
    save();
  }

  @Override
  public void updateEpic(final Epic epic) {
    super.updateEpic(epic);
    save();
  }

  @Override
  public void updateSubtask(final Subtask subtask) {
    super.updateSubtask(subtask);
    save();
  }

  private void load() {
    try (final BufferedReader br = new BufferedReader(
        new FileReader(file, StandardCharsets.UTF_8))) {
      loadTask(br);
      loadHistory(br);
    } catch (IOException e) {
      throw new ManagerLoadException("An Error occurred during reading the file", e);
    }
    setCounterLastUsed();
  }

  private void save() {
    try (final Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
      fileWriter.write(TASKS_CSV_HEADER + System.lineSeparator());
      for (Task task : getAllTasks()) {
        fileWriter.write(task.toCsvLine() + System.lineSeparator());
      }
      for (Epic epic : getAllEpics()) {
        fileWriter.write(epic.toCsvLine() + System.lineSeparator());
      }
      for (Subtask subtask : getAllSubtasks()) {
        fileWriter.write(subtask.toCsvLine() + System.lineSeparator());
      }
      // save history
      fileWriter.write(HISTORY_HEADER + System.lineSeparator());
      for (Task historyTask : getHistory()) {
        fileWriter.write(historyTask.toCsvLine() + System.lineSeparator());
      }
    } catch (IOException e) {
      throw new ManagerSaveException("An error occurred during saving to the file.", e);
    }
  }

  private void loadTask(final BufferedReader br) throws IOException {
    final List<String> taskData = new ArrayList<>();
    while (br.ready()) {
      String line = br.readLine();
      if (line.equals(HISTORY_HEADER)) {
        break;
      }
      taskData.add(line);
    }
    if (!taskData.isEmpty()) {
      this.restoreAll(taskData, TASKS_CSV_HEADER);
    }
  }

  private void loadHistory(final BufferedReader br) throws IOException {
    final List<String> historyData = new ArrayList<>();
    while (br.ready()) {
      historyData.add(br.readLine());
    }
    if (historyData.size() != 0) {
      this.restoreAll(historyData, HISTORY_HEADER);
    }
  }

  private void restoreAll(final List<String> tasks, final String header) {
    switch (header) {
      case TASKS_CSV_HEADER -> restoreTasks(tasks);
      case HISTORY_HEADER -> restoreHistory(tasks);
    }
  }

  private void restoreTasks(final List<String> tasks) {
    for (String eachLine : tasks) {
      if (eachLine.equals(TASKS_CSV_HEADER) || eachLine.isBlank()) {
        continue;
      }
      final Task taskFromFile = this.fromString(eachLine);
      final TaskType typeFromFile = taskFromFile.getType();
      switch (typeFromFile) {
        case TASK -> this.tasks.put(taskFromFile.getId(), taskFromFile);
        case EPIC -> this.epics.put(taskFromFile.getId(), (Epic) taskFromFile);
        case SUBTASK -> {
          this.subtasks.put(taskFromFile.getId(), (Subtask) taskFromFile);
          int epicId = ((Subtask) taskFromFile).getEpicId();
          this.epics.get(epicId).addSubtask((Subtask) taskFromFile);
        }
      }
    }
  }

  private void restoreHistory(final List<String> tasksLines) {
    for (String eachLine : tasksLines) {
      if (eachLine.equals(HISTORY_HEADER) || eachLine.isBlank()) {
        continue;
      }
      Task taskToRestore = this.fromString(eachLine);
      if (TaskType.EPIC.equals(taskToRestore.getType())) {
        taskToRestore = epics.get(taskToRestore.getId());
      }
      historyManager.add(taskToRestore);
    }
  }

  private void setCounterLastUsed() {
    final Set<Integer> allIds = new HashSet<>(this.tasks.keySet());
    allIds.addAll(this.epics.keySet());
    allIds.addAll(this.subtasks.keySet());
    final Integer maxId = allIds.stream().max(Comparator.naturalOrder()).orElse(0);
    counter = maxId;
  }

  private Task fromString(final String stringCSV) {
    Objects.requireNonNull(stringCSV);
    if (!Pattern.matches(TASKS_LINE_FORMAT, stringCSV)) {
      return null;
    }
    final String[] taskData = stringCSV.split(",");
    final int id = Integer.parseInt(taskData[0]);
    final TaskType type = TaskType.valueOf(taskData[1]);
    final String title = taskData[2];
    final TaskStatus status = TaskStatus.valueOf(taskData[3]);
    final String description = taskData[4];
    switch (type) {
      case TASK -> {
        final Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        return task;
      }
      case EPIC -> {
        final Epic epic = new Epic();
        epic.setId(id);
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        return epic;
      }
      case SUBTASK -> {
        final Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setTitle(title);
        subtask.setDescription(description);
        subtask.setStatus(status);
        subtask.setEpicId(Integer.parseInt(taskData[5]));
        return subtask;
      }
    }
    return null;
  }

}
