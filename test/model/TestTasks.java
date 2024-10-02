package model;

import enums.Status;
import enums.TaskKind;
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
        taskManager.removeTask(TaskKind.TASK, task.getId());
        tasks = taskManager.readTasks();

        assertEquals(0, tasks.size(), "При удалении единственной задачи список не пуст");

        taskManager.addTask(task);
        Task task2 = new Task("Test removeTasks2", "Test removeTasks2 description");
        taskManager.addTask(task2);
        taskManager.removeTask(TaskKind.TASK, task.getId());
        tasks = taskManager.readTasks();

        assertNotEquals(0, tasks.size(), "При удалении не единственной задачи список пуст");
    }

    @Test
    void addHistory() {
        Task task = new Task("Test history task", "Test history task description", Status.NEW, 1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Размер истории не 1.");

        Task task2 = new Task("Task 2", "Task descr 2", Status.NEW, 2);
        historyManager.add(task2);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Размер истории не 2.");

        //добавляем таск с тем же ИД, должна замениться
        Task task3 = new Task("Task 1", "Task descr 1", Status.NEW, 1);
        historyManager.add(task3);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Размер истории не 2.");
    }

}