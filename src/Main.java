//import java.util.HashMap;

import Enums.Status;
import Tasks.*;

import java.util.HashMap;

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
        sub2.setStaus(Status.IN_PROGRESS);
        taskManager.updateSubTask(sub2);
        for ( Epic task: taskManager.readEpics().values() ) {
            System.out.println(task);
        }
        taskManager.removeSubTask(j);

        for ( Task task: taskManager.readTasks().values() ) {
            System.out.println(task);
        }
        for ( Epic task: taskManager.readEpics().values() ) {
            System.out.println(task);
        }
        for ( SubTask task: taskManager.readSubTasks().values() ) {
            System.out.println(task);
        }
    }

}
