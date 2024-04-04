import main.java.ru.yandex.practicum.tasktracker.service.Managers;
import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.TaskStatus;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;
import main.java.ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;

import java.util.List;


public class Main {
  //Test data
  static Task task1 = new Task();
  static Task task2 = new Task();
  static Epic epic1 = new Epic();
  static Subtask subtask11 = new Subtask();
  static Subtask subtask12 = new Subtask();
  static Epic epic2 = new Epic();
  static Subtask subtask21 = new Subtask();
  static TaskManager t ;

  public static void main(String[] args) {
    t = Managers.getDefault(); //start task tracker

    checkHowHistoryWorks();

//    test1AddTask();
//    test2AddEpic();
//    test3AddEpicWithSubtasks();
//    test4AddSubtask();
//    test5ChangingTaskStatus();
//    test6ChangeSubtaskStatus();
//    test7AutoUpdatingEpicAfterDeletingSubtask();
//    test8DeleteTask();
//    test9DeleteSubtask();
//    test10DeleteEpic();


  }

  static void test1AddTask() {
    System.out.println("Test 1 : add new task to the tracker.");
    System.out.println("---------------------------------------");
    int tasksNumBeforeAdding = t.getAllTasks().size();
    Task task = t.addTask(task1);
    int taskNumAfterAdding = t.getAllTasks().size();
    System.out.println("Test result: \n" + (tasksNumBeforeAdding < taskNumAfterAdding ? "PASS" : "FAILED"));
    System.out.println();
  }

  static void test2AddEpic() {
    System.out.println("Test 2 : add new epic to the tracker.");
    System.out.println("---------------------------------------");
    int epicsNumBeforeAdding = t.getAllEpics().size();
    Epic epic = t.addEpic(epic1);
    int epicsNumAfterAdding = t.getAllEpics().size();
    System.out.println("Test result: \n" + (epicsNumBeforeAdding < epicsNumAfterAdding ? "PASS" : "FAILED"));
    System.out.println();
  }

  static void test3AddEpicWithSubtasks() {
    System.out.println("Test 3 : add new epic with subtasks in it  to the tracker.");
    System.out.println("---------------------------------------");
    int epicsNumBeforeAdding = t.getAllEpics().size();

    Epic epicToAdd = new Epic();
    epicToAdd.setTitle("EpicWithSubtasks");
    epicToAdd.setDescription("Has two subtasks with status DONE and NEW");

    Subtask subtaskForEpic = new Subtask();
    subtaskForEpic.setTitle("Subtask for EpicWithSubtasks");
    subtaskForEpic.setDescription("Some Description");
    subtaskForEpic.setStatus(TaskStatus.DONE);

    Subtask subtaskForEpic2 = new Subtask();
    subtaskForEpic2.setTitle("Subtask 2 for EpicWithSubtasks");
    subtaskForEpic2.setDescription("Some Description");

    epicToAdd.addSubtask(subtaskForEpic);
    epicToAdd.addSubtask(subtaskForEpic2);
//    epicToAdd.setSubtasks(new ArrayList<>(List.of(subtaskForEpic, subtaskForEpic2)));


    Epic epic = t.addEpic(epicToAdd);
    int epicsNumAfterAdding = t.getAllEpics().size();
    boolean isEpicAdded = epicsNumBeforeAdding < epicsNumAfterAdding;
    boolean isEpicStatusExpected = t.getEpicById(epic.getId()).getStatus() == TaskStatus.DONE;
    boolean areSubtasksAddedToEpic = !t.getSubtasksByEpicId(epic.getId()).isEmpty();
    boolean areSubtasksAddedToTracker = t.getAllSubtasks().containsAll(t.getSubtasksByEpicId(epic.getId()));

    System.out.println("Test result: \n" +
        (isEpicAdded && isEpicStatusExpected && areSubtasksAddedToEpic && areSubtasksAddedToTracker ? "PASS" : "FAILED"));
    System.out.println();
  }

  static void test4AddSubtask() {
    System.out.println("Test 4 : add new subtask to the tracker.");
    System.out.println("---------------------------------------");
    epic1.setTitle("Epic1");
    epic1.setDescription("TODO Epic1");
    int epicId = epic1.getId();
    subtask11.setEpicId(epicId);
    subtask11.setTitle("Subtask1 to Epic1");
    subtask11.setDescription("Some Description");
    subtask11.setStatus(TaskStatus.NEW);
    int subtasksNumBeforeAdding = t.getAllSubtasks().size();
    t.addSubtask(subtask11);
    int taskNumAfterAdding = t.getAllSubtasks().size();
    boolean subtaskAddedToTracker = subtasksNumBeforeAdding < taskNumAfterAdding;
    boolean subtaskAddedToEpic = t.getSubtasksByEpicId(epicId).contains(subtask11);
    boolean isEpicStatusCorrect = t.getEpicById(epicId).getStatus() == TaskStatus.NEW;
    boolean result = subtaskAddedToTracker && subtaskAddedToEpic && isEpicStatusCorrect;
    System.out.println("Test result: \n" + (result ? "PASS" : "FAILED"));
    clearAll();
    System.out.println();
  }

