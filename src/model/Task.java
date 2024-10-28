package model;

import enums.Status;
import enums.TaskKind;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    //считаем по умолчанию продолжительность задачи 30 минут
    protected static final int DEFAULT_DURATION_IN_MINUTES = 0;

    protected String name;
    protected String descr;
    protected int id;
    protected Status status;

    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String descr, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.descr = descr;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String descr, Status status, int id) {
        this(name, descr, id, status, LocalDateTime.now(), Duration.ofMinutes(DEFAULT_DURATION_IN_MINUTES));
    }

    public Task(String name, String descr) {
        this(name, descr, Status.NEW, 0);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStaus() {
        return status;
    }

    public void setStaus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public TaskKind getTaskKind() {
        return TaskKind.TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getEpicId() {
        return 0;
    }

    @Override
    public String toString() {
        return this.getClass() + "{" +
                "name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
