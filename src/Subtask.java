public class Subtask extends Task{
  private Epic epic;

  public Subtask(String title, String description, int epicId) {
    super(title, description);
    this.epic = (Epic)TaskManager.getById(epicId);
  }

  public Epic getEpic() {
    return epic;
  }

  public void setEpic(Epic epic) {
    this.epic = epic;
  }
}
