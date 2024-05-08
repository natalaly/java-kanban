package ru.yandex.practicum.tasktracker.model;


import java.util.HashSet;
import java.util.Set;

public class Subtask extends Task {

  private int epicId;

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
  public Subtask clone() {
//    Subtask subtask = (Subtask)super.clone();
    Subtask subtask = new Subtask();
    subtask.setId(this.getId());
    subtask.setTitle(this.getTitle());
    subtask.setDescription(this.getDescription());
    subtask.setStatus(TaskStatus.valueOf(this.getStatus().name()));
    subtask.setEpicId(this.epicId);
    return subtask;
  }
}