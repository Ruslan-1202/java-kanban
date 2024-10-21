package service;

import model.Task;

import java.io.File;

public class Managers {

    public static void main(String[] args) {
        File file = new File("task_manager.csv");

        TaskManager taskManager = getDefault(file);
        for (Task task : taskManager.readTasks()) {
            System.out.println(task);
        }
        taskManager.addTask(new Task("Task7", "Task 7 from file"));
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
