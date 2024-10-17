package service;

import enums.Status;
import enums.TaskKind;
import esceptions.ManagerReadException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        return loadFromFile(file);
    }

    private static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(getDefaultHistory(), file);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                Task task = fromString(br.readLine());
                addTaskFromFile(fileBackedTaskManager, task);
            }

        } catch (IOException e) {
            throw new ManagerReadException();
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        if (value.isBlank())
            return null;
        String[] fields = value.split(",");
        Task task = null;

//        id,type,name,status,description,epic
        switch (fields[1]) {
            case "TASK":
                task = new Task(fields[2], fields[4], Status.valueOf(fields[3]), Integer.parseInt(fields[0]));
                break;
            case "EPIC":
                task = new Epic(fields[2], fields[4], Status.valueOf(fields[3]), Integer.parseInt(fields[0]));
                break;
            case "SUB_TASK":
                task = new SubTask(fields[2], fields[4], Status.valueOf(fields[3]), Integer.parseInt(fields[0]), Integer.parseInt(fields[5]));
                break;
        }

        return task;
    }

    public static void addTaskFromFile(FileBackedTaskManager fileBackedTaskManager, Task task) {
        if (task == null)
            return;

        TaskKind taskKind = task.getTaskKind();

        switch (taskKind) {
            case TASK -> fileBackedTaskManager.putTask(task);
            case EPIC -> fileBackedTaskManager.putEpic((Epic) task);
            case SUB_TASK -> fileBackedTaskManager.putSubTask((SubTask) task);
            case null, default -> {
                return;
            }
        }

        if (fileBackedTaskManager.getCounterId() < task.getId())
            fileBackedTaskManager.setCounterId(task.getId());
    }
}
