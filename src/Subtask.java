public class Subtask extends Task{
  private Epic epic;

  public Subtask(String title, String description, Status status, Epic epic) {
    super(title, description, status);
    this.epic = epic;
  }

  public Epic getEpic() {
    return epic;
  }

  public void setEpic(Epic epic) {
    this.epic = epic;
  }
}
