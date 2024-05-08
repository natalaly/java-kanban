package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class Task implements Cloneable {

  private int id;
  private String title;
  private String description;
  private TaskStatus status;

  public Task() {
    this.status = TaskStatus.NEW;
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
    return "Task{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", status=" + getStatus() +
        '}';
  }

  public Task clone() {
    Task task = new Task();
    task.setId(this.id);
    task.setTitle(this.title);
    task.setDescription(this.description);
    task.setStatus(TaskStatus.valueOf(this.status.name()));
    return task;
  }



}
