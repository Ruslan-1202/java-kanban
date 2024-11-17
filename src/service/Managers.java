package service;

import exceptions.ManagerReadException;

import java.io.File;

public class Managers {

    public static void main(String[] args) {
        File file = new File("task_manager.csv");

        TaskManager taskManager = getDefault(file);
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(File file) throws ManagerReadException {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
