import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
  private ArrayList<Subtask> subtasks = new ArrayList<>();

  public Epic() {
  }

  public Epic(String title, String description) {
    super(title, description);
  }

  private Epic(int id, String title, String description, Status status, ArrayList<Subtask> subtasks) {
    this(title, description);
    this.setId(id);
    for (Subtask subtask : subtasks) {
      this.subtasks.add(subtask.clone());
    }
    this.setStatus(status);
  }

//  ToDo to check it
  public void updateStatus() {
    this.setStatus(calculateStatus());
  }

  public ArrayList<Subtask> getSubtasks() {
    return  this.subtasks;
  }
//  ToDo
  public void setSubtasks(ArrayList<Subtask> subtasks) {
    this.subtasks = subtasks;
  }

  public Status calculateStatus() {
    if(subtasks.size() == 0){
      return Status.NEW;
    }

    HashMap<Status,Integer> subtaskStatus = getStatuses();
    if (subtaskStatus.getOrDefault(Status.NEW, 0) == subtasks.size()) {
      return Status.NEW;
    }
    if (subtaskStatus.getOrDefault(Status.DONE, 0) == subtasks.size()) {
      return Status.DONE;
    }
    return Status.IN_PROGRESS;
  }

  private HashMap<Status,Integer> getStatuses(){
    HashMap<Status,Integer> result = new HashMap<>();
    for(Subtask subtask : subtasks){
      if (!result.containsKey(subtask.getStatus())) {
        result.put(subtask.getStatus(),1);
      } else {
        result.put(subtask.getStatus(),result.get(subtask.getStatus()) + 1);
      }
    }
    return result;
  }

  public void addSubtask(Subtask subtask){
    getSubtasks().add(subtask);
  }

  @Override
  public String toString() {
    return super.toString().substring(0,super.toString().lastIndexOf("}")) +
        ", subtasks.size=" + subtasks.size() +
        "}";
  }

  @Override
  protected Epic clone() {
    return new Epic(getId(), getTitle(), getDescription(), getStatus(),getSubtasks());
  }


}



