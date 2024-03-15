public class Task {
  private String title;
  private String description;
  private Status status;
  private int id;

  public Task() {
  }

  public Task(String title, String description, Status status) {
    this.title = title;
    this.description = description;
    this.status = status;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task task)) return false;

    return getId() == task.getId();
  }

  @Override
  public int hashCode() {
    return getId();
  }

  @Override
  public String toString() {
    return "Task{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", status=" + status +
        ", id=" + id +
        '}';
  }
}
