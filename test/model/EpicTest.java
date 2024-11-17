package model;

import enums.Status;
import enums.TaskKind;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;
import utils.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
public class EpicTest {
    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeAll
    static void initTasks() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("создание эпиков")
    public void createEpic() {
        Epic epic = new Epic("Epic 1", "Epic descr 1", 8);
        Epic epic1 = taskManager.addEpic(epic);

        assertEqualsEpics(epic1, epic, "Эпики не совпадают");
    }

    @Test
    @DisplayName("работа с эпиками")
    void checkEpics() {
        Epic epic1 = new Epic("Epic 1", "Descr Ep 1", 0);
        epic1 = taskManager.addEpic(epic1);
        TestUtils.sleepTask();
        int id = epic1.getId();

        SubTask subTask1 = new SubTask("Sub 1", "Descr 1", 0, id);
        subTask1 = taskManager.addSubTask(subTask1, epic1.getId());
        TestUtils.sleepTask();

        SubTask subTask2 = new SubTask("Sub 2", "Descr 2");
        subTask2 = taskManager.addSubTask(subTask2, id);
        TestUtils.sleepTask();

        SubTask subTask3 = new SubTask("Sub 3", "Descr 3");
        subTask3 = taskManager.addSubTask(subTask3, id);
        TestUtils.sleepTask();

        assertEquals(epic1.getStaus(), Status.NEW, "Статус нового эпика не NEW");

        subTask2.setStaus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);
        TestUtils.sleepTask();
        assertEquals(epic1.getStaus(), Status.IN_PROGRESS, "Статус измененного эпика не IN_PROGRESS");

        subTask1.setStaus(Status.DONE);
        subTask2.setStaus(Status.DONE);
        assertEquals(epic1.getStaus(), Status.IN_PROGRESS, "Статус измененного эпика не IN_PROGRESS");

        taskManager.removeTask(TaskKind.SUB_TASK, subTask3.getId());
        assertEquals(epic1.getStaus(), Status.DONE, "Статус закрытого эпика не DONE");
    }

    @Test
    @DisplayName("история по эпикам")
    void historyEpics() {
        Epic epic1 = new Epic("Epic 1", "Descr Ep 1", 0);
        epic1 = taskManager.addEpic(epic1);
        TestUtils.sleepTask();
        int id = epic1.getId();

        SubTask subTask1 = new SubTask("Sub 1", "Descr 1", 0, id);
        subTask1 = taskManager.addSubTask(subTask1, epic1.getId());
        TestUtils.sleepTask();

        SubTask subTask2 = new SubTask("Sub 2", "Descr 2");
        subTask2 = taskManager.addSubTask(subTask2, id);
        TestUtils.sleepTask();

        taskManager.getEpic(id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История имеет неправильный размер");

        taskManager.getSubTask(subTask1.getId());
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "История имеет неправильный размер");

        taskManager.getSubTask(subTask2.getId());
        taskManager.removeTask(TaskKind.SUB_TASK, subTask1.getId());
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "История имеет неправильный размер");

        taskManager.removeTask(TaskKind.EPIC, id);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "История имеет неправильный размер");
    }

    private void assertEqualsEpics(Epic expected, Epic actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescr(), actual.getDescr(), message + ", descr");
    }
}
