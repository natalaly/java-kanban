package ru.yandex.practicum.tasktracker.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.yandex.practicum.tasktracker.exception.ManagerSaveException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;

/**
 * A task manager that extends the functionality of {@link InMemoryTaskManager} by providing
 * automatic <b>saving</b> and <b>loading</b> of data to and from a specified <b>file</b>.
 * <p>
 * The {@code FileBackedTaskManager} receives a file for auto-saving in its constructor and stores
 * it in a field. The {@link #save()} method saves the current state of the manager to the specified
 * file after each modifying operation.
 * <p>
 * In addition to the save method, there is a static method {@link #loadFromFile(File)} that
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

  private static final String TASKS_HEADER = "id,type,name,status,description,epic";
  private static final String HISTORY_HEADER = "history";
  private static final File DEFAULT_FILE = Path.of("tasksBackup.csv").toFile();
  private File file;
  private final String formatReg = "^(([^,\\\"\\'\\n]+),){5}([^,\\\"\\'\\n]+)$";

  public FileBackedTaskManager() {
    super();
    this.file = DEFAULT_FILE;
  }

  public FileBackedTaskManager(final File file) {
    this();
    this.file = file;
  }


  // TODO
  private void save() {
    try (final Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
      fileWriter.write(TASKS_HEADER + System.lineSeparator());
      for (Task task : getAllTasks()) {
        fileWriter.write(taskToCsvString(task));
      }
      for (Epic epic : getAllEpics()) {
        fileWriter.write(taskToCsvString(epic));
      }
      for (Subtask subtask : getAllSubtasks()) {
        fileWriter.write(taskToCsvString(subtask));
      }
      // save history
      fileWriter.write(HISTORY_HEADER + System.lineSeparator());
      for (Task historyTask : getHistory()) {
        fileWriter.write(taskToCsvString(historyTask));
      }
    } catch (IOException e) {
      throw new ManagerSaveException("An error occurred during saving to the file.", e);
    }
  }

  //  TODO restore TaskManager data from  the file when start the app
  public static FileBackedTaskManager loadFromFile(final File file) {
    final FileBackedTaskManager fb = new FileBackedTaskManager();
    final List<String> taskLines = new ArrayList<>();
    final List<String> historyLines = new ArrayList<>();
    List<String> destination = taskLines;
    try (final BufferedReader br = new BufferedReader(
        new FileReader(file.toPath().toFile(), StandardCharsets.UTF_8))) {
      while (br.ready()) {
        String line = br.readLine();
        if (line.equals(HISTORY_HEADER)) {
          destination = historyLines;
        }
        destination.add(line);
      }
    } catch (IOException e) {
      throw new RuntimeException("An Error occurred during reading the file", e);
    }

    if (taskLines.size() != 0) {
      /* restore all tasks to the maps */
      fb.restoreAll(taskLines, TASKS_HEADER);
    }
    if (historyLines.size() != 0) {
      /* restore history */
      fb.restoreAll(historyLines, HISTORY_HEADER);
    }
    /* reseating the counter */
    final Set<Integer> allIds = new HashSet<>(fb.tasks.keySet());
    allIds.addAll(fb.epics.keySet());
    allIds.addAll(fb.subtasks.keySet());
    final Integer maxId = allIds.stream().max(Comparator.naturalOrder()).orElse(0);
    counter = maxId;
    return fb;
  }

  private void restoreAll(final List<String> tasks, final String header) {
    switch (header) {
      case TASKS_HEADER -> restoreTasks(tasks);
      case HISTORY_HEADER -> restoreHistory(tasks);
    }
  }

  private void restoreTasks(final List<String> tasks) {
    for (String eachLine : tasks) {
      if (eachLine.equals(TASKS_HEADER) || eachLine.isBlank()) {
        continue;
      }
      final Task taskFromFile = this.fromString(eachLine);
      final TaskType typeFromFile = TaskType.getType(taskFromFile);
      switch (typeFromFile) {
        case TASK -> {
          this.tasks.put(taskFromFile.getId(), taskFromFile);
          break;
        }
        case EPIC -> {
          this.epics.put(taskFromFile.getId(), (Epic) taskFromFile);
          break;
        }
        case SUBTASK -> {
          this.subtasks.put(taskFromFile.getId(), (Subtask) taskFromFile);
          int epicId = ((Subtask) taskFromFile).getEpicId();
          this.epics.get(epicId).addSubtask((Subtask) taskFromFile);
          break;
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
      if (TaskType.EPIC.equals(TaskType.getType(taskToRestore))) {
        taskToRestore = epics.get(taskToRestore.getId());
      }
      historyManager.add(taskToRestore);
    }
  }

  //  TODO NullPointerException - for now toStringCSV is used only in save() where task is never null
  private String taskToCsvString(final Task task) {
    return String.format("%s,%s,%s,%s,%s,%s" + System.lineSeparator(),
        task.getId(),
        TaskType.getType(task),
        task.getTitle(),
        task.getStatus(),
        task.getDescription(),
        (TaskType.getType(task) == TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : " ");
  }

  public Task fromString(final String stringCSV) {
    if (stringCSV == null || stringCSV.isBlank() || !stringCSV.matches(formatReg)) {
      return null;
    }
    final String[] taskData = stringCSV.split(",");
    int id = Integer.parseInt(taskData[0]);
    final TaskType type = TaskType.valueOf(taskData[1]);
    final String title = taskData[2];
    final TaskStatus status = TaskStatus.valueOf(taskData[3]);
    final String description = taskData[4];
    final int epicId =
        (taskData[5] == null || taskData[5].isBlank()) ? 0 : Integer.parseInt(taskData[5]);
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

  /**
   * <h2>Use Case File</h2>
   * <ol>
   *   <li> GIVEN: A {@code FileBackedTaskManager oldManager} that has  3 Tasks, 2 Epics, and 2 Subtasks for one of the Epic.</li>
   *   <li> WHEN: A new {@code FileBackedTaskManager newManager} was created from the file of the oldManager.</li>
   *   <li> THEN: This new manager has all same  Tasks, Epics , and Subtasks in it. </li>
   * </ol>
   *
   */
  public static void main(String[] args) {
    /* GIVEN */
    /* Create tasks data: */
    Task t = new Task();
    t.setTitle("task");
    t.setDescription("t description");

    Epic e = new Epic();
    e.setTitle("epic");
    e.setDescription("e description");

    Subtask sb = new Subtask();
    sb.setTitle("sbtask");
    sb.setDescription("st description");

    Path path = Path.of("useCaseFile.csv");
    FileBackedTaskManager oldManager = new FileBackedTaskManager(path.toFile());
    oldManager.addTask(t); // add task
    oldManager.addEpic(e); // add epic
    sb.setEpicId(2);
    oldManager.addSubtask(sb); // add subtask to epic
    e.setTitle("epic2");
    oldManager.addEpic(e); // add epic2
    sb.setTitle("sbtask2");
    oldManager.addSubtask(sb); // add subtask2 to epic
    t.setTitle("task2");
    oldManager.addTask(t); // add task2
    t.setTitle("task3");
    oldManager.addTask(t); // add task3
    sb.setStatus(TaskStatus.DONE);
    oldManager.updateSubtask(sb); // update subtask2 of epic
    oldManager.getEpicById(2);

    /* WHEN */
    FileBackedTaskManager newManage = FileBackedTaskManager.loadFromFile(path.toFile());
    List<Integer> taskIds = new ArrayList<>();
    newManage.getAllTasks().forEach(a ->taskIds.add(a.getId()));
    List<Integer> epicIds = new ArrayList<>();
    newManage.getAllEpics().forEach(a -> epicIds.add(a.getId()));
    List<Integer> subtaskIds = new ArrayList<>();
    newManage.getAllSubtasks().forEach(a -> subtaskIds.add(a.getId()));

    /* THEN */
    System.out.println("newManage.getAllTasks().size() should be 3: " + newManage.getAllTasks().size());
    System.out.println("Tasks ids should be 1,6,7 : " + taskIds);
    System.out.println("newManage.getAllEpics().size() should be 2: " + newManage.getAllEpics().size());
    System.out.println("Epics ids should be 2, 4 : " + epicIds);
    System.out.println("newManage.getAllSubtasks().size() should be 2: " + newManage.getAllSubtasks().size());
    System.out.println("Subtasks ids should 3, 5 : " + subtaskIds);
    System.out.println("newManage.getHistory() should have epic id=2: " + newManage.getHistory());

  }
}
