package service;

import enums.TaskKind;
import esceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public Task addTask(Task task) {
        Task retTask = super.addTask(task);
        save();
        return retTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic retEpic = super.addEpic(epic);
        save();
        return retEpic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask, int idEpic) {
        SubTask retSubTask = super.addSubTask(subTask, idEpic);
        save();
        return retSubTask;
    }

    @Override
    public void clearTasks(TaskKind taskKind) {
        super.clearTasks(taskKind);
        save();
    }

    @Override
    public void removeTask(TaskKind taskKind, int id) {
        super.removeTask(taskKind, id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    public void addTaskFromFile(Task task) {
        TaskKind kind = getType(task);

        switch (kind) {
            case TASK -> tasks.put(task.getId(), task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
            case SUB_TASK -> {
                SubTask subTask = (SubTask) task;
                subTasks.put(subTask.getId(), subTask);
                Epic epic = epics.get(subTask.getEpicId());
                epic.addSubTask(subTask.getId());
            }
            case null, default -> {
                return;
            }
        }

        if (getCounterId() < task.getId())
            setCounterId(task.getId());
    }

    private void save() throws ManagerSaveException {

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(this.file, false))) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : this.readTasks()) {
                fileWriter.write(toString(task));
            }

            for (Epic epic : this.readEpics()) {
                fileWriter.write(toString(epic));
            }

            for (SubTask subTask : this.readSubTasks()) {
                fileWriter.write(toString(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private String toString(Task task) {
        int epicId = 0;
        TaskKind taskKind = getType(task);

        if (TaskKind.SUB_TASK.equals(taskKind)) {
            SubTask subTask = (SubTask) task;
            epicId = subTask.getEpicId();
        }

        return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), taskKind, task.getName(), task.getStaus(), task.getDescr(), epicId);
    }

    private TaskKind getType(Task task) {
        TaskKind taskKind = null;

        if (task instanceof SubTask)
            taskKind = TaskKind.SUB_TASK;
        else if (task instanceof Epic)
            taskKind = TaskKind.EPIC;
        else if (task != null)
            taskKind = TaskKind.TASK;

        return taskKind;
    }
}
