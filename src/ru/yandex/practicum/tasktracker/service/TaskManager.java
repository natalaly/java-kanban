package ru.yandex.practicum.tasktracker.service;

import java.util.Set;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import java.util.List;

/**
 * The {@code TaskManager} interface manages tasks of different types including {@link Task},
 * {@link Epic}, and {@link Subtask}. It provides functionalities for storing, retrieving, updating,
 * and deleting tasks.
 * <p>
 * Implementations of this interface should provide methods to manipulate tasks, epics, and subtasks
 * within the system. These methods include retrieving all tasks, epics, and subtasks, clearing all
 * tasks, epics, and subtasks, deleting tasks, epics, and subtasks by their respective IDs,
 * retrieving tasks, epics, and subtasks by their IDs, adding new tasks, epics, and subtasks,
 * updating existing tasks, epics, and subtasks, and retrieving subtasks associated with a specific
 * epic.
 * <p>
 * The interface also provides a method to retrieve the history of operations performed on tasks.
 *
 * @see Task
 * @see Epic
 * @see Subtask
 */
public interface TaskManager {

  List<Task> getTasks();

  List<Epic> getEpics();

  List<Subtask> getSubtasks();

  List<Task> getPrioritizedTasks();

  List<Task> getHistory();

  Task getTaskById(final int id);

  Epic getEpicById(final int id);

  Subtask getSubtaskById(final int id);

  Set<Subtask> getSubtasksByEpicId(final int id);

  Task addTask(final Task task);

  Epic addEpic(final Epic epic);

  Subtask addSubtask(final Subtask subtask);

  void updateTask(final Task task);

  void updateEpic(final Epic epic);

  void updateSubtask(final Subtask subtask);

  void clearTasks();

  void clearEpics();

  void clearSubtasks();

  void deleteTask(final int id);

  void deleteEpic(final int id);

  void deleteSubtask(final int id);



}
