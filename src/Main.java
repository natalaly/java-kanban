import java.util.ArrayList;
import java.util.List;

/**
 * Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
 * Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
 * Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
 * И, наконец, попробуйте удалить одну из задач и один из эпиков.
 * Воспользуйтесь дебаггером среды разработки, чтобы понять логику работы программы и отладить её.
 */
public class Main {
  //Test data
  static Task task1 = new Task("Task 1", "description task 1");
  static Task task2 = new Task("Task 2", "description task 2");
  static Epic epic1 = new Epic("Epic 1", "Description For Epic 1");
  static Subtask subtask11 = new Subtask("Subtask 1-epic 1", "description to subtask 1 ,epic 1");
  static Subtask subtask12 = new Subtask("Subtask 2-epic 1", "description to subtask 2 ,epic 1");
  static Epic epic2 = new Epic("Epic 2", "Description For Epic 2");
  static Subtask subtask21 = new Subtask("Subtask 1-epic 2", "description to subtask 1 ,epic 2");
  static TaskManager t = new TaskManager();

  public static void main(String[] args) {










  }

  static void testAddTask() {
    int tasksNumBeforeAdding = t.getAllTasks().size();
    Task task = t.addTask(task1);
    int taskNumAfterAdding = t.getAllTasks().size();
    System.out.println("Test create new task on the tracker ( addTask() ): ");
    System.out.println(tasksNumBeforeAdding < taskNumAfterAdding ? "PASS" : "FAILED");
    System.out.println("Task added: " + t.getTaskById(task.getId()));
    System.out.println();
  }

  static void testAddEpic() {
    int epicsNumBeforeAdding = t.getAllEpics().size();
    Epic epic = t.addEpic(epic1);
    int epicsNumAfterAdding = t.getAllEpics().size();
    System.out.println("Test create new Epic on the tracker ( addEpic() ): ");
    System.out.println(epicsNumBeforeAdding < epicsNumAfterAdding ? "PASS" : "FAILED");
    System.out.println("Epic added: " + t.getEpicById(epic.getId()));
    System.out.println();
  }

  static void testAddEpicWithSubtasks() {
    int epicsNumBeforeAdding = t.getAllEpics().size();
    System.out.println(t.getAllEpics());
    Epic epicToAdd = new Epic("EpicWithSubtasks", "Has one subtask with status Done");
    Subtask subtaskWithEpic = new Subtask("Subtask for EpicWithSubtasks", "Some Description");
    subtaskWithEpic.setStatus(Status.DONE);
    Subtask subtaskWithEpic2 = new Subtask("Subtask 2 for EpicWithSubtasks", "Some Description");
    epicToAdd.setSubtasks(new ArrayList<>(List.of(subtaskWithEpic, subtaskWithEpic2)));

    Epic epic = t.addEpic(epicToAdd);
    int epicsNumAfterAdding = t.getAllEpics().size();

    System.out.print("Test create new Epic with subtasks inside on the tracker: ");
    System.out.println(epicsNumBeforeAdding < epicsNumAfterAdding ? "PASS" : "FAILED");
    System.out.println("Epic added has status IN PROGRESS: \n" + (t.getEpicById(epic.getId()).getStatus() == Status.IN_PROGRESS ? "PASS" : "FAILED"));
    System.out.println("Added Epic: " + t.getEpicById(epic.getId()));
    System.out.println("Subtasks for this epic (getSubtasksOfEpic()) : " + t.getSubtasksOfEpic(epic.getId()));
    System.out.println("All subtasks in the tracker: " + t.getAllSubtasks());
    System.out.println();
  }

  static void testAddSubtask() {
    int epicId = t.addEpic(epic1).getId();
    int subtasksNumBeforeAdding = t.getAllSubtasks().size();
    t.addSubtask(subtask11, epicId);
    int taskNumAfterAdding = t.getAllSubtasks().size();

    System.out.println("Test create new subtask on the tracker: \n" + (subtasksNumBeforeAdding < taskNumAfterAdding ? "PASS" : "FAILED"));
    System.out.println("Subtask was  added to the epic: " + t.getEpicById(epicId));
    System.out.println("Subtasks for this epic: " + t.getSubtasksOfEpic(epicId));
    System.out.println("All subtasks in the tracker total: " + t.getAllSubtasks());
    System.out.println();
  }

  static void testChangingTaskStatus() {
    addAll();
    System.out.println("Validation updateTask() method ...");
    String result1 = "";
    String result2 = "";
    Task someTask = new Task(task1.getTitle(), task1.getDescription());
    someTask.setStatus(Status.DONE);
    t.updateTask(someTask);
    for (Task task : t.getAllTasks()) {
      if (task.getId() == 0 && task.getStatus() == Status.DONE) {
        result1 = "FAIL";
      }
      result1 = "PASS";
    }
    System.out.println("Test 1 : Updating task 1 with New task, with status DONE: \n" + result1);

    someTask.setId(task1.getId());
    t.updateTask(someTask);
    if (t.getTaskById(task1.getId()).getStatus() == Status.DONE) {
      result2 = "PASS";
    } else {
      result2 = "FAIL";
    }
    System.out.println("Test 2 : Updating task 1 with status DONE: \n" + result2);
    clearAll();
  }

