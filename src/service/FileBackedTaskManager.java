package service;

import enums.TaskKind;
import esceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final String FILE_NAME = "task_manager.csv";

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public Task addTask(Task task) {
        Save();
        return super.addTask(task);
    }

    @Override
    public Epic addEpic(Epic epic) {
        Save();
        return super.addEpic(epic);
    }

    @Override
    public SubTask addSubTask(SubTask subTask, int idEpic) {
        Save();
        return super.addSubTask(subTask, idEpic);
    }

    @Override
    public void clearTasks(TaskKind taskKind) {
        super.clearTasks(taskKind);
    }

    @Override
    public void removeTask(TaskKind taskKind, int id) {
        super.removeTask(taskKind, id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
    }

    private void Save() throws ManagerSaveException {

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
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
        int epicId1 = 0;
        TaskKind taskKind = getType(task);
        if (TaskKind.SUB_TASK.equals(taskKind)) {
            SubTask subTask = (SubTask) task;
            epicId1 = subTask.getEpicId();
        }

        return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), taskKind, task.getName(), task.getStaus(), task.getDescr(), epicId1);
    }

    private TaskKind getType(Task task) {
        if (task instanceof SubTask)
            return TaskKind.SUB_TASK;
        else if (task instanceof Epic)
            return TaskKind.EPIC;
        else
            return TaskKind.TASK;
    }
}
