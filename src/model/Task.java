package model;

import enums.*;

public class Task {

    protected String name;
    protected String descr;
    protected int id;
    protected Status status;

    public Task(String name, String descr, Status status, int id) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.descr = descr;
    }

    public Task(String name, String descr, int id) {
        this(name, descr, Status.NEW, id);
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
