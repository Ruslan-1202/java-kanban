package service;

import enums.Status;
import exceptions.ManagerReadException;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

@DisplayName("Файловый TaskManager")
public class FileBackedTaskManagerTest extends TaskManagerTest {
    @BeforeAll
    static void initTasks() {
        File file = new File("task_manager.csv");
        try {
            taskManager = Managers.getDefault(file);
        } catch (ManagerReadException e) {
            System.out.println("Ошибка обработки файла");
        }
    }

    @Test
    public void addNullDateTimeAndDuration() {
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(new File("task_manager1.csv"));

        Task task = new Task("Name no time", "Descr no time", 1, Status.NEW, null, null);
        task = taskManager1.addTask(task);

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(new File("task_manager1.csv"));
        Task newtask = taskManager2.getTask(task.getId());

        assertEqualsTasks(newtask, task, "Не совпадают задачи после загрузки из файла");
    }
}
