import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.service.FileBackedTaskManager;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;


public class Main {

  private static TaskManager tm;
  private static Task task1;
  private static Task task2;
  private static Epic epic1;
  private static Subtask sb1;
  private static Subtask sb2;
  private static Subtask sb3;
  private static Epic epic2;

  public static void main(String[] args) {

    int choice = 2;

    switch (choice) {
      case 1 -> useCase1(); /* History Saving */
      case 2 -> useCaseFile(); /* File saving / restoring */
      case 3 -> sprint8Workshop();
    }

  }

  static void sprint8Workshop() {
//    System.out.println();
//    Task t = new Task() ;
//    t.setId(2);
//    t.setTitle("title");
//    t.setDescription("description");
//    t.setDuration(Duration.ofMinutes(15));
//    t.setStartTime(LocalDateTime.now());
//    System.out.println(t);

    epic1 = new Epic();
    epic1.setId(1);
    epic1.setTitle("Epic1");
    epic1.setDescription("Do e1");

    sb1 = new Subtask();
    sb1.setId(2);
    sb1.setTitle("Subtask1");
    sb1.setDescription("Subtask1 to the Epic1");
    sb1.setEpicId(1);
    sb1.setDuration(Duration.ofMinutes(5));
    sb1.setStartTime(LocalDateTime.now().plus(Duration.ofDays(4)));

    sb2 = new Subtask();
    sb2.setId(3);
    sb2.setTitle("Subtask2");
    sb2.setDescription("Subtask2 to the Epic1");
    sb2.setEpicId(1);
    sb2.setDuration(Duration.ofHours(15));
    sb2.setStartTime(LocalDateTime.now().plus(Duration.ofDays(1)));

    sb3 = new Subtask();
    sb3.setId(4);
    sb3.setTitle("Subtask3");
    sb3.setDescription("Subtask3 to the Epic1");
    sb3.setEpicId(1);
    sb3.setStartTime(LocalDateTime.now().plus(Duration.ofDays(2)));

    epic1.addSubtask(sb1);
    epic1.addSubtask(sb2);
    epic1.addSubtask(sb3);

    System.out.println(sb2.getEndTime());
    System.out.println(epic1.toCsvLine());
    System.out.println(epic1.toCsvLine().split(",").length);
    System.out.println(fromString("1,EPIC,Epic1,NEW,Do e1,905,2024-05-22T18:41:32.633049, "));
    System.out.println(epic1);
    System.out.println(LocalDateTime.parse("2024-05-22T18:48:57.235393"));


  }

