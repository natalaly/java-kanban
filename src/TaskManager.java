import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
  private static HashMap<Integer, Task> tasks = new HashMap<>();
//  ToDo Add implementation for other types
//  private static HashMap<Integer, Epic> epics = new HashMap<>();
//  private static HashMap<Integer, Subtask> subtasks = new HashMap<>();
  private static int counter = 0;


  public static ArrayList<Task> getAllTasks() {
    return new ArrayList<>(tasks.values());
  }

  public static String getAllTasksPretty() {
    String result = "";
    for (Task task : getAllTasks()){
      result = result + task + "\n";
    }
    return result;
  }

  public static void clearTasks() {
    tasks.clear();
  }


  public static Task getById(int id) {
    return tasks.get(id);
  }

  public static Task addTask(Task newTask) {
    if (newTask == null) {
      return null;
    }
    int id = generateId();
    newTask.setId(id);
    Task taskToAdd = newTask.clone();
    tasks.put(taskToAdd.getId(), taskToAdd);
    return taskToAdd;
  }

  private static int generateId() {
    return ++counter;
  }

  public static Task updateTask(Task updatedTask) {
    if(!tasks.containsKey(updatedTask.getId())){
      System.out.println("There is no such task.");
      return null;
    }
    if(updatedTask == null){
      System.out.println("No update data received.");
      return null;
    }
    tasks.put(updatedTask.getId(), updatedTask);
    return getById(updatedTask.getId());
  }

  public static void deleteTaskById(int id) {
    tasks.remove(id);
  }

// ToDo Add implementation
  public static List<Subtask> getAllSubtasks(Epic epic){
    List<Subtask> result = new ArrayList<>();
    return result;
  }



}
