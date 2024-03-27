package main.java.ru.yandex.practicum.tasktracker.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
  private final ArrayList<Subtask> subtasks = new ArrayList<>();

  public Epic(String title, String description) {
    super(title, description);
  }

  @Override
  public Status getStatus() {
    Status status = calculateStatus();
    setStatus(status);
    return status;
  }

  public ArrayList<Subtask> getSubtasks() {
    return subtasks;
  }

  public void setSubtasks(ArrayList<Subtask> subtasks) {
    this.subtasks.addAll(subtasks);
  }

  public void addSubtask(Subtask subtask) {
    if (subtask == null || subtasks.contains(subtask)) {
      return;
    }
    subtasks.add(subtask);
  }

  public void updateSubtask(Subtask subtask) {
    if (subtask == null || !subtasks.contains(subtask)) {
      return;
    }
    subtasks.set(subtasks.indexOf(subtask), subtask);
  }

  public void removeSubtask(Subtask subtask) {
    subtasks.remove(subtask);
  }

  public void clearSubtasks() {
    subtasks.clear();
  }

  private Status calculateStatus() {
    if (subtasks.size() == 0) {
      return Status.NEW;
    }

    HashMap<Status, Integer> subtaskStatus = getSubtasksStatuses();
    if (subtaskStatus.getOrDefault(Status.NEW, 0) == subtasks.size()) {
      return Status.NEW;
    }
    if (subtaskStatus.getOrDefault(Status.DONE, 0) == subtasks.size()) {
      return Status.DONE;
    }
    return Status.IN_PROGRESS;
  }

  private HashMap<Status, Integer> getSubtasksStatuses() {
    HashMap<Status, Integer> result = new HashMap<>();
    for (Subtask subtask : subtasks) {
      if (!result.containsKey(subtask.getStatus())) {
        result.put(subtask.getStatus(), 1);
      } else {
        result.put(subtask.getStatus(), result.get(subtask.getStatus()) + 1);
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return super.toString().substring(0, super.toString().lastIndexOf("}")) +
        ", subtasks.size=" + subtasks.size() +
        "}";
  }
}