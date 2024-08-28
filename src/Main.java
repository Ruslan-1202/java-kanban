//import java.util.HashMap;

import enums.Status;
import model.*;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //   Данный код для тестирования и примеров вызовов методов

        TaskManager taskManager = new TaskManager();

        taskManager.addTask("Task 1", "Descr 1");
        taskManager.addTask("Task 2", "Descr 2");
        taskManager.addTask("Task 3", "Descr 3");

        int i = taskManager.addEpic("Epic 1", "Descr Ep 1");

        taskManager.addSubTask("Sub 1", "Descr 1", i);
        int j = taskManager.addSubTask("Sub 2", "Descr 2", i);
        SubTask sub2 = taskManager.getSubTask(j);
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

        Epic epic3 = null;
         try {
             epic3 = taskManager.getEpic(i);
         } catch (CloneNotSupportedException e) {

         }

        epic3.setStaus(Status.DONE);
        taskManager.updateEpic(epic3);

        taskManager.removeSubTask(j);
        SubTask sub1 = taskManager.getSubTask(5);
        sub1.setStaus(Status.DONE);
        taskManager.updateSubTask(sub1);

        System.out.println("\nЭпики после изменения статусов:");
        for (Epic task : taskManager.readEpics()) {
            System.out.println(task);
        }

        System.out.println("\nFinal status:");
        for (Task task : taskManager.readTasks()) {
            System.out.println(task);
        }
        for (Epic task : taskManager.readEpics()) {
            System.out.println(task);
        }
        for (SubTask task : taskManager.readSubTasks()) {
            System.out.println(task);
        }
    }

}
