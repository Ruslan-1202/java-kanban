package service;

import enums.Status;
import enums.TaskKind;
import exceptions.NoSuchEpicExists;
import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int counterId;

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;

    protected TreeSet<Task> prioritizedTasks;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) { //надо вот так
        this.historyManager = historyManager;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(new CompareDate());
    }

    private int getNewId() {
        return ++counterId;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    //  Создание новой задачи
//    ==============================
    @Override
    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }

        if (!addPrioritizedTasks(task)) {
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

        if (!addPrioritizedTasks(epic)) {
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

        if (!addPrioritizedTasks(subTask)) {
            return null;
        }

        int id = getNewId();
        subTask.setId(id);
        subTask.setEpicId(idEpic);
        subTasks.put(id, subTask);
        epic.addSubTask(id);
        calculateStatus(idEpic);
        calculateTimes(subTask);
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

    private boolean addPrioritizedTasks(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }

        if (!isGoodDateTime(task)) {
            return false;
        }

        return prioritizedTasks.add(task);
    }

    private boolean isGoodDateTime(Task task) {
        Task lowerTask = prioritizedTasks.lower(task);
        Task higherTask = prioritizedTasks.higher(task);

        if (lowerTask != null && lowerTask.getEndTime().isAfter(task.getStartTime()) && !lowerTask.equals(task)) {
            return false;
        } else
            return higherTask == null || !task.getEndTime().isAfter(higherTask.getStartTime()) || higherTask.equals(task);
    }

    private void clearTasks() {
        clearAdditional(tasks.keySet(), TaskKind.TASK);
        tasks.clear();
    }


    private void clearEpics() {
        clearAdditional(epics.keySet(), TaskKind.EPIC);
        epics.clear();

        clearSubTasks();
    }

    private void clearSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.setStaus(Status.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }

        clearAdditional(subTasks.keySet(), TaskKind.SUB_TASK);
        subTasks.clear();
    }

    private void clearAdditional(Set<Integer> ids, TaskKind taskKind) {
        ids.stream().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(getTask(taskKind, id));
        });
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
        removeAdditional(id, TaskKind.TASK);
    }

    private void removeEpic(int id) {
        removeSubTasksInEpic(id);
        epics.remove(id);

        removeAdditional(id, TaskKind.EPIC);
    }

    private void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubTask(id);
        subTasks.remove(id);

        calculateStatus(epicId);
        calculateTimes(subTask);

        removeAdditional(id, TaskKind.SUB_TASK);
    }

    private void removeAdditional(int id, TaskKind taskKind) {
        historyManager.remove(id);
        prioritizedTasks.remove(getTask(taskKind, id));
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
        calculateTimes(subTask);
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

    private void calculateTimes(SubTask subTask) {
        int idEpic = subTask.getEpicId();

        Epic epic = getEpic(idEpic);
        if (epic == null) {
            throw new NoSuchEpicExists("Такого эпика нет: " + idEpic);
        }
        //продолжительность эпика - сумма всех сабов
        long tasksDuration = epic.readSubTasks().stream().mapToLong(a -> (getSubTask(a).getDuration().toMinutes())).sum();

        epic.setDuration(Duration.ofMinutes(tasksDuration));
        //время начала - самое раннее время
        if (epic.getStartTime().isAfter(subTask.getStartTime())) {
            epic.setStartTime(subTask.getStartTime());
        }
        //время окончания - самое позднее время
        if (epic.getEndTime().isBefore(subTask.getEndTime())) {
            epic.setEndTime(subTask.getEndTime());
        }
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

    protected int getCounterId() {
        return counterId;
    }

    protected void setCounterId(int counterId) {
        this.counterId = counterId;
    }
}

class CompareDate implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        }
        return 0;
    }
}
