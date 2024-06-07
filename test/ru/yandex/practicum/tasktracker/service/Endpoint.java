package ru.yandex.practicum.tasktracker.service;

public enum Endpoint {

  /* GET endpoints */
  GET_TASKS,
  GET_TASKS_ID,
  GET_SUBTASKS,
  GET_SUBTASKS_ID,
  GET_EPICS,
  GET_EPICS_ID,
  GET_EPICS_ID_SUBTASKS,
  GET_HISTORY,
  GET_PRIORITIZED,

  /* POST endpoints */
  POST_TASKS,
  POST_TASKS_ID,
  POST_SUBTASKS,
  POST_SUBTASKS_ID,
  POST_EPIC,

  /* DELETE endpoints */
  DELETE_TASKS_ID,
  DELETE_SUBTASKS_ID,
  DELETE_EPICS_ID

}
