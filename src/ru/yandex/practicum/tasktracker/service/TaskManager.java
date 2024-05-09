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
 *  @see Task
 *  @see Epic
 *  @see Subtask
 */
public interface TaskManager {

  List<Task> getAllTasks();

  List<Epic> getAllEpics();

  List<Subtask> getAllSubtasks();

  void clearTasks();

  void clearEpics();

  void clearSubtasks();

  void deleteTask(int id);

  void deleteEpic(int id);

  void deleteSubtask(int id);

  Task getTaskById(int id);

  Epic getEpicById(int id);

  Subtask getSubtaskById(int id);

  Task addTask(Task task);

  Epic addEpic(Epic epic);

  Subtask addSubtask(Subtask subtask);

  void updateTask(Task task);

  void updateEpic(Epic epic);

  void updateSubtask(Subtask subtask);

  Set<Subtask> getSubtasksByEpicId(int id);

  List<Task> getHistory();

}
