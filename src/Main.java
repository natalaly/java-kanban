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
  private static Task task3;
  private static Epic epic1;
  private static Subtask sb1;
  private static Subtask sb2;
  private static Subtask sb3;
  private static Epic epic2;
  private static LocalDateTime time1 = LocalDateTime.now();
  private static LocalDateTime time2 = time1.plusMinutes(20);

  public static void main(String[] args) {

    int choice = 2;

    switch (choice) {
      case 1 -> useCase1(); /* History Saving */
      case 2 -> useCaseFile(); /* File saving / restoring */
    }

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
    task1 = new Task();
    task1.setTitle("task");
    task1.setDescription("t description");

    task2 = new Task();
    task2.setTitle("task2");
    task2.setDescription("t2");
    task2.setDuration(Duration.ofMinutes(20));
    task2.setStartTime(LocalDateTime.of(2024,05,21,12,20,00));

    task3 = new Task();
    task3.setTitle("task3");
    task3.setDescription("t3");
    task3.setDuration(Duration.ofMinutes(20));
    task3.setStartTime(LocalDateTime.of(2024,05,21,12,00,00));

    epic1 = new Epic();
    epic1.setTitle("epic");
    epic1.setDescription("null");

    epic2 =  new Epic();
    epic2.setTitle("epic2");
    epic2.setDescription("null");

    sb1 = new Subtask();
    sb1.setTitle("sbtask");
    sb1.setDescription("st description");

    sb2 = new Subtask();
    sb2.setTitle("sbtask2");
    sb2.setDescription("st2 description");

    Path path = Path.of("resources/test/useCaseFile.csv");
    File temp = path.toFile();
    TaskManager oldManager = FileBackedTaskManager.loadFromFile(temp);

    oldManager.addTask(task1); // add task -1
    oldManager.addEpic(epic1); // add epic -2
    sb1.setEpicId(epic1.getId());
    oldManager.addSubtask(sb1); // add subtask to epic -3
    oldManager.addEpic(epic2); // add epic2-4
    sb2.setEpicId(epic1.getId());
    oldManager.addSubtask(sb2); // add subtask2 to epic-5
    oldManager.addTask(task2); // add task2-6
    oldManager.addTask(task3); // add task3 -7

    sb2.setStatus(TaskStatus.DONE);
    sb2.setDuration(Duration.ofMinutes(15));
    sb2.setStartTime(time2);
    oldManager.updateSubtask(sb2); // update subtask2 of epic

    oldManager.getEpicById(2);

    System.out.println("GIVEN: oldManager.getPrioritizedTasks():" );
    oldManager.getPrioritizedTasks().forEach(System.out::println);
    System.out.println();

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
    System.out.println("newManage.getHistory() should have epic id=2: ");
    newManage.getHistory().forEach(System.out::println);

    System.out.println("THEN: newManage.getPrioritizedTasks():" );
    newManage.getPrioritizedTasks().forEach(System.out::println);

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
