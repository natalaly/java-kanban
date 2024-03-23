public class Subtask extends Task{
  private int epicId;

  public Subtask() {
  }

  public Subtask(String title, String description) {
    super(title, description);
  }

  private Subtask(int id, String title, String description, Status status,int epicId) {
    this(title, description);
    this.setId(id);
    this.setStatus(status);
    this.setEpicId(epicId);
  }

  public int getEpicId() {
    return this.epicId;
  }

  public void setEpicId(int epicId) {
    this.epicId = epicId;
  }

  @Override
  public String toString() {
    String result = super.toString().substring(0,super.toString().lastIndexOf("}")) +
        ", epic id=" + epicId +
        '}';
    return result;
  }

  @Override
  protected Subtask clone() {
    return new Subtask(getId(), getTitle(), getDescription(), getStatus(), getEpicId());
  }
}
