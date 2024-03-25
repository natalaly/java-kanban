import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
  private final HashMap<Integer, Task> tasks = new HashMap<>();
  private final HashMap<Integer, Epic> epics = new HashMap<>();
  private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
  private static int counter = 0;


  public ArrayList<Task> getAllTasks() {
    return new ArrayList<>(tasks.values());
  }

  public ArrayList<Epic> getAllEpics() {
    return new ArrayList<>(epics.values());
  }

  public ArrayList<Subtask> getAllSubtasks() {
    return new ArrayList<>(subtasks.values());
  }

  public String getAllTasksPretty() {
    String result = "";
    for (Task task : getAllTasks()) {
      result = result + task + "\n";
    }
    return result;
  }

  public void clearTasks() {
    tasks.clear();
  }

  public void clearEpics() {
    subtasks.clear();
    epics.clear();
  }

  public void clearSubtasks() {
    for (Epic epic : epics.values()) {
      epic.clearSubtasks();
    }
    subtasks.clear();
  }

  public void deleteSubtask(Subtask subtask){
    if(subtask == null || !subtasks.containsKey(subtask.getId())){
      return;
    }
    getEpicById(subtask.getEpicId()).removeSubtask(subtask);
    deleteById(subtask.getId());
  }

  public Task getTaskById(int id) {
    return tasks.get(id);
  }

  public Epic getEpicById(int id) {
    return epics.get(id);
  }

  public Subtask getSubtaskById(int id) {
    return subtasks.get(id);
  }

  public Task addTask(Task task) {
    if (task == null) {
      return null;
    }
    task.setId(generateId());
    tasks.put(task.getId(), task);
    return task;
  }

  public Epic addEpic(Epic epic) {
    if (epic == null) {
      return null;
    }
    epic.setId(generateId());
    epics.put(epic.getId(), epic);
    //  Adding subtasks to subtasks map for future reference
    for (Subtask subtask : epic.getSubtasks()) {
      addSubtask(subtask, epic.getId());
    }
    return epic;
  }

  public boolean addSubtask(Subtask subtask, int epicId) {
    if (subtask == null || !epics.containsKey(epicId)) {
      return false;
    }
    subtask.setId(generateId());
    subtask.setEpicId(epicId);
    getEpicById(epicId).addSubtask(subtask);
    subtasks.put(subtask.getId(), subtask);
    return true;
  }

  public boolean updateTask(Task task) {
    if (task == null || !tasks.containsKey(task.getId())) {
      return false;
    }
    tasks.put(task.getId(), task);
    return true;
  }

  public boolean updateEpic(Epic epic) {
    if (epic == null || !epics.containsKey(epic.getId())) {
      return false;
    }
    epics.put(epic.getId(), epic);
    for (Subtask subtask : epic.getSubtasks()) {
      if (subtask.getId() == 0) {
        addSubtask(subtask, epic.getId());
      } else {
        updateSubtask(subtask);
      }
    }
    return true;
  }

  public boolean updateSubtask(Subtask subtask) {
    if (subtask == null ||
        !subtasks.containsKey(subtask.getId()) ||
        getSubtaskById(subtask.getId()).getEpicId() != subtask.getEpicId()) {
      return false;
    }
    subtasks.put(subtask.getId(), subtask);
    epics.get(subtask.getEpicId()).updateStatus();

    return true;
  }

  public boolean deleteById(int id) {
    if (tasks.containsKey(id)) {
      tasks.remove(id);
      return true;
    }
    if (epics.containsKey(id)) {
      getSubtasksOfEpic(id).clear();
      epics.remove(id);
      return true;
    }
    if (subtasks.containsKey(id)) {
      int epicId = getSubtaskById(id).getEpicId();
      epics.get(epicId).removeSubtask(subtasks.get(id));
      subtasks.remove(id);
    }
    return false;

  }


  public ArrayList<Subtask> getSubtasksOfEpic(int id) {
    return new ArrayList<>(epics.get(id).getSubtasks());
  }

  private int generateId() {
    return ++counter;
  }


}
