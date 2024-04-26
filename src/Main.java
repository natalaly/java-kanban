import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;
import ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.service.TaskManager;

/**
 * Use Case
 * 1. Create: Two tasks; Epic with three subtasks; Epic without subtask.
 * 2. Request (GET) the created tasks multiple times in different orders.
 * 3. Display the history view  and ensure that there are no duplicates.
 * 4. Delete: a task that exists in the history;
 *    Verify: it does not appear when printing history of viewing.
 * 5. Delete: epic with three subtasks;
 *    Verify: both the epic itself and all its subtasks are removed from the history.
 */
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
    tm = new InMemoryTaskManager();

    createTasksInTaskManager();

    getTasksInDifferentOrder();

    printWholeHistory();

    deleteTaskAndCheckHistory();

    deleteEpicWithSubtaskAndCheckHistory();

    System.out.println();
    System.out.println("Task2 in history:");
    System.out.println(tm.getHistory().get(1));

    System.out.println("Apply new Status for task2");
    Task t = tm.getTaskById(task2.getId());
    t.setStatus(TaskStatus.DONE);
    tm.updateTask(task2);

    System.out.println("This task in history:");
    System.out.println(tm.getHistory().get(1));

  }
  private static void deleteEpicWithSubtaskAndCheckHistory() {
    System.out.println();
    System.out.println("Deleting Epic1: ..." );
    tm.deleteEpic(epic1.getId());
    System.out.println("Should be in the following order:");
    System.out.println("Epic2 - Task2");
    System.out.println("Saved history:");
    tm.getHistory().forEach(t -> System.out.print(t.getTitle() + " - "));
    System.out.println();
  }

  private static void deleteTaskAndCheckHistory() {
    System.out.println();
    System.out.println("Deleting Task1: ..." );
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

  private static void prepareTestData(){
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
