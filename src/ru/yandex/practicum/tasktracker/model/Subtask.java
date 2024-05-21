package ru.yandex.practicum.tasktracker.model;

public class Subtask extends Task {

  private int epicId;

  public Subtask() {
  }

  public int getEpicId() {
    return epicId;
  }

  public void setEpicId(int epicId) {
    this.epicId = epicId;
  }

  @Override
  public String toString() {
    return super.toString().substring(0, super.toString().lastIndexOf("}")) +
        ", epic id=" + epicId +
        '}';
  }

  @Override
  public TaskType getType() {
    return TaskType.SUBTASK;
  }

  @Override
  public Subtask copy() {
    Subtask newSubtask = new Subtask();
    newSubtask.setId(this.getId());
    newSubtask.setTitle(this.getTitle());
    newSubtask.setDescription(this.getDescription());
    newSubtask.setStatus(TaskStatus.valueOf(this.getStatus().name()));
    newSubtask.setDuration(this.getDuration());
    newSubtask.setStartTime(this.getStartTime());
    newSubtask.setEpicId(this.getEpicId());
    return newSubtask;
  }

  @Override
  public String toCsvLine() {
    return super.toCsvLine().trim() + getEpicId();
  }
}