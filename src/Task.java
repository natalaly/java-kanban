public class Task {
  private  int id;
  private String title;
  private String description;
  private Status status;

  public Task() {
  }

  public Task(String title, String description) {
    this();
    this.title = title;
    this.description = description;
    this.status = Status.NEW;
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
    String result = this.getClass().getName() +
        "{" +
        "id=" + id +
        ", title='" + title + '\'';
    if (description != null) {
      result += ", description.length=" + description.length();
    } else {
      result += ", description=null";
    }
    result += ", status=" + status +
        '}';
    return result;
  }

}
