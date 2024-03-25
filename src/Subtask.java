public class Subtask extends Task{
  private int epicId;

  public Subtask() {
  }

  public Subtask(String title, String description) {
    super(title, description);
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

}
