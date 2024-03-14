import java.util.ArrayList;

public class Epic extends Task{
  private ArrayList<Subtask> subtasks;

  public Epic(String title, String description, Status status, ArrayList<Subtask> subtasks) {
    super(title, description, status);
    this.subtasks = subtasks;
  }

  public ArrayList<Subtask> getSubtasks() {
    return subtasks;
  }

  public void setSubtasks(ArrayList<Subtask> subtasks) {
    this.subtasks = subtasks;
  }
}
