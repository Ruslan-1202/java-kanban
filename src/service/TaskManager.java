package service;

import enums.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {

    private int counterId;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    private int getNewId() {
        return ++counterId;
    }

    //  Создание новой задачи
//    ==============================
    public int addTask(String name, String descr) {
        Task task = addTask(new Task(name, descr, Status.NEW, 0));
        return task.getId();
    }

    public Task addTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

//    public int addTask(Task task) {
//        int id = task.getId();
//        tasks.put(id, task);
//        return id;
//    }

    public int addEpic(String name, String descr) {
        counterId++;
        Epic epic = new Epic(name, descr, Status.NEW, counterId);
        epics.put(counterId, epic);
        return counterId;
    }

    public Epic addEpic(Epic epic) {
        int id = getNewId();
        Epic newEpic = new Epic(epic.getName(), epic.getDescr(), id);
        epics.put(id, newEpic);
        return newEpic;
    }

    public int addSubTask(String name, String descr, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return 0;
        }
        counterId++;
        SubTask subTask = new SubTask(name, descr, Status.NEW, counterId, idEpic);
        subTasks.put(counterId, subTask);
        epic.addSubTask(counterId);
        calculateStatus(idEpic);
        return counterId;
    }

    public SubTask addSubTask(SubTask subTask, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return null;
        }
        subTask.setId(getNewId());
        int id = subTask.getId();
        subTasks.put(id, subTask);
        epic.addSubTask(id);
        calculateStatus(idEpic);
        return subTask;
    }
//    ==============================

    //    Получение списка всех задач
//    ==============================
    public Collection<Task> readTasks() {
        return tasks.values();
    }

    public Collection<Epic> readEpics() {
        return epics.values();
    }

    public Collection<SubTask> readSubTasks() {
        return subTasks.values();
    }

    public HashMap<Integer, SubTask> readSubTasksInEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        return readSubTasksInEpic(epic);
    }

    public HashMap<Integer, SubTask> readSubTasksInEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик не найден");
            return null;
        }

        HashMap<Integer, SubTask> subTasks = new HashMap<>();
        for (int id : epic.readSubTasks()) {
            subTasks.put(id, getSubTask(id));
        }
        return subTasks;
    }
//    ==============================

    //    Удаление всех задач
    public void clearTasks(TaskKind taskKind) {
        switch (taskKind) {
            case TASK:
                tasks.clear();
                break;
            case EPIC:
                epics.clear();
                subTasks.clear();
                break;
            case SUB_TASK:
                for (Epic epic : epics.values()) {
                    epic.clearSubTasks();
                    epic.setStaus(Status.NEW);
                }
                subTasks.clear();
                break;
            default:
        }
    }

    public void clearTasks() {
        clearTasks(TaskKind.TASK);
    }

    public void clearEpics() {
        clearTasks(TaskKind.EPIC);
    }

    public void clearSubTasks() {
        clearTasks(TaskKind.SUB_TASK);
    }
//    ==============================

    //    c. Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) throws CloneNotSupportedException {
        return epics.get(id).clone();
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }
//    ==============================

    //    f. Удаление по идентификатору
//    ==============================
    public void removeTask(int id) {
        removeTask(TaskKind.TASK, id);
    }

    public void removeEpic(int id) {
        removeTask(TaskKind.EPIC, id);
    }

    public void removeSubTask(int id) {
        removeTask(TaskKind.SUB_TASK, id);
    }

    public void removeTask(TaskKind taskKind, int id) {
        switch (taskKind) {
            case TASK:
                tasks.remove(id);
                break;
            case EPIC:
                removeSubTasksInEpic(id);
                epics.remove(id);
                break;
            case SUB_TASK:
                SubTask subTask = subTasks.get(id);
                int epicId = subTask.getEpicId();
                Epic epic = epics.get(epicId);
                epic.removeSubTask(id);
                subTasks.remove(id);
                calculateStatus(epicId);
                break;
            default:

        }
    }
//    ==============================

    //    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    ==============================
    public void updateTask(Task task) {
        if (task == null) return;

        int id = task.getId();
        if (tasks.get(id) == null) {
            System.out.println("Такой задачи нет в списке");
            return;
        }
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) {
        if (epic == null) return;

        Epic newEpic = epics.get(epic.getId());
        if (newEpic == null) {
            System.out.println("Такого эпика нет в списке");
            return;
        }
//        обновляем только то что можно
        newEpic.setName(epic.getName());
        newEpic.setDescr(epic.getDescr());
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask == null) return;

        int id = subTask.getId();
        if (subTasks.get(id) == null) {
            System.out.println("Такой подзадачи нет в списке");
            return;
        }
        subTasks.put(id, subTask);
        calculateStatus(subTask.getEpicId());
    }
//    ==============================

    //    Получение списка всех подзадач определённого эпика.
//    ==============================
    public HashMap<Integer, SubTask> getSubTasksInEpic(int idEpic) {
        HashMap<Integer, SubTask> subTasks = new HashMap<>();
        Epic epic = epics.get(idEpic);
        for (int id : epic.readSubTasks()) {
            subTasks.put(id, subTasks.get(id));
        }
        return subTasks;
    }
//    ==============================
    //Удаление сабтасков из эпика
    public void removeSubTasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        ArrayList<Integer> epicSubTasks = epic.readSubTasks();

        for (int id: epicSubTasks) {
            subTasks.remove(id);
            epic.removeSubTask(id);
        }
        epic.clearSubTasks();
    }

    //    расчет статуса эпика по сабтакскам
    private void calculateStatus(int epicId) {
//        проверим, все ли DONE
        boolean isAllDone = true;
        boolean isAllNew = true;
        ArrayList<Integer> subsOfEpic;
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Такого эпика не существует");
            return;
        }

        subsOfEpic = epic.readSubTasks();

        for (int id : subsOfEpic) {
            SubTask subTask = subTasks.get(id);
            if (!Status.DONE.equals(subTask.getStaus())) {
                isAllDone = false;
            }
            if (!Status.NEW.equals(subTask.getStaus())) {
                isAllNew = false;
            }
            if (!isAllDone && !isAllNew) break;
        }

        if (subsOfEpic.isEmpty()) {
            isAllDone = false;
            isAllNew = true;
        }

        if (isAllDone) {
            epic.setStaus(Status.DONE);
        } else if (isAllNew) {
            epic.setStaus(Status.NEW);
        } else {
            epic.setStaus(Status.IN_PROGRESS);
        }
    }
}
