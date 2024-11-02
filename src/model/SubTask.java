package model;

import enums.Status;
import enums.TaskKind;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicId; //плоская модель
//    private Epic epic; // объектная модель

    public SubTask(String name, String descr, int id, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, descr, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String descr, Status status, int id, int epicId) {
        this(name, descr, id, status, LocalDateTime.now(), Duration.ofMinutes(DEFAULT_DURATION_IN_MINUTES), epicId);
    }

    public SubTask(String name, String descr, int id, int epicId) {
        this(name, descr, Status.NEW, id, epicId);
    }

    public SubTask(String name, String descr, Status status) {
        super(name, descr, status, 0);
    }

    public SubTask(String name, String descr) {
        super(name, descr, Status.NEW, 0);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskKind getTaskKind() {
        return TaskKind.SUB_TASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", descr='" + getDescr() + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}
