import main.java.ru.yandex.practicum.tasktracker.model.Epic;
import main.java.ru.yandex.practicum.tasktracker.model.Subtask;
import main.java.ru.yandex.practicum.tasktracker.model.Task;
import main.java.ru.yandex.practicum.tasktracker.model.TaskStatus;
import main.java.ru.yandex.practicum.tasktracker.service.InMemoryTaskManager;
import main.java.ru.yandex.practicum.tasktracker.service.TaskManager;

public class Main {
  public static void main(String[] args) {

    TaskManager t = new InMemoryTaskManager();

    Epic epic = new Epic();
    epic.setTitle("e");
    epic.setDescription("d");
//    t.addEpic(epic);

//    Subtask st1 = new Subtask();
//    st1.setTitle("st1");
//    st1.setId(30);
//    Subtask st2 = new Subtask();
//    st2.setTitle("st2");
//    epic.addSubtask( st1);
//    epic.addSubtask(st2);

    t.addEpic(epic);

    Subtask st1 = new Subtask();
    st1.setTitle("st1");
    st1.setEpicId(epic.getId());

    t.addSubtask(st1);
    System.out.println("Subtasks in epic" + t.getSubtasksByEpicId(epic.getId()));

    Epic changesToEpic = new Epic();
    changesToEpic.setId(epic.getId());
    changesToEpic.setTitle(epic.getTitle());
    changesToEpic.setDescription(epic.getDescription());
    changesToEpic.setStatus(epic.getStatus());
    Subtask ss = epic.getSubtasks().stream().findFirst().get();


    Subtask s = new Subtask();
        s.setId(ss.getId());
    s.setTitle(ss.getTitle());
    s.setDescription(ss.getDescription());
    s.setEpicId(ss.getEpicId());
    s.setStatus(TaskStatus.DONE);
    changesToEpic.addSubtask(s);

    System.out.println(changesToEpic.getSubtasks());
    System.out.println("Subtasks in epic" + t.getSubtasksByEpicId(epic.getId()));

    System.out.println("before updating");
    System.out.println("Subtasks in epic" + t.getSubtasksByEpicId(epic.getId()));
    System.out.println("t.getAllEpics()" + t.getAllEpics());
    System.out.println("t.getAllSubtasks()" + t.getAllSubtasks());

    t.updateEpic(changesToEpic);
    System.out.println();

    System.out.println("after updating");
    System.out.println("Subtasks in epic" + t.getSubtasksByEpicId(epic.getId()));
    System.out.println("t.getAllEpics()" + t.getAllEpics());
    System.out.println("t.getAllSubtasks()" + t.getAllSubtasks());




//    System.out.println("t.getSubtasksByEpicId(1) " + t.getSubtasksByEpicId(1));
//    System.out.println("t.getSubtasksByEpicId(2) " + t.getSubtasksByEpicId(2));

  }
}