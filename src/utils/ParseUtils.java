package utils;

import enums.Status;
import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParseUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static Task fromString(String value) {
        if (value.isBlank()) {
            return null;
        }

        String[] fields = value.split(",");
        Task task;

//        id,type,name,status,description,epic,startTime,duration
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
            default:
                return null;
        }

        if (!fields[6].equals("null")) {
            task.setStartTime(LocalDateTime.parse(fields[6], FORMATTER));
        } else {
            task.setStartTime(null);
        }

        if (!fields[7].equals("null")) {
            task.setDuration(Duration.ofMinutes(Integer.parseInt(fields[7])));
        } else {
            task.setDuration(null);
        }

        return task;
    }

    public static String parseToString(Task task) {
        String dateTime, duration;

        if (task.getStartTime() == null) {
            dateTime = "null";
        } else {
            dateTime = task.getStartTime().format(FORMATTER);
        }

        if (task.getDuration() == null) {
            duration = "null";
        } else {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", task.getId(), task.getTaskKind(), task.getName(), task.getStaus(), task.getDescr(), task.getEpicId(), dateTime, duration);
    }
}
