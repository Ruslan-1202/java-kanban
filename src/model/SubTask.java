package model;

import enums.*;

public class SubTask extends Task {

    private int epicId; //плоская модель
//    private Epic epic; // объектная модель

    public SubTask(String name, String descr, Status status, int id, int epicId) {
        super(name, descr, status, id);
        this.epicId = epicId;
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

    public int getEpicId() {
        return epicId;
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
