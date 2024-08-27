import enums.*;
import tasks.*;

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

    public int getNewId() {
        return ++counterId;
    }

    //  Создание новой задачи
//    ==============================
    public int addTask(String name, String descr) {
        counterId++;
        Task task = new Task(name, descr, Status.NEW, counterId);
        tasks.put(counterId, task);
        return counterId;
    }

    public int addTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
        return id;
    }

    public int addEpic(String name, String descr) {
        counterId++;
        Epic epic = new Epic(name, descr, Status.NEW, counterId);
        epics.put(counterId, epic);
        return counterId;
    }

    public int addEpic(Epic epic) {
        int id = epic.getId();
        epics.put(id, epic);
        return id;
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

    public int addSubTask(SubTask subTask, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return 0;
        }
        int id = subTask.getId();
        subTasks.put(id, subTask);
        epic.addSubTask(id);
        calculateStatus(idEpic);
        return id;
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
                    calculateStatus(epic.getId());
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

    public Epic getEpic(int id) {
        return epics.get(id);
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
        int id = epic.getId();

        Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            System.out.println("Такого эпика нет в списке");
            return;
        }
//        нельзя менять статус эпика вручную
        if (!oldEpic.getStaus().equals(epic.getStaus())) {
            System.out.println("Нельзя менять статус эпика");
            return;
        }

        epics.put(id, epic);
        calculateStatus(id);
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
    public void calculateStatus(int epicId) {
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