  public static Task fromString(final String stringCsv) {
    Objects.requireNonNull(stringCsv);
    if (!Pattern.matches("^([^,\"']+,){7}[^,\"']+$", stringCsv)) {
      throw new IllegalArgumentException("Invalid CSV format of the line with task: " + stringCsv);
    }
    final String[] taskData = stringCsv.split(",");
    final int id = Integer.parseInt(taskData[0]);
    final TaskType type = TaskType.valueOf(taskData[1]);
    final String title = taskData[2];
    final TaskStatus status = TaskStatus.valueOf(taskData[3]);
    final String description = taskData[4];
    final Duration duration = Duration.ofMinutes(Long.parseLong(taskData[5]));
    final LocalDateTime startTime = LocalDateTime.parse(taskData[6]);
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

  /**
   * <h2> Use Case 2 </h2>
   * <ol>
   *   <li> GIVEN: A {@code FileBackedTaskManager oldManager} that has  3 Tasks, 2 Epics, and 2 Subtasks for one of the Epic.</li>
   *   <li> WHEN: A new {@code FileBackedTaskManager newManager} was created from the file of the oldManager.</li>
   *   <li> THEN: This new manager has all same  Tasks, Epics , and Subtasks in it. </li>
   * </ol>
   */
  static void useCaseFile() {
    /* GIVEN */
    /* Create tasks data: */
    Task t = new Task();
    t.setTitle("task");
    t.setDescription("t description");

    Epic e = new Epic();
    e.setTitle("epic");
    e.setDescription("null");
    System.out.println(e.getDescription() == null);

    Subtask sb = new Subtask();
    sb.setTitle("sbtask");
    sb.setDescription("st description");

    Path path = Path.of("resources/test/useCaseFile.csv");
    File temp = path.toFile();
    TaskManager oldManager = FileBackedTaskManager.loadFromFile(temp);
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
    sb.setDuration(Duration.ofMinutes(15));
    sb.setStartTime(LocalDateTime.now());
    oldManager.updateSubtask(sb); // update subtask2 of epic
    oldManager.getEpicById(2);

    /* WHEN */
    TaskManager newManage = FileBackedTaskManager.loadFromFile(temp);
    List<Integer> taskIds = new ArrayList<>();
    newManage.getAllTasks().forEach(a -> taskIds.add(a.getId()));
    List<Integer> epicIds = new ArrayList<>();
    newManage.getAllEpics().forEach(a -> epicIds.add(a.getId()));
    List<Integer> subtaskIds = new ArrayList<>();
    newManage.getAllSubtasks().forEach(a -> subtaskIds.add(a.getId()));

    /* THEN */
    System.out.println(
        "newManage.getAllTasks().size() should be 3: " + newManage.getAllTasks().size());
    System.out.println("Tasks ids should be 1,6,7 : " + taskIds);
    System.out.println(
        "newManage.getAllEpics().size() should be 2: " + newManage.getAllEpics().size());
    System.out.println("Epics ids should be 2, 4 : " + epicIds);
    System.out.println(
        "newManage.getAllSubtasks().size() should be 2: " + newManage.getAllSubtasks().size());
    System.out.println("Subtasks ids should 3, 5 : " + subtaskIds);
    System.out.println("newManage.getHistory() should have epic id=2: " + newManage.getHistory());

    System.out.println(newManage.getHistory().get(0).getDescription().length());

  }

  /**
   * <h2>Use Case 1 </h2>
   * <ol>
   *   <li>Create: Two tasks; Epic with three subtasks; Epic without subtask.</li>
   *   <li>Request ({@code .get...()} methods)the created tasks multiple times in different order.</li>
   *   <li>Display the history view  and ensure that there are no duplicates.</li>
   *   <li>Delete: a task that exists in the history; Verify: it does not appear when printing history of viewing.</li>
   *   <li>Delete: epic with three subtasks; Verify: both the epic itself and all its subtasks are removed from the history.</li>
   * </ol>
   * <p>
   */
  static void useCase1() {
    tm = new InMemoryTaskManager();
    createTasksInTaskManager();
    getTasksInDifferentOrder();
    printWholeHistory();
    deleteTaskAndCheckHistory();
    deleteEpicWithSubtaskAndCheckHistory();
  }

  private static void deleteEpicWithSubtaskAndCheckHistory() {
    System.out.println();
    System.out.println("Deleting Epic1: ...");
    tm.deleteEpic(epic1.getId());
    System.out.println("Should be in the following order:");
    System.out.println("Epic2 - Task2");
    System.out.println("Saved history:");
    tm.getHistory().forEach(t -> System.out.print(t.getTitle() + " - "));
    System.out.println();
  }

  private static void deleteTaskAndCheckHistory() {
    System.out.println();
    System.out.println("Deleting Task1: ...");
    tm.deleteTask(task1.getId());
    System.out.println("Should be in the following order:");
    System.out.println("Epic1 - Subtask2 - Epic2 - Subtask3 - Task2");
    System.out.println("Saved history:");
    tm.getHistory().forEach(t -> System.out.print(t.getTitle() + " - "));
    System.out.println();

  }

  private static void printWholeHistory() {
    System.out.println();
    System.out.println("Checking history..");
    System.out.println("Should be in the following order:");
    System.out.println("Task1 - Epic1 - Subtask2 - Epic2 - Subtask3 - Task2");
    System.out.println("Saved history:");
    tm.getHistory().forEach(t -> System.out.print(t.getTitle() + " - "));
    System.out.println();
    System.out.println(tm.getHistory());
    System.out.println();
  }

  private static void getTasksInDifferentOrder() {
    System.out.println("Get Task2:    " + tm.getTaskById(task2.getId()));
    System.out.println("Get Epic1:    " + tm.getEpicById(epic1.getId()));
    System.out.println("Get Task1:    " + tm.getTaskById(task1.getId()));
    System.out.println("Get Epic1:    " + tm.getEpicById(epic1.getId()));
    System.out.println("Get Subtask2: " + tm.getSubtaskById(sb2.getId()));
    System.out.println("Get Task2:    " + tm.getTaskById(task2.getId()));
    System.out.println("Get Epic2:    " + tm.getEpicById(epic2.getId()));
    System.out.println("Get Subtask3: " + tm.getSubtaskById(sb3.getId()));
    System.out.println("Get Task2:    " + tm.getTaskById(task2.getId()));
    System.out.println("Get Task2:    " + tm.getTaskById(task2.getId()));
    System.out.println();
  }

  private static void createTasksInTaskManager() {
    prepareTestData();
    tm.addTask(task1);
    tm.addTask(task2);

    tm.addEpic(epic1);
    int epic1Id = epic1.getId();
    sb1.setEpicId(epic1Id);
    sb2.setEpicId(epic1Id);
    sb3.setEpicId(epic1Id);
    tm.addSubtask(sb1);
    tm.addSubtask(sb2);
    tm.addSubtask(sb3);

    tm.addEpic(epic2);

    System.out.println("Tasks: ");
    System.out.println(tm.getAllTasks());
    System.out.println("Epics: ");
    System.out.println(tm.getAllEpics());
    System.out.println("Subtasks:");
    System.out.println(tm.getAllSubtasks());
    System.out.println();
  }

  private static void prepareTestData() {
    task1 = new Task();
    task1.setTitle("Task1");
    task1.setDescription("Do Task1");

    task2 = new Task();
    task2.setTitle("Task2");
    task2.setDescription("Do Task2");

    epic1 = new Epic();
    epic1.setTitle("Epic1");
    epic1.setDescription("Do e1");
    sb1 = new Subtask();
    sb1.setTitle("Subtask1");
    sb1.setDescription("Subtask1 to the Epic1");
    sb2 = new Subtask();
    sb2.setTitle("Subtask2");
    sb2.setDescription("Subtask2 to the Epic1");
    sb3 = new Subtask();
    sb3.setTitle("Subtask3");
    sb3.setDescription("Subtask3 to the Epic1");

    epic2 = new Epic();
    epic2.setTitle("Epic2");
    epic2.setDescription("Do e2");
  }

}
