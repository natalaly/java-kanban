import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.Status;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;
import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;

import java.util.ArrayList;
import java.util.List;


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
    test1AddTask();
    test2AddEpic();
    test3AddEpicWithSubtasks();
    test4AddSubtask();
    test5ChangingTaskStatus();
    test6ChangeSubtaskStatus();
    test7AutoUpdatingEpicAfterDeletingSubtask();
    test8DeleteTask();
    test9DeleteSubtask();
    test10DeleteEpic();
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
    Epic epicToAdd = new Epic("EpicWithSubtasks", "Has two subtasks with status DONE and NEW");
    Subtask subtaskWithEpic = new Subtask("Subtask for EpicWithSubtasks", "Some Description");
    subtaskWithEpic.setStatus(Status.DONE);
    Subtask subtaskWithEpic2 = new Subtask("Subtask 2 for EpicWithSubtasks", "Some Description");
    epicToAdd.setSubtasks(new ArrayList<>(List.of(subtaskWithEpic, subtaskWithEpic2)));

    Epic epic = t.addEpic(epicToAdd);
    int epicsNumAfterAdding = t.getAllEpics().size();
    boolean isEpicAdded = epicsNumBeforeAdding < epicsNumAfterAdding;
    boolean isEpicStatusExpected = t.getEpicById(epic.getId()).getStatus() == Status.IN_PROGRESS;
    boolean areSubtasksAddedToEpic = !t.getSubtasksByEpicId(epic.getId()).isEmpty();
    boolean areSubtasksAddedToTracker = t.getAllSubtasks().containsAll(t.getSubtasksByEpicId(epic.getId()));

    System.out.println("Test result: \n" +
        (isEpicAdded && isEpicStatusExpected && areSubtasksAddedToEpic && areSubtasksAddedToTracker ? "PASS" : "FAILED"));
    System.out.println();
  }

  static void test4AddSubtask() {
    System.out.println("Test 4 : add new subtask to the tracker.");
    System.out.println("---------------------------------------");
    int epicId = epic1.getId();
    int subtasksNumBeforeAdding = t.getAllSubtasks().size();
    subtask11.setEpicId(epicId);
    t.addSubtask(subtask11);
    int addedSubtaskId = subtask11.getId();
    int taskNumAfterAdding = t.getAllSubtasks().size();
    boolean subtaskAddedToTracker = subtasksNumBeforeAdding < taskNumAfterAdding;
    boolean subtaskAddedToEpic = t.getSubtasksByEpicId(epicId).contains(subtask11);
    boolean isEpicStatusCorrect = t.getEpicById(epicId).getStatus() == Status.NEW;
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
    Task someTask = new Task(task1.getTitle(), task1.getDescription());
    someTask.setStatus(Status.DONE);
    t.updateTask(someTask);
    for (Task task : t.getAllTasks()) {
      if (task.getId() == 0 && task.getStatus() == Status.DONE) {
        canNotUpdateWithNewTask = false;
        break;
      }
      canNotUpdateWithNewTask = true;
    }
    someTask.setId(task1.getId());
    t.updateTask(someTask);
    if (t.getTaskById(task1.getId()).getStatus() == Status.DONE) {
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

    Subtask updatingData = new Subtask(subtask11.getTitle(), subtask11.getDescription());
    updatingData.setId(t.getSubtaskById(idSubtaskToUpdate).getId());
    updatingData.setEpicId(epicId);
    updatingData.setStatus(Status.DONE);

    t.updateSubtask(updatingData);

    boolean isSubtaskStatusUpdated = t.getSubtaskById(idSubtaskToUpdate).getStatus() == Status.DONE;
    boolean isEpicStatusUpdated = t.getEpicById(epicId).getStatus() == Status.IN_PROGRESS;
    System.out.println("Test result: ");
    System.out.println(isSubtaskStatusUpdated && isEpicStatusUpdated ? "PASS" : "FAIL");
    clearAll();
  }

  static void test7AutoUpdatingEpicAfterDeletingSubtask() {
    System.out.println("Test 7 : auto updating epic status after subtask has been deleted.");
    System.out.println("---------------------------------------");
    addAll();
    Subtask subtask = new Subtask("Subtask To Delete", "Will be deleted soon");
    int subtaskIdToDelete = 25;
    int epicIdToUpdate = 23;
    subtask.setId(subtaskIdToDelete);
    subtask.setEpicId(epicIdToUpdate);
    subtask.setStatus(Status.IN_PROGRESS);
    t.updateSubtask(subtask);
    int epicId = t.getSubtaskById(subtask.getId()).getEpicId();

    t.deleteSubtask(subtask.getId());

    boolean isDeletedSubtask = t.getSubtaskById(subtask.getId()) == null;
    boolean isEpicStatusUpdated = t.getEpicById(epicId).getStatus() == Status.NEW;
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
    ArrayList<Subtask> subtasksBefore = t.getAllSubtasks();
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

  static void printAll() {
    System.out.println("Print all tasks: ");
    System.out.println(t.getAllTasks());
    System.out.println("Print all epics: ");
    System.out.println(t.getAllEpics());
    System.out.println("Print all subtasks: ");
    System.out.println(t.getAllSubtasks());
  }

  static void clearAll() {
    t.clearTasks();
    t.clearSubtasks();
    t.clearEpics();
    System.out.println();
  }

  static public String getAllTasksPretty() {
    String result = "";
    for (Task task : t.getAllTasks()) {
      result = result + task + "\n";
    }
    return result;
  }
}