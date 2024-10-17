package service;

import enums.TaskKind;
import model.Epic;
import model.SubTask;
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
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(getDefaultHistory(), file);
        return fileBackedTaskManager.loadFromFile();
    }

    public static void addTaskFromFile(FileBackedTaskManager fileBackedTaskManager, Task task) {
        if (task == null) {
            return;
        }

        TaskKind taskKind = task.getTaskKind();

        switch (taskKind) {
            case TASK -> fileBackedTaskManager.putTask(task);
            case EPIC -> fileBackedTaskManager.putEpic((Epic) task);
            case SUB_TASK -> fileBackedTaskManager.putSubTask((SubTask) task);
            case null, default -> {
                return;
            }
        }

        if (fileBackedTaskManager.getCounterId() < task.getId()) {
            fileBackedTaskManager.setCounterId(task.getId());
        }
    }
}
