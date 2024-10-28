package service;

import enums.Status;
import enums.TaskKind;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Таск менеджер")
public class InMemoryTaskManagerTest {

    static TaskManager taskManager;

    @BeforeAll
    static void initTasks() {
        taskManager = Managers.getDefault();
    }

    @BeforeEach
    void clear() {
        taskManager.clearTasks(TaskKind.TASK);
        taskManager.clearTasks(TaskKind.EPIC);
        taskManager.clearTasks(TaskKind.SUB_TASK);
    }

    @Test
    @DisplayName("Добавление одной задачи")
    void add1Task() {
        Task task1 = new Task("Task 1", "Descr 1");
        task1 = taskManager.addTask(task1);

        assertEquals(1, task1.getId(), "Не совпадают ИД");
        assertEquals(Status.NEW, task1.getStaus(), "Не совпадают статус новой задачи");
    }

    @Test
    @DisplayName("Добавление нескольких задач")
    void addNTask() {
        final int COUNT_TASKS = 20;

        for (int i = 1; i <= COUNT_TASKS; i++) {
            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
            sleepTask();
        }

        List<Task> tasks = taskManager.readTasks();
        assertEquals(COUNT_TASKS, tasks.size(), "Возвращается неправильное количество задач");
    }

    private void sleepTask() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {

        }
    }

    @Test
    @DisplayName("Обновление задачи")
    void updateTask() {
        int id = 1;
        for (int i = 0; i < 2; i++) {
            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
            sleepTask();
        }

        Task updTask = taskManager.getTask(id);
        updTask.setStartTime(updTask.getStartTime().plusMinutes(10));
        taskManager.updateTask(updTask);

        assertEqualsTasks(updTask, taskManager.getTask(updTask.getId()), "Не совпадают задачи");
    }

    @Test
    @DisplayName("Проверка приоритезатора")
    void prioritizedTasks() {
        int id = 1;
        for (int i = 0; i < 3; i++) {
            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
            sleepTask();
        }

        TreeSet<Task> treeSetBefore = taskManager.getPrioritizedTasks();

        Task updTask = taskManager.getTask(id);
        updTask.setStartTime(updTask.getStartTime().plusMinutes(10));
        taskManager.updateTask(updTask);

        TreeSet<Task> treeSetAfter = taskManager.getPrioritizedTasks();

    }

    private void assertEqualsTasks(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescr(), actual.getDescr(), message + ", descr");
        assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", getStartTime");
    }
}
