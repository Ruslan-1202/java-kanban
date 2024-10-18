package service;

import enums.TaskKind;
import exceptions.ManagerReadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.*;

import static service.Managers.addTaskFromFile;
import static service.ParseUtils.fromString;
import static service.ParseUtils.parseToString;

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
        int id = subTask.getId();

        subTasks.put(id, subTask);
        Epic epic = epics.get(subTask.getEpicId());
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
                fileWriter.write(parseToString(task));
            }

            for (Epic epic : this.readEpics()) {
                fileWriter.write(parseToString(epic));
            }

            for (SubTask subTask : this.readSubTasks()) {
                fileWriter.write(parseToString(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    //Если опять какой-то метод надо будет делать статическим и в другом месте, просьба объяснить,
    // что именно имеется ввиду
    public FileBackedTaskManager loadFromFile() throws ManagerReadException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                Task task = fromString(br.readLine());
                addTaskFromFile(this, task);
            }
        } catch (IOException e) {
            throw new ManagerReadException();
        }
        return this;
    }
}
