package ru.yandex.practicum.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {

  private final Set<Subtask> subtasks = new HashSet<>();

  public Epic() {
  }

  @Override
  public TaskStatus getStatus() {
    return calculateStatus();
  }

  @Override
  public TaskType getType() {
    return TaskType.EPIC;
  }

  // TODO
  @Override
  public Duration getDuration() {
    return calculateDuration();
  }

  //  TODO
  @Override
  public LocalDateTime getStartTime() {
    return defineStartTime();
  }

  //  TODO
  @Override
  public LocalDateTime getEndTime() {
    return defineEndTime();
  }

  @Override
  public Epic copy() {
    Epic newEpic = new Epic();
    newEpic.setId(this.getId());
    newEpic.setTitle(this.getTitle());
    newEpic.setDescription(this.getDescription());
    newEpic.setStatus(TaskStatus.valueOf(this.getStatus().name()));
    newEpic.setDuration(this.getDuration());
    newEpic.setStartTime(this.getStartTime());
    this.subtasks.forEach(subtask -> newEpic.addSubtask(subtask.copy()));
    return newEpic;
  }

  @Override
  public String toString() {
    return super.toString().substring(0,super.toString().lastIndexOf("}")) +
        ", subtasks.size=" + subtasks.size() +
        "}";
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
    if (subtasks.isEmpty()) {
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

  /**
   * Performs a calculation of the total duration of the Epic by summing the durations of its subtasks.
   * <p>
   * If the Epic does not have any subtasks, the total duration will be zero.
   * @return {@link Duration}
   */
  private Duration calculateDuration() {
    return subtasks.stream().map(Subtask::getDuration)
        .reduce(Duration.ZERO, Duration::plus);
  }

  /**
   * Defines and returns the earliest start date and time among all subtasks of the Epic.
   * <p>
   * If the Epic does not contain any subtasks or if all subtasks have null start times, null is returned.
   * @return {@link LocalDateTime}
   */
  private LocalDateTime defineStartTime() {
    return subtasks.stream()
        .map(Subtask::getStartTime)
        .filter(Objects::nonNull)
        .min(LocalDateTime::compareTo)
        .orElse(null);
  }

  /**
   * Defines and returns the latest end date and time among all subtasks of the Epic.
   * <p>
   *   If the Epic does not have any subtasks or if ll subtasks have null end times, null is returned.
   * @return {@link LocalDateTime}
   */
  private LocalDateTime defineEndTime() {
    return subtasks.stream()
        .map(Subtask::getEndTime)
        .filter(Objects::nonNull)
        .max(LocalDateTime::compareTo)
        .orElse(null);

  }




}