  static void testChangeSubtaskStatus() {
    addAll();
    System.out.println("Changing status of subtask 11 from epic 1 into Done. ");
    Subtask updatingData = new Subtask(subtask11.getTitle(), subtask11.getDescription());
    updatingData.setId(subtask11.getId());
    updatingData.setEpicId(subtask11.getEpicId());
    updatingData.setStatus(Status.DONE);

    t.updateSubtask(updatingData);

    System.out.println("Subtask11 in the tracker has status DONE: ");
    System.out.println(t.getSubtaskById(subtask11.getId()).getStatus() == Status.DONE ? "PASS" : "FAIL");
    System.out.println("Epic 1 has status IN_PROGRESS:  ");
    System.out.println(t.getEpicById(subtask11.getEpicId()).getStatus() == Status.IN_PROGRESS ? "PASS" : "FAIL");
    clearAll();
  }

  static void testUpdatingEpicAfterDeletingSubtask() {
    addAll();
    Subtask subtask = new Subtask("Subtask To Delete", "Will be deleted soon");
    subtask.setId(7);
    subtask.setEpicId(4);
    subtask.setStatus(Status.IN_PROGRESS);
    System.out.println(subtask);
    t.updateSubtask(subtask);
    int epicId = t.getSubtaskById(subtask.getId()).getEpicId();
    System.out.println("Subtask to delete : " + t.getSubtaskById(subtask.getId()));
    System.out.println("From epic: " + t.getEpicById(epicId));

    System.out.println("Deleting subtask with id 7..");
    t.deleteById(subtask.getId());
    System.out.println("Subtask with id = 7 deleted: ");
    System.out.println(t.getSubtaskById(subtask.getId()) == null ? "PASS" : "FAIL");
    System.out.println("Status of the epic changed into NEW: ");
    System.out.println(t.getEpicById(epicId).getStatus() == Status.NEW ? "PASS" : "FAIL");
    System.out.println("All subtasks left: " + t.getAllSubtasks());
    System.out.println("Corresponding epic after:" + t.getEpicById(epicId));
    clearAll();
  }

  static void testDeleteTask() {
    addAll();
    System.out.println("Deleting task with id 2..");
    t.deleteById(2);
    System.out.println("Task with id = 2 deleted : ");
    System.out.println(t.getTaskById(2) == null ? "PASS" : "FAIL");
    System.out.println("All tasks left: " + t.getAllTasks());
    clearAll();
  }

  static void testDeleteSubtask() {
    addAll();
    System.out.println("Deleting subtask with id 6..");
    int epicId = t.getSubtaskById(6).getEpicId();
    System.out.println("Corresponding epic before:" + t.getEpicById(epicId));
    t.deleteById(6);
    System.out.println("Subtask with id = 6 deleted: ");
    System.out.println(t.getSubtaskById(6) == null ? "PASS" : "FAIL");
    System.out.println("All subtasks left: " + t.getAllSubtasks());
    System.out.println("Corresponding epic before:" + t.getEpicById(epicId));
    clearAll();
  }

  static void testDeleteEpic() {
    addAll();
    System.out.println("Test deleting epic with index 3...");
    t.deleteById(3);
    boolean deleted = true;
    for (Subtask subtask : t.getAllSubtasks()) {
      if (subtask.getEpicId() == 3) {
        deleted = false;
      }
    }
    System.out.println("Epic with id = 3 was deleted: ");
    System.out.println(t.getEpicById(3) == null ? "PASS" : "FAIL");
    System.out.println("Corresponding subtasks were deleted from the tracker: ");
    System.out.println(deleted ? "PASS" : "FAIL");
    System.out.println("All epics left: " + t.getAllEpics());
    System.out.println("All subtasks left: " + t.getAllSubtasks());
    clearAll();
  }

  static void printAllTestData() {
    System.out.println("Our test Data: ");
    System.out.println(task1);
    System.out.println(task2);
    System.out.println(epic1);
    System.out.println(subtask11);
    System.out.println(subtask12);
    System.out.println(epic2);
    System.out.println(subtask21);
    System.out.println("##############################################################################");
  }

  static void addAll() {
    System.out.println();
    System.out.println("Adding tasks,epics, and subtasks into the tracker....");
    t.addTask(task1);
    t.addTask(task2);
    Epic epicAdded1 = t.addEpic(epic1);
    Epic epicAdded2 = t.addEpic(epic2);
    int epicId1 = epicAdded1.getId();
    t.addSubtask(subtask11, epicId1);
    t.addSubtask(subtask12, epicId1);
    int epicId2 = epicAdded2.getId();
    t.addSubtask(subtask21, epicId2);
  }

  static void printAll() {
    System.out.println("Print all tasks: ");
    System.out.println(t.getAllTasks());
    System.out.println("Print all epics: ");
    System.out.println(t.getAllEpics());
    System.out.println("Print all subtasks: ");
    System.out.println(t.getAllSubtasks());
    System.out.println();
  }

  static void clearAll() {
    System.out.println("Clear tasks...");
    t.clearTasks();
    System.out.println("Clear subtasks...");
    t.clearSubtasks();
    System.out.println("Clear epics...");
    t.clearEpics();
    System.out.println();
  }

}
