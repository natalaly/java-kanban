package ru.yandex.practicum.tasktracker.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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

  private static final String TASKS_CSV_HEADER = "id,type,name,status,description,duration,startTime,epic";
  private static final String HISTORY_HEADER = "history";
  private static final String PRIORITIZED_HEADER = "prioritized";
  private static final String TASKS_LINE_FORMAT = "^([^,\"']+,){7}[^,\"']+$";// Format used for ensuring that each task line in the file will contain exactly 8 fields separated by commas.
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
      loadSection(br, TASKS_CSV_HEADER, HISTORY_HEADER);
      loadSection(br, HISTORY_HEADER, PRIORITIZED_HEADER);
      loadSection(br, PRIORITIZED_HEADER, "");
    } catch (IOException e) {
      throw new ManagerLoadException("An Error occurred during reading the file", e);
    }
    setCounterLastUsed();
  }

  private void save() {
    try (final Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
      saveSection(fileWriter, TASKS_CSV_HEADER, getAllTasks());
      saveSection(fileWriter, HISTORY_HEADER, historyManager.getHistory());
      saveSection(fileWriter, PRIORITIZED_HEADER, new ArrayList<>(prioritizedTasks));
    } catch (IOException e) {
      throw new ManagerSaveException("An error occurred during saving to the file.", e);
    }
  }

  private void saveSection(final Writer fileWriter, final String header,
      final List<Task> tasksToSave) throws IOException {
    fileWriter.write(header + System.lineSeparator());
    tasksToSave.forEach(task -> {
      try {
        fileWriter.write(task.toCsvLine() + System.lineSeparator());
      } catch (IOException e) {
        throw new ManagerSaveException(
            "An error occurred during saving tasks from taskManager to the file.", e);
      }
    });
  }

  private void loadSection(final BufferedReader br, final String start, final String end)
      throws IOException {
    final List<String> taskData = new ArrayList<>();

    while (br.ready()) {
      String line = br.readLine();
      if (line.equals(end)) {
        break;
      }
      taskData.add(line);
    }
    if (!taskData.isEmpty()) {
      this.restore(taskData, start);
    }
  }

  private void restore(final List<String> tasks, final String header) {
    switch (header) {
      case TASKS_CSV_HEADER -> restoreByTaskType(tasks);
      case HISTORY_HEADER -> restoreTasks(tasks, header, historyManager::add);
      case PRIORITIZED_HEADER -> restoreTasks(tasks, header, prioritizedTasks::add);
    }
  }

  private void restoreByTaskType(final List<String> tasks) {
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

  private void restoreTasks(final List<String> tasksLines, final String header,
      Consumer<Task> action) {
    for (String eachLine : tasksLines) {
      if (eachLine.equals(header) || eachLine.isBlank()) {
        continue;
      }

      Task taskToRestore = this.fromString(eachLine);

      if (TaskType.EPIC.equals(taskToRestore.getType())) {
        taskToRestore = epics.get(taskToRestore.getId());
      }
      action.accept(taskToRestore);
    }
  }

  private void setCounterLastUsed() {
    counter = Stream.concat(
            Stream.concat(this.tasks.keySet().stream(), this.epics.keySet().stream()),
            this.subtasks.keySet().stream())
        .max(Comparator.naturalOrder())
        .orElse(0);
  }

  private Task fromString(final String stringCsv) {
    Objects.requireNonNull(stringCsv);
    if (!Pattern.matches(TASKS_LINE_FORMAT, stringCsv)) {
      throw new IllegalArgumentException("Invalid CSV format of the line with task: " + stringCsv);
    }
    final String[] taskData = stringCsv.split(",");
    final int id = Integer.parseInt(taskData[0]);
    final TaskType type = TaskType.valueOf(taskData[1]);
    final String title = taskData[2];
    final TaskStatus status = TaskStatus.valueOf(taskData[3]);
    final String description = taskData[4];
    final Duration duration = Duration.ofMinutes(Long.parseLong(taskData[5]));
    final LocalDateTime startTime =
        taskData[6].equals("null") ? null : LocalDateTime.parse(taskData[6]);
    switch (type) {
      case TASK -> {
        final Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setDuration(duration);
        task.setStartTime(startTime);
        return task;
      }
      case EPIC -> {
        final Epic epic = new Epic();
        epic.setId(id);
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(status);
        epic.setDuration(duration);
        epic.setStartTime(startTime);
        return epic;
      }
      case SUBTASK -> {
        final Subtask subtask = new Subtask();
        subtask.setId(id);
        subtask.setTitle(title);
        subtask.setDescription(description);
        subtask.setStatus(status);
        subtask.setDuration(duration);
        subtask.setStartTime(startTime);
        subtask.setEpicId(Integer.parseInt(taskData[7]));
        return subtask;
      }
    }
    throw new IllegalArgumentException("Unknown task type: " + type);
  }

  private List<Task> getAllTasks() {
    return Stream.concat(
        Stream.concat(tasks.values().stream(), epics.values().stream()),
        subtasks.values().stream()
    ).toList();
  }
}