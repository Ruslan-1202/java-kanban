import Enums.*;
import Tasks.*;

import java.util.HashMap;

public class TaskManager {

    private int counterId;

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
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
        counterId++;
        tasks.put(counterId, task);
        return counterId;
    }

    public int addEpic(String name, String descr) {
        counterId++;
        Epic epic = new Epic(name, descr, Status.NEW, counterId);
        epics.put(counterId, epic);
        return counterId;
    }

    public int addEpic(Epic epic) {
        counterId++;
        epics.put(counterId, epic);
        return counterId;
    }

    public int addSubTask(String name, String descr, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return 0;
        }
        counterId++;
        epic.addSubTask(counterId, name, descr);
        return counterId;
    }

    public int addSubTask(SubTask subTask, int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return 0;
        }
        counterId++;
        epic.addSubTask(counterId, subTask);
        return counterId;
    }
//    ==============================

    //    Получение списка всех задач
//    ==============================
    public HashMap<Integer, Task> readTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> readEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> readSubTasksInEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return null;
        }
        return epic.readSubTasks();
    }

    public HashMap<Integer, SubTask> readSubTasksInEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        return epic.readSubTasks();
    }

    public HashMap<Integer, SubTask> readSubTasks() {
        HashMap<Integer, SubTask> subTasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            subTasks.putAll(epic.readSubTasks());
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
                break;
            case SUB_TASK:
                for (Epic epic : epics.values()) {
                    epic.clearSubTasks();
                }
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
        return readSubTasks().get(id);
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
                epics.remove(id);
                break;
            case SUB_TASK:
                SubTask subTask = readSubTasks().get(id);
                Epic epic = epics.get(subTask.getEpicId());
                epic.removeSubTask(subTask);
                epic.calculateStatus();
                break;
            default:

        }
    }
//    ==============================

    //    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    ==============================
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
//        нельзя менять статус эпика вручную
        if (!oldEpic.getStaus().equals(epic.getStaus())) {
            System.out.println("Нельзя менять статус эпика");
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        readSubTasks().put(subTask.getId(), subTask);

        epics.get(subTask.getEpicId())
                .calculateStatus();
    }
//    ==============================

    //    Получение списка всех подзадач определённого эпика.
//    ==============================
    public HashMap<Integer, SubTask> getSubTasksInEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        return epic.readSubTasks();
    }
//    ==============================
}
