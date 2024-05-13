package ru.yandex.practicum.tasktracker.model;

public class Subtask extends Task {

  private int epicId;

  public Subtask(){}

  public Subtask(Subtask subtaskToCopy) {
    super(subtaskToCopy);
    setEpicId(subtaskToCopy.epicId);
  }

  public int getEpicId() {
    return epicId;
  }

  public void setEpicId(int epicId) {
    this.epicId = epicId;
  }

  @Override
  public String toString() {
    String result = super.toString().substring(0, super.toString().lastIndexOf("}")) +
        ", epic id=" + epicId +
        '}';
    return result;
  }

  @Override
  public TaskType getType() {
    return TaskType.SUBTASK;
  }

  @Override
  public String toCsvLine() {
    return super.toCsvLine().trim() + getEpicId() + System.lineSeparator();
  }
}