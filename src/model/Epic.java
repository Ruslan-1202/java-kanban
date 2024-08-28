package model;

import enums.*;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasks; //плоская модель
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
        return (ArrayList<Integer>)(subTasks.clone());
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public void removeSubTask(int id) {
        subTasks.remove(Integer.valueOf(id));
    }

    @Override
    public Epic clone() throws CloneNotSupportedException {
        Epic epic = new Epic(this.name, this.descr, this.status, this.id);
        return epic;
    }
}
