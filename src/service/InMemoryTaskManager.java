package service;

import enums.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int counterId;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) { //надо вот так
        this.historyManager = historyManager;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    private int getNewId() {
        return ++counterId;
    }

    //  Создание новой задачи
//    ==============================
    @Override
    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }
        int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        int id = getNewId();
        epic.setStaus(Status.NEW);
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null || subTask == null) {
            return null;
        }
        int id = getNewId();
        subTask.setId(id);
        subTask.setEpicId(idEpic);
        subTasks.put(id, subTask);
        epic.addSubTask(id);
        calculateStatus(idEpic);
        return subTask;
    }
//    ==============================

    //    Получение списка всех задач
//    ==============================
    @Override
    public List<Task> readTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> readEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> readSubTasks() {
        return new ArrayList<>(subTasks.values());
    }
//    ==============================

    //    Удаление всех задач
    @Override
    public void clearTasks(TaskKind taskKind) {
        switch (taskKind) {
            case TASK:
                clearTasks();
                break;
            case EPIC:
                clearEpics();
                break;
            case SUB_TASK:
                clearSubTasks();
                break;
            default:
        }
    }

    private void clearTasks() {
        tasks.clear();
    }

    private void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    private void clearSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.setStaus(Status.NEW);
        }
        subTasks.clear();
    }
//    ==============================

    //    c. Получение по идентификатору.
    private Task getTask(TaskKind taskKind, int id) {
        Task task = null;
        if (TaskKind.SUB_TASK.equals(taskKind)) {
            task = subTasks.get(id);
        } else if (TaskKind.EPIC.equals(taskKind)) {
            task = epics.get(id);
        } else if (TaskKind.TASK.equals(taskKind)) {
            task = tasks.get(id);
        }
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task getTask(int id) {
        return getTask(TaskKind.TASK, id);
    }

    @Override
    public Epic getEpic(int id) {
//        return epics.get(id);
        return (Epic) getTask(TaskKind.EPIC, id);
    }

    @Override
    public SubTask getSubTask(int id) {
//        return subTasks.get(id);
        return (SubTask) getTask(TaskKind.SUB_TASK, id);
    }
//    ==============================

    //    f. Удаление по идентификатору
//    ==============================
    private void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    private void removeEpic(int id) {
        removeSubTasksInEpic(id);
        epics.remove(id);

        historyManager.remove(id);
    }

    private void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubTask(id);
        subTasks.remove(id);
        calculateStatus(epicId);

        historyManager.remove(id);
    }

    @Override
    public void removeTask(TaskKind taskKind, int id) {
        switch (taskKind) {
            case TASK:
                removeTask(id);
                break;
            case EPIC:
                removeEpic(id);
                break;
            case SUB_TASK:
                removeSubTask(id);
                break;
            default:

        }
    }
//    ==============================

    //    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    ==============================
    @Override
    public void updateTask(Task task) {
        if (task == null) return;

        int id = task.getId();
        if (tasks.get(id) == null) {
            System.out.println("Такой задачи нет в списке");
            return;
        }
        tasks.put(id, task);
    }

    @Override
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

    @Override
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
    @Override
    public ArrayList<SubTask> readSubTasksInEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик не найден");
            return null;
        }

        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int id : epic.readSubTasks()) {
            subTasks.add(this.subTasks.get(id));
        }
        return subTasks;
    }
//    public ArrayList<SubTask> getSubTasksInEpic(int idEpic) {
//        Epic epic = epics.get(idEpic);
//        return epic.readSubTasks();
//    }

    //    ==============================
    //Удаление сабтасков из эпика
    private void removeSubTasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        for (int id : epic.readSubTasks()) {
            subTasks.remove(id);
            historyManager.remove(id);
        }
        epic.clearSubTasks();
    }

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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
