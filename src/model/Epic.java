package model;

import enums.Status;
import enums.TaskKind;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subTasks; //плоская модель
    //private ArrayList<SubTask> subTasks; //объектная модель

    public Epic(String name, String descr, Status status, int id) {
        super(name, descr, status, id);
        subTasks = new ArrayList<>();
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

    @Override
    public TaskKind getTaskKind() {
        return TaskKind.EPIC;
    }
}
