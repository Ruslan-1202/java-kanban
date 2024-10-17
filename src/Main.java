import enums.Status;
import enums.TaskKind;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        //   Данный код для тестирования и примеров вызовов методов

        // TaskManager taskManager = Managers.getDefault();
        File file = new File("task_manager.csv");

        TaskManager taskManager = Managers.getDefault(file);

        Task task1 = new Task("Task 1", "Descr 1", Status.NEW, 0);
        task1 = taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Descr 2", Status.NEW, 0);
        task2 = taskManager.addTask(task2);

        Task task3 = new Task("Task 3", "Descr 3", Status.NEW, 0);
        task3 = taskManager.addTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task3.getId());
        System.out.println("hisTest.getTasks 1" + taskManager.getHistory());
        taskManager.getTask(task2.getId());
        System.out.println("hisTest.getTasks 2" + taskManager.getHistory());
        taskManager.getTask(task3.getId());
        System.out.println("hisTest.getTasks 3" + taskManager.getHistory());
        taskManager.getTask(task1.getId());
        System.out.println("hisTest.getTasks 4" + taskManager.getHistory());
        taskManager.removeTask(TaskKind.TASK, task1.getId());
        System.out.println("hisTest.getTasks 5" + taskManager.getHistory());

        Epic epic1 = new Epic("Epic 1", "Descr Ep 1", 0);
        epic1 = taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Sub 1", "Descr 1", 0, epic1.getId());
        subTask1 = taskManager.addSubTask(subTask1, epic1.getId());

        SubTask subTask2 = new SubTask("Sub 2", "Descr 2");
        subTask2 = taskManager.addSubTask(subTask2, epic1.getId());

        SubTask subTask3 = new SubTask("Sub 3", "Descr 3");
        subTask3 = taskManager.addSubTask(subTask3, epic1.getId());

        SubTask sub2 = subTask2;
        sub2.setStaus(Status.DONE);
        taskManager.updateSubTask(sub2);

        System.out.println("\nЭпик и сабтаски");
        for (Epic task : taskManager.readEpics()) {
            System.out.println(task);
        }
        for (SubTask task : taskManager.readSubTasks()) {
            System.out.println(task);
        }

        Epic epic2 = new Epic("Epic 2", "DesEpic 2", 0);
        taskManager.addEpic(epic2);

        Epic epic3 = taskManager.getEpic(epic2.getId());

        epic3.setStaus(Status.DONE);
        taskManager.updateEpic(epic3);

        taskManager.removeTask(TaskKind.SUB_TASK, subTask2.getId());
        SubTask sub1 = taskManager.getSubTask(5);
        sub1.setStaus(Status.DONE);
        taskManager.updateSubTask(sub1);

        for (int i = 0; i < 8; i++) {
            Task task34 =  taskManager.getEpic(epic2.getId());
        }

        System.out.println("\nЭпики после изменения статусов:");
        for (Epic task : taskManager.readEpics()) {
            System.out.println(task);
        }

        System.out.println("\nИстория задач:");
        for (Task task: taskManager.getHistory()) {
            System.out.println(task);
        }

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.readTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.readEpics()) {
            System.out.println(epic);

            for (SubTask task : manager.readSubTasksInEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.readSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

