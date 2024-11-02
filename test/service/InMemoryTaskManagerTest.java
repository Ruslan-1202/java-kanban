package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Таск менеджер")
public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeAll
    static void initTasks() {
        taskManager = Managers.getDefault();
    }
}
