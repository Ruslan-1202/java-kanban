package service;

import enums.Status;
import enums.TaskKind;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        }

        List<Task> tasks = taskManager.readTasks();
        assertEquals(COUNT_TASKS, tasks.size(), "Возвращается неправильное количество задач");
//        assertEquals(Status.NEW, task1.getStaus(), "Не совпадают статус новой задачи");
    }
    
}
