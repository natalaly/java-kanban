public class Task {
  private String title;
  private String description;
  private Status status;
  private long id;

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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task task)) return false;

    if (getId() != task.getId()) return false;
    if (getTitle() != null ? !getTitle().equals(task.getTitle()) : task.getTitle() != null) return false;
    if (getDescription() != null ? !getDescription().equals(task.getDescription()) : task.getDescription() != null)
      return false;
    return getStatus() == task.getStatus();
  }

  @Override
  public int hashCode() {
    int result = getTitle() != null ? getTitle().hashCode() : 0;
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
    result = 31 * result + (int) (getId() ^ (getId() >>> 32));
    return result;
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
