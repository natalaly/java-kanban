import java.util.ArrayList;

public class Epic extends Task{
  private ArrayList<Subtask> subtasks;

  public Epic() {
  }

  public Epic(String title, String description) {
    super(title, description);
    this.subtasks = new ArrayList<>();
  }

  public ArrayList<Subtask> getSubtasks() {
    return subtasks;
  }

  public void setSubtasks(ArrayList<Subtask> subtasks) {
    this.subtasks = subtasks;
  }

//  ToDo: to override correctly
  @Override
  public String toString() {
    return super.toString();

  }
}
//"Task{" +
//    "title='" + title + '\'' +
//    ", description='" + description + '\'' +
//    ", status=" + status +
//    ", id=" + id +
//    '}';

//ToDo: Status management
//    если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
//    если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
//    во всех остальных случаях статус должен быть IN_PROGRESS.