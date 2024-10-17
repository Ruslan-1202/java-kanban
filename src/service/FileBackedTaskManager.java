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

    public void putTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void putEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void putSubTask(SubTask subTask) {
        Integer epicId = subTask.getEpicId();
        Integer id = subTask.getId();

        subTasks.put(id, subTask);
        Epic epic = epics.get(epicId);
        epic.addSubTask(id);
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
        TaskKind taskKind = task.getTaskKind();

        if (TaskKind.SUB_TASK.equals(taskKind)) {
            SubTask subTask = (SubTask) task;
            epicId = subTask.getEpicId();
        }

        return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), taskKind, task.getName(), task.getStaus(), task.getDescr(), epicId);
    }
}
