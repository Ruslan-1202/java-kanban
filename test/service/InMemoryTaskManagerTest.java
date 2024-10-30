package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Таск менеджер")
public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeAll
    static void initTasks() {
        taskManager = Managers.getDefault();
    }

//    @BeforeEach
//    void clear() {
//        taskManager.clearTasks(TaskKind.TASK);
//        taskManager.clearTasks(TaskKind.EPIC);
//        taskManager.clearTasks(TaskKind.SUB_TASK);
//    }
//
//    @Test
//    @DisplayName("Добавление одной задачи")
//    void add1Task() {
//        Task task1 = new Task("Task 1", "Descr 1");
//        task1 = taskManager.addTask(task1);
//
//        assertEquals(1, task1.getId(), "Не совпадают ИД");
//        assertEquals(Status.NEW, task1.getStaus(), "Не совпадают статус новой задачи");
//    }
//
//    @Test
//    @DisplayName("Добавление нескольких задач")
//    void addNTask() {
//        final int COUNT_TASKS = 20;
//
//        for (int i = 1; i <= COUNT_TASKS; i++) {
//            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
//            TestUtils.sleepTask();
//        }
//
//        List<Task> tasks = taskManager.readTasks();
//        assertEquals(COUNT_TASKS, tasks.size(), "Возвращается неправильное количество задач");
//    }
//
//    @Test
//    @DisplayName("Обновление задачи")
//    void updateTask() {
//        for (int i = 0; i < 2; i++) {
//            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
//            TestUtils.sleepTask();
//        }
//
//        Task oldTask = taskManager.readTasks().getFirst();
//        Task updTask = new Task("Upd task", "upd decr", Status.NEW, oldTask.getId());
//        updTask.setStartTime(oldTask.getStartTime().plusMinutes(10));
//        taskManager.updateTask(updTask);
//
//        assertEqualsTasks(updTask, taskManager.getTask(oldTask.getId()), "Не совпадают задачи");
//    }
//
//    @Test
//    @DisplayName("Проверка приоритезатора")
//    void prioritizedTasks() {
//        int id;
//        for (int i = 0; i < 3; i++) {
//            taskManager.addTask(new Task("Task " + i, "Descr task " + i));
//            TestUtils.sleepTask();
//        }
//
//        TreeSet<Task> treeSetBefore = taskManager.getPrioritizedTasks();
//
//        id = treeSetBefore.first().getId();
//
//        Task updTask = new Task("Upd task", "upd descr", id, Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ZERO);
//
//        taskManager.updateTask(updTask);
//
//        TreeSet<Task> treeSetAfter = taskManager.getPrioritizedTasks();
//
//        assertEquals(treeSetBefore.first().getId(), treeSetAfter.last().getId(), "Неправильно приоритезировалось");
//    }
//
//    private void assertEqualsTasks(Task expected, Task actual, String message) {
//        assertEquals(expected.getId(), actual.getId(), message + ", id");
//        assertEquals(expected.getName(), actual.getName(), message + ", name");
//        assertEquals(expected.getDescr(), actual.getDescr(), message + ", descr");
//        assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", getStartTime");
//    }
}
