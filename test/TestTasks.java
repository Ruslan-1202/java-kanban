import enums.Status;
import enums.TaskKind;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestTasks {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeAll
    static void initTasks() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void checkEpics() {
        Epic epic1 = new Epic("Epic 1", "Descr Ep 1", 0);
        epic1 = taskManager.addEpic(epic1);

        int id = epic1.getId();

        SubTask subTask1 = new SubTask("Sub 1", "Descr 1", 0, id);
        subTask1 = taskManager.addSubTask(subTask1, epic1.getId());

        SubTask subTask2 = new SubTask("Sub 2", "Descr 2");
        subTask2 = taskManager.addSubTask(subTask2, id);

        SubTask subTask3 = new SubTask("Sub 3", "Descr 3");
        subTask3 = taskManager.addSubTask(subTask3, id);

        assertEquals(epic1.getStaus(), Status.NEW, "Статус нового эпика не NEW");

        subTask2.setStaus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);
        assertEquals(epic1.getStaus(), Status.IN_PROGRESS, "Статус измененного эпика не IN_PROGRESS");

        subTask1.setStaus(Status.DONE);
        subTask2.setStaus(Status.DONE);
        assertEquals(epic1.getStaus(), Status.IN_PROGRESS, "Статус измененного эпика не IN_PROGRESS");

        taskManager.removeTask(TaskKind.SUB_TASK, subTask3.getId());
        assertEquals(epic1.getStaus(), Status.DONE, "Статус закрытого эпика не DONE");
    }

    @Test
    void createTask() {
        Task taskId1 = new Task("Test taskId1", "Test taskId1 description", Status.NEW, 6);
        Task taskId2 = new Task("Test taskId2", "Test taskId2 description", Status.DONE, 6);

        assertEquals(taskId1, taskId2, "задачи не равны");

        taskId2.setId(1);
        assertNotEquals(taskId1, taskId2, "задачи равны");

        SubTask SubtaskId1 = new SubTask("Test SubtaskId1", "Test SubtaskId1 description", Status.NEW, 2, 0);
        SubTask SubtaskId2 = new SubTask("Test SubtaskId2", "Test SubtaskId2 description", Status.DONE, 2, 0);

        assertEquals(SubtaskId1, SubtaskId2, "подзадачи не равны");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.readTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

    }

    @Test
    void removeTasks() {
        taskManager.clearTasks(TaskKind.TASK);
        List<Task> tasks = taskManager.readTasks();

        assertEquals(0, tasks.size(), "При очистке задач список не пуст");

        Task task = new Task("Test removeTasks", "Test removeTasks description");
        taskManager.addTask(task);
        taskManager.removeTask(task.getId());
        tasks = taskManager.readTasks();

        assertEquals(0, tasks.size(), "При удалении единственной задачи список не пуст");

        taskManager.addTask(task);
        Task task2 = new Task("Test removeTasks2", "Test removeTasks2 description");
        taskManager.addTask(task2);
        taskManager.removeTask(task.getId());
        tasks = taskManager.readTasks();

        assertNotEquals(0, tasks.size(), "При удалении не единственной задачи список пуст");
    }

    @Test
    void addHistory() {
        Task task = new Task("Test history task", "Test history task description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}