package tasks;

import enums.*;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String descr, Status status, int id, int epicId) {
        super(name, descr, status, id);
        this.epicId = epicId;
    }

    public SubTask(String name, String descr, int id, int epicId) {
        this(name, descr, Status.NEW, id, epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}
