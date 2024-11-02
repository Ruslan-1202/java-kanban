package model;

import enums.Status;
import enums.TaskKind;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subTasks; //плоская модель
    //private ArrayList<SubTask> subTasks; //объектная модель
    private LocalDateTime endTime;

    public Epic(String name, String descr, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, descr, id, status, startTime, duration);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String descr, Status status, int id) {
        this(name, descr, id, status, null, null);
    }

    public Epic(String name, String descr, int id) {
        this(name, descr, Status.NEW, id);
    }

    public void addSubTask(int id) {
        subTasks.add(id);
    }

    public ArrayList<Integer> readSubTasks() {
        return subTasks;
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskKind getTaskKind() {
        return TaskKind.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
