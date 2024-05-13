package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class Task {

  private int id;
  private String title;
  private String description;
  private TaskStatus status;

  public Task() {
    this.status = TaskStatus.NEW;
  }

  public Task(Task taskToCopy) {
    Objects.requireNonNull(taskToCopy);
    setId(taskToCopy.id);
    setTitle(taskToCopy.title);
    setDescription(taskToCopy.description);
    setStatus(TaskStatus.valueOf(taskToCopy.status.name()));
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public TaskStatus getStatus() {
    return status;
  }

  public void setStatus(TaskStatus taskStatus) {
    this.status = taskStatus;
  }

  public TaskType getType() {
    return TaskType.TASK;
  }

  public String toCsvLine() {
    return String.format("%s,%s,%s,%s,%s,%s" + System.lineSeparator(),
        this.getId(),
        this.getType(),
        this.getTitle(),
        this.getStatus(),
        this.getDescription(),
        " ");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Task task)) {
      return false;
    }
    return getId() == task.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() +
        "{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", status=" + getStatus() +
        '}';
  }


}
