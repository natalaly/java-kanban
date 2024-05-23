package ru.yandex.practicum.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

  private int id;
  private String title;
  private String description;
  private TaskStatus status;
  private Duration duration;
  private LocalDateTime startTime;

  public Task() {
    this.status = TaskStatus.NEW;
    this.duration = Duration.ZERO;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public void setStatus(final TaskStatus taskStatus) {
    this.status = taskStatus;
  }

  //  TODO throw exception if duration < 0
  public void setDuration(final Duration duration) {
    if (duration.isNegative()) {
      throw new IllegalArgumentException("Duration cannot be negative");
    }
    this.duration = duration;
  }

  public Duration getDuration() {
    return duration;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(final LocalDateTime startTime) {
    this.startTime = startTime;
  }

  //  TODO case when start time is null
  public LocalDateTime getEndTime() {
    return startTime == null ? null : startTime.plus(duration);
  }

  public TaskType getType() {
    return TaskType.TASK;
  }

  public String toCsvLine() {
    return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
        this.getId(),
        this.getType(),
        this.getTitle(),
        this.getStatus(),
        this.getDescription(),
        this.getDuration().toMinutes(),
        this.getStartTime(),
        " ");
  }

  public Task copy() {
    final Task newTask = new Task();
    newTask.setId(this.getId());
    newTask.setTitle(this.getTitle());
    newTask.setDescription(this.getDescription());
    newTask.setStatus(TaskStatus.valueOf(this.getStatus().name()));
    newTask.setDuration(this.getDuration());
    newTask.setStartTime(this.getStartTime());
    return newTask;
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
        ", duration=" + getDuration().toMinutes() +
        ", startTime=" + getStartTime() +
        ", endTime=" + getEndTime() +
        '}';
  }
}
