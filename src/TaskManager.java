import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
  private static final HashMap<Integer, Task> tasks = new HashMap<>();
  private static final HashMap<Integer, Epic> epics = new HashMap<>();
  private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
  private static int counter = 0;


  public static List<Task> getAllTasks() {
    ArrayList<Task> result = new ArrayList<>();
    for (Task task : tasks.values()) {
      result.add(task.clone());
    }
    return result;
  }

  public static List<Epic> getAllEpics() {
    ArrayList<Epic> result = new ArrayList<>();
    for (Epic epic : epics.values()) {
      result.add(epic.clone());
    }
    return result;
  }

  public static String getAllTasksPretty() {
    String result = "";
    for (Task task : getAllTasks()) {
      result = result + task + "\n";
    }
    return result;
  }

  public static void clearTasks() {
    tasks.clear();
  }

  public static void clearEpics() {
    epics.clear();
    subtasks.clear();
  }

  public static Task getTaskById(int id) {
    return tasks.get(id).clone();
  }

  public static Epic getEpicById(int id) {
    return epics.get(id).clone();
  }

  public static Task addTask(Task newTask) {
    if (newTask == null) {
      return null;
    }
    Task taskToAdd = newTask.clone();
    int id = generateId();
    taskToAdd.setId(id);
    tasks.put(taskToAdd.getId(), taskToAdd);
    return taskToAdd.clone();
  }

  public static Epic addEpic(Epic newEpic) {
    if (newEpic == null) {
      return null;
    }
    Epic epicToAdd = newEpic.clone();
    int id = generateId();
    epicToAdd.setId(id);
    epics.put(epicToAdd.getId(), epicToAdd);
    return epicToAdd.clone();
  }
// TODO return type boolean
  public static void addSubtask(Subtask subtask, int epicId) {
    if (subtask == null || !epics.containsKey(epicId) ) {
      return;
    }
    Subtask subtaskToAdd = subtask.clone();
    int id = generateId();
    subtaskToAdd.setId(id);
    subtaskToAdd.setEpicId(epicId);
    epics.get(epicId).addSubtask(subtaskToAdd);
//    subtaskToAdd.setEpicId(getEpicById(epicId));
    updateEpic(null);

    //input: newTAsk
    // check not null, epicId exists in db
    // taskToSave


  }


  private static int generateId() {
    return ++counter;
  }

  public static Task updateTask(Task task) {
    if (task == null) {
      System.out.println("No update data received.");
      return null;
    }
    if (!tasks.containsKey(task.getId())) {
      System.out.println("There is no such task.");
      return null;
    }
    Task taskUpdated = task.clone();
    tasks.put(taskUpdated.getId(), taskUpdated);
    return getTaskById(task.getId());
  }

  //ToDo Add subtasks review
  public static Epic updateEpic(Epic epic) {
    if (epic == null) {
      System.out.println("No update data received.");
      return null;
    }

    if (!epics.containsKey(epic.getId())) {
      System.out.println("There is no such epic.");
      return null;
    }
    Epic epicUpdated = epic.clone();
    epics.put(epicUpdated.getId(), epicUpdated.clone());
    return getEpicById(epic.getId());
  }

  public static void deleteById(int id) {
    tasks.remove(id);
    epics.remove(id);
    subtasks.remove(id);
  }

  // ToDo Add implementation
//  ??? - getSubtasks() from epic already has clone(), so we do not need clone here
  public static ArrayList<Subtask> getAllSubtasks(int id) {
//    get epic from the tracker
    Epic epicToLookAt = epics.get(id);
//    get subtasks from the epic in the tracker
    epicToLookAt.getSubtasks();
    return epicToLookAt.getSubtasks();
  }


}
