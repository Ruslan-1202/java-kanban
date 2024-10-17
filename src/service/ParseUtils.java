package service;

import enums.Status;
import enums.TaskKind;
import model.Epic;
import model.SubTask;
import model.Task;

public class ParseUtils {

    public static Task fromString(String value) {
        if (value.isBlank()) {
            return null;
        }

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

    public static String parseToString(Task task) {
        int epicId = 0;
        TaskKind taskKind = task.getTaskKind();

        if (TaskKind.SUB_TASK.equals(taskKind)) {
            SubTask subTask = (SubTask) task;
            epicId = subTask.getEpicId();
        }

        return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), taskKind, task.getName(), task.getStaus(), task.getDescr(), epicId);
    }
}