  static void test5ChangingTaskStatus() {
    System.out.println("Test 5 : Change status for task.");
    System.out.println("---------------------------------------");
    addAll();
    boolean canNotUpdateWithNewTask = false;
    boolean isUpdatedTask = false;
    Task someTask = new Task();
    someTask.setTitle(task1.getTitle());
    someTask.setDescription(task1.getDescription());
    someTask.setStatus(TaskStatus.DONE);
    t.updateTask(someTask);
    for (Task task : t.getAllTasks()) {
      if (task.getId() == 0 && task.getStatus() == TaskStatus.DONE) {
        canNotUpdateWithNewTask = false;
        break;
      }
      canNotUpdateWithNewTask = true;
    }
    someTask.setId(task1.getId());
    t.updateTask(someTask);
    if (t.getTaskById(task1.getId()).getStatus() == TaskStatus.DONE) {
      isUpdatedTask = true;
    } else {
      isUpdatedTask = false;
    }
    System.out.println("Test result: ");
    System.out.println(canNotUpdateWithNewTask && isUpdatedTask ? "PASS" : "FAIL");
    clearAll();
  }

  static void test6ChangeSubtaskStatus() {
    System.out.println("Test 6 : Change status for subtask.");
    System.out.println("---------------------------------------");
    addAll();
    int idSubtaskToUpdate = subtask11.getId();
    int epicId = t.getSubtaskById(idSubtaskToUpdate).getEpicId();

    Subtask updatingData = new Subtask();
    updatingData.setTitle(subtask11.getTitle());
    updatingData.setDescription(subtask11.getDescription());
    updatingData.setId(t.getSubtaskById(idSubtaskToUpdate).getId());
    updatingData.setEpicId(epicId);
    updatingData.setStatus(TaskStatus.DONE);

    t.updateSubtask(updatingData);

    boolean isSubtaskStatusUpdated = t.getSubtaskById(idSubtaskToUpdate).getStatus() == TaskStatus.DONE;
    boolean isEpicStatusUpdated = t.getEpicById(epicId).getStatus() == TaskStatus.IN_PROGRESS;
    System.out.println("Test result: ");
    System.out.println(isSubtaskStatusUpdated && isEpicStatusUpdated ? "PASS" : "FAIL");
    clearAll();
  }

  static void test7AutoUpdatingEpicAfterDeletingSubtask() {
    System.out.println("Test 7 : auto updating epic status after subtask has been deleted.");
    System.out.println("---------------------------------------");
    addAll();
    Subtask subtask = new Subtask();
    subtask.setTitle("Subtask To Delete");
    subtask.setDescription("Will be deleted soon");
    int subtaskIdToDelete = 25;
    int epicIdToUpdate = 23;
    subtask.setId(subtaskIdToDelete);
    subtask.setEpicId(epicIdToUpdate);
    subtask.setStatus(TaskStatus.IN_PROGRESS);
    t.updateSubtask(subtask);
    int epicId = t.getSubtaskById(subtask.getId()).getEpicId();

    t.deleteSubtask(subtask.getId());

    boolean isDeletedSubtask = t.getSubtaskById(subtask.getId()) == null;
    boolean isEpicStatusUpdated = t.getEpicById(epicId).getStatus() == TaskStatus.NEW;
    System.out.println("Test result: ");
    System.out.println(isDeletedSubtask && isEpicStatusUpdated ? "PASS" : "FAIL");
    clearAll();
  }

  static void test8DeleteTask() {
    System.out.println("Test 8 : Delete task.");
    System.out.println("---------------------------------------");
    addAll();
    int idToDelete = task1.getId();
    t.deleteTask(idToDelete);
    System.out.println("Test result: ");
    System.out.println(t.getTaskById(idToDelete) == null ? "PASS" : "FAIL");
    clearAll();
  }

  static void test9DeleteSubtask() {
    System.out.println("Test 9 : Delete subtask.");
    System.out.println("---------------------------------------");
    addAll();
    int idToDelete = subtask21.getId();
    int epicId = t.getSubtaskById(idToDelete).getEpicId();//39
    List<Subtask> subtasksBefore = t.getAllSubtasks();
    Epic epicBefore = t.getEpicById(epicId);
    boolean isSubtaskDeleted = t.getSubtaskById(idToDelete) == null;
    boolean isSubtaskDeletedFromEpic = !t.getSubtasksByEpicId(epicId).contains(t.getSubtaskById(idToDelete));

    t.deleteSubtask(idToDelete);
    isSubtaskDeleted = t.getSubtaskById(idToDelete) == null;
    isSubtaskDeletedFromEpic = !t.getSubtasksByEpicId(epicId).contains(t.getSubtaskById(idToDelete));
    System.out.println("Test result: ");
    System.out.println(isSubtaskDeleted && isSubtaskDeletedFromEpic ? "PASS" : "FAIL");
    clearAll();
  }

