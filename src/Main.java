import java.util.ArrayList;

/**
 * Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
 * Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
 * Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
 * И, наконец, попробуйте удалить одну из задач и один из эпиков.
 * Воспользуйтесь дебаггером среды разработки, чтобы понять логику работы программы и отладить её.
 */
public class Main {

  //Test data
  static Task task1 = new Task("Task 1","description task 1");
  static Task task2 = new Task("Task 2","description task 2");
  static Epic epic1 = new Epic("Epic 1", "Description For Epic 1");
  static Subtask subtask11 = new Subtask("Subtask 1-epic 1", "description to subtask 1 ,epic 1");
  static Subtask subtask12 = new Subtask("Subtask 2-epic 1", "description to subtask 2 ,epic 1");
  static Epic epic2 = new Epic("Epic 2", "Description For Epic 2");
  static Subtask subtask21 =  new Subtask("Subtask 1-epic 2", "description to subtask 1 ,epic 2");

  public static void main(String[] args) {
    System.out.println("Поехали!");

    TaskManager.addTask(task1);
    TaskManager.addTask(task2);
    TaskManager.addEpic(epic1);
    TaskManager.addEpic(epic2);

    System.out.println("\"getAllTasks()\"");
    ArrayList<Task> taskList = (ArrayList<Task>)TaskManager.getAllTasks();
    System.out.println(taskList);

    System.out.println("\"getAllEpics()\"");
    ArrayList<Epic> epicList = (ArrayList<Epic>)TaskManager.getAllEpics();
    System.out.println(epicList);

    System.out.println("\"getById(int id)\" for Task");
    System.out.println(TaskManager.getTaskById(1));

    System.out.println("\"getById(int id)\" for Epic");
    Epic e = TaskManager.getEpicById(3);
    System.out.println(e);
    e.setTitle("qwewe");
    System.out.println(TaskManager.getAllEpics());
    System.out.println(TaskManager.getAllTasks());








//    System.out.println();
//    System.out.println("We have:");
//    System.out.println(task1.getTitle() + task1);
//    System.out.println(task2.getTitle() + task2);

//    testAddNewTask();

//    testGetAllTasks();

//    testGetById();

//    testClearTasks();

//    testUpdateTask();

//    deleteTaskById();










  }






  public static void testAddNewTask(){
    System.out.println();
    System.out.println("Testing TaskManager.addTask(): add task1 and task2 to the tracker.");
    TaskManager.addTask(task1);
    TaskManager.addTask(task2);
    System.out.println(task1 + " - verify, task object obtained new id ");
    System.out.println(task2 + " - verify, task object obtained new id ");
    System.out.println("Tasks in the tracker: ");
    System.out.println(TaskManager.getAllTasksPretty());
  }

  public static void testGetAllTasks(){
    System.out.println();
    System.out.println("Testing TaskManager.getAllTasks(): Get all tasks from the tracker:");
    ArrayList<Task> taskList = (ArrayList<Task>) TaskManager.getAllTasks();
    System.out.println(taskList);
  }

  public static void testGetById(){
    System.out.println();
    System.out.println("Testing TaskManager.getById(): Get task by id 1:");
    System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    int id = 1;
    if(TaskManager.getAllTasks().size() == 0){
      System.out.println(" TaskManager.getById(1) : " + TaskManager.getTaskById(id));
      System.out.println("Add some tasks and try again.");
      TaskManager.addTask(task1);
      TaskManager.addTask(task2);
      System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    }
    System.out.println(" TaskManager.getById(1) : " + TaskManager.getTaskById(id));

  }

  public static void testClearTasks(){
    System.out.println();
    System.out.println("Testing TaskManager.clearTasks(): Clear all tasks");
    if(TaskManager.getAllTasks().size() == 0){
      TaskManager.addTask(task1);
      TaskManager.addTask(task2);
    }
    System.out.println("Tasks in the tracker before: \n " + TaskManager.getAllTasksPretty());
    TaskManager.clearTasks();
    System.out.println("Tasks in the tracker after: " + TaskManager.getAllTasksPretty());
  }

  public static void testUpdateTask(){
    System.out.println();
    System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    System.out.println("Testing TaskManager.updateTask(): Update task2 with  Status = IN_PROGRESS: ");
    System.out.println("1. Changes the object itself:");
    task2.setStatus(Status.IN_PROGRESS);
    System.out.println(task2);
    if(TaskManager.getAllTasks().size() == 0){
      System.out.println("2. While task2 in the tracker before updating: " + TaskManager.getTaskById(2));
      System.out.println("3. Apply updateTask(task2,2), update task2 in the tracker:");
      TaskManager.updateTask(task2);
      System.out.println(TaskManager.updateTask(task2));

      System.out.println("Add some tasks and try again.");
      task2.setStatus(Status.NEW);
      TaskManager.addTask(task1);
      TaskManager.addTask(task2);
      System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    }
    task2.setStatus(Status.IN_PROGRESS);
    System.out.println("3. Apply updateTask(Task task), update task2 in the tracker:");
    System.out.println(TaskManager.updateTask(task2));
    System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
  }

  public static void deleteTaskById() {
    System.out.println();
    System.out.println("Testing TaskManager.deleteTaskById(Id id): delete task with id 1: ");
    System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    if(TaskManager.getAllTasks().size() == 0){
      TaskManager.deleteById(1);
      System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
      System.out.println("Add some tasks and try again.");
      TaskManager.addTask(task1);
      TaskManager.addTask(task2);
      System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
    }
    TaskManager.deleteById(1);
    System.out.println("Tasks in the tracker: \n " + TaskManager.getAllTasksPretty());
  }

}
