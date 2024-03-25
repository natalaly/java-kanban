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
  static TaskManager t = new TaskManager();

  public static void main(String[] args) {
    TaskManager t = new TaskManager();
    System.out.println("We have: ");
    System.out.println(task1);
    System.out.println(task2);
    System.out.println(epic1);
    System.out.println(subtask11);
    System.out.println(subtask12);
    System.out.println(epic2);
    System.out.println(subtask21);
    System.out.println("###############################################################################################");
    System.out.println();

    System.out.println("Added tasks,epics, and subtasks into the tracker:");
    System.out.println("---------------------------------------");
    Task taskAdded1 = t.addTask(task1); //id=1
    Task taskAdded2 = t.addTask(task2); //id=2
    Epic epicAdded1 = t.addEpic(epic1); //id=3
    Epic epicAdded2 =t.addEpic(epic2); //id=4
    int epicId1 = epicAdded1.getId();
    t.addSubtask(subtask11,epicId1); //id=5
    t.addSubtask(subtask12, epicId1); //id=6
    int epicId2 = epicAdded2.getId();
    t.addSubtask(subtask21,epicId2); //id=7

    System.out.println("Print Task, Epic and Subtask lists:");
    System.out.println(t.getAllTasks());
    System.out.println(t.getAllEpics());
    System.out.println(t.getAllSubtasks());
    System.out.println();

    System.out.println("Change status of the task1 - IN_PROGRESS");
    System.out.println("---------------------------------------");
    taskAdded1.setStatus(Status.IN_PROGRESS);
    t.updateTask(taskAdded1);
    System.out.println(t.getTaskById(1));
    System.out.println();

    System.out.println("Change status of the subtask 11 - IN_PROGRESS");
    System.out.println("---------------------------------------");
    System.out.println("Before:");
    System.out.println(t.getAllSubtasks());
    System.out.println(t.getAllEpics());
    Subtask subtaskUpdated =  t.getSubtaskById(5);
    subtaskUpdated.setStatus(Status.DONE);
    t.updateSubtask(subtaskUpdated);
    System.out.println("After:");
    System.out.println(t.getAllSubtasks());
    System.out.println(t.getSubtasksOfEpic(3));
    System.out.println(t.getAllEpics());
    System.out.println();

    System.out.println("Delete 2nd subtask from epic 1");
    System.out.println("---------------------------------------");
    System.out.println("epic 1 before deleting subtask: \n" + t.getEpicById(3));
    System.out.println("all subtasks of epic 1 before deleting: \n" + t.getSubtasksOfEpic(3));
    t.deleteById(6);

    System.out.println("epic 1 after deleting subtask: \n" + t.getEpicById(3));
    System.out.println("All epics after deleting : \n" + t.getAllEpics());
    System.out.println("all subtasks in the tracker after deleting: \n " + t.getAllSubtasks());




//    addTaskTest();
//    addEpicTest();
  }

  static void addTaskTest(){
    int tasksNumBeforeAdding = t.getAllTasks().size();
    Task task = t.addTask(task1);
    int taskNumAfterAdding = t.getAllTasks().size();
    String testResult = tasksNumBeforeAdding < taskNumAfterAdding? "PASS" : "FAILED";
    System.out.println("Test create new task on the tracker ( addTask() ): " + testResult);
    System.out.println("Task added: " + task);
  }

  static void addEpicTest(){
    int epicsNumBeforeAdding = t.getAllEpics().size();
    System.out.println(t.getAllEpics());
    Epic epic = t.addEpic(epic1);
    int epicsNumAfterAdding = t.getAllEpics().size();
    String testResult = epicsNumBeforeAdding < epicsNumAfterAdding? "PASS" : "FAILED";
    System.out.println("Test create new Epic on the tracker ( addEpic() ): " + testResult);
    System.out.println("Epic added: " + epic);
  }
//  static void addSubtaskTest(){
//    int subtasksNumBeforeAdding = t.getAllSubtasks().size();
//    t.addSubtask(subtask11);
//    int taskNumAfterAdding = t.getAllTasks().size();
//    String testResult = subtasksNumBeforeAdding < taskNumAfterAdding? "PASS" : "FAILED";
//    System.out.println("Test create new task on the tracker ( addTask() ): " + testResult);
//    System.out.println("Task added: " + subtask);
//  }

}
