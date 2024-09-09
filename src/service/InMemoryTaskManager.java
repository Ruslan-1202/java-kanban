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

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();

        historyManager = Managers.getDefaultHistory();
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
        List newTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            newTasks.add(task);
        }
        return newTasks;
    }

    @Override
    public List<Epic> readEpics() {
        List newEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            newEpics.add(epic);
        }
        return newEpics;
    }

    @Override
    public List<SubTask> readSubTasks() {
        List newSubTasks = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            newSubTasks.add(subTask);
        }
        return newSubTasks;
    }
//    ==============================

    //    Удаление всех задач
    @Override
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
    @Override
    public void removeTask(int id) {
        removeTask(TaskKind.TASK, id);
    }

    @Override
    public void removeEpic(int id) {
        removeTask(TaskKind.EPIC, id);
    }

    @Override
    public void removeSubTask(int id) {
        removeTask(TaskKind.SUB_TASK, id);
    }

    @Override
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
