package ru.yandex.practicum.tasktracker.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Epic extends Task {

  private Set<Subtask> subtasks = new HashSet<>();

  @Override
  public TaskStatus getStatus() {
    return calculateStatus();
  }

  public Set<Subtask> getSubtasks() {
    return Collections.unmodifiableSet(subtasks);
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
    subtasks.remove(subtask);
    subtasks.add(subtask);
  }

  public void removeSubtask(Subtask subtask) {
    subtasks.remove(subtask);
  }

  public void clearSubtasks() {
    subtasks.clear();
  }

  private TaskStatus calculateStatus() {
    if (subtasks.size() == 0) {
      return TaskStatus.NEW;
    }
    Map<TaskStatus, Integer> subtaskStatus = getSubtasksStatuses();

    if (subtaskStatus.getOrDefault(TaskStatus.NEW, 0) == subtasks.size()) {
      return TaskStatus.NEW;
    }
    if (subtaskStatus.getOrDefault(TaskStatus.DONE, 0) == subtasks.size()) {
      return TaskStatus.DONE;
    }
    return TaskStatus.IN_PROGRESS;
  }

  private Map<TaskStatus, Integer> getSubtasksStatuses() {
    Map<TaskStatus, Integer> result = new HashMap<>();

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

  @Override
  public Epic clone() {
    Epic epic = new Epic();
    epic.setId(this.getId());
    epic.setTitle(this.getTitle());
    epic.setDescription(this.getDescription());
    epic.setStatus(TaskStatus.valueOf(this.getStatus().name()));
    Set<Subtask> clonedSubtasks = new HashSet<>();
    this.subtasks.forEach(s -> clonedSubtasks.add(s.clone()));
    epic.subtasks = clonedSubtasks;
    return epic;
  }
}