  static void test10DeleteEpic() {
    System.out.println("Test 10 : Delete epic.");
    System.out.println("---------------------------------------");
    addAll();
    int idToDelete = epic2.getId();
    boolean deleted = true;

    t.deleteEpic(idToDelete);
    for (Subtask subtask : t.getAllSubtasks()) {
      if (subtask.getEpicId() == idToDelete) {
        deleted = false;
      }
    }
    System.out.println("Test result: ");
    System.out.println(!t.getAllEpics().contains(epic2) && deleted ? "PASS" : "FAIL");
    clearAll();
  }

  static void addAll() {
    getReadyToAddAll();

    t.addTask(task1);
    t.addTask(task2);

    Epic epicAdded1 = t.addEpic(epic1);
    Epic epicAdded2 = t.addEpic(epic2);

    int epicId1 = epicAdded1.getId();
    subtask11.setEpicId(epicId1);
    subtask12.setEpicId(epicId1);

    t.addSubtask(subtask11);
    t.addSubtask(subtask12);

    int epicId2 = epicAdded2.getId();
    subtask21.setEpicId(epicId2);
    t.addSubtask(subtask21);



  }

  static void clearAll() {
    t.clearTasks();
    t.clearSubtasks();
    t.clearEpics();
    System.out.println();
  }

  static void getReadyToAddAll() {
    task1.setTitle("Task 1");
    task1.setDescription("description Task 1");
    task1.setStatus(TaskStatus.NEW);

    task2.setTitle("Task 2");
    task2.setDescription("description Task 2");
    task2.setStatus(TaskStatus.NEW);

    epic1.setTitle("Epic 1");
    epic1.setDescription("Description For Epic 1");
    subtask11.setTitle("Subtask 1-epic 1");
    subtask11.setDescription("Description to subtask 1 ,epic 1");
    subtask11.setStatus(TaskStatus.NEW);
    subtask12.setTitle("Subtask 2-epic 1");
    subtask12.setDescription("Description to subtask 2 ,epic 1");
    subtask12.setStatus(TaskStatus.NEW);

    epic2.setTitle("Epic 2");
    epic2.setDescription("Description For Epic 2");
    subtask21.setTitle("Subtask 1-epic 2");
    subtask21.setDescription("Description to subtask 1 ,epic 2");
    subtask21.setStatus(TaskStatus.NEW);
  }

  static void printAll() {
    System.out.println("Print all tasks: ");
    System.out.println(t.getAllTasks());
    System.out.println("Print all epics: ");
    System.out.println(t.getAllEpics());
    System.out.println("Print all subtasks: ");
    System.out.println(t.getAllSubtasks());
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

  static public String getAllTasksPretty() {
    String result = "";
    for (Task task : t.getAllTasks()) {
      result = result + task + "\n";
    }
    return result;
  }

  static void checkHowHistoryWorks() {
    getReadyToAddAll(); //create all tasks, epics and subtasks with titles, descriptions, and statuses NEW
    System.out.println("Task View History:(Should be blank) " + t.getHistory());
    addAll();
    System.out.println("Apply addAll();....");
    System.out.println("Task View History:(Should be blank) " + t.getHistory());

    System.out.println("All Tasks(should be 2): " + t.getAllTasks().size());
    System.out.println("All Epics(should be 2): " + t.getAllEpics().size());
    System.out.println("All SubTasks(should be 3): " + t.getAllSubtasks().size());
    System.out.println("Subtasks in Epic 1 (id = 3): " + t.getSubtasksByEpicId(3));
    System.out.println("Task View History:(Should be blank) " + t.getHistory());
    System.out.println();

//    t.getTaskById(1);
    for (int i = 0; i < 10; i++) {
      t.getTaskById(1);
    }
    System.out.println("t.getTaskById(1); X 10...");
    System.out.println("Task View History: " + t.getHistory());
    System.out.println("Task View History size(should be 10): " + t.getHistory().size());
    System.out.println();

//    //delete task that had been viewed before and check history
//    t.deleteTask(1);
//    System.out.println("Task View History: " + t.getHistory());
    System.out.println();
    //
    System.out.println("t.getEpicById(3)....");
    System.out.println(t.getEpicById(3));
    System.out.println("Task View History: " + t.getHistory());
    System.out.println("Task View History size(should be 10): " + t.getHistory().size());
    System.out.println();
    //create new subtask for Epic1 (id=3)
    System.out.println("Add new subtask to the epic 1 (id = 3), update it with status IN_PROGRESS");
    Subtask subtask = new Subtask();
    subtask.setTitle("qwqwe");
    subtask.setDescription("rer");
    subtask.setStatus(TaskStatus.NEW);
    subtask.setEpicId(3);
    t.addSubtask(subtask);
    subtask.setStatus(TaskStatus.IN_PROGRESS);
    t.updateSubtask(subtask);
    System.out.println("Task View History: " + t.getHistory());
    System.out.println("Subtasks in Epic 1 (id = 3): " + t.getSubtasksByEpicId(3));
    System.out.println("Task View History: " + t.getHistory());
  }

}