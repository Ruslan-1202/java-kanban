package service;

import exceptions.ManagerReadException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

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
}
