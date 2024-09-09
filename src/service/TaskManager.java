package service;

import enums.Status;
import enums.TaskKind;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    SubTask addSubTask(SubTask subTask, int idEpic);

    //    Получение списка всех задач
//    ==============================
    List<Task> readTasks();

    List<Epic> readEpics();

    List<SubTask> readSubTasks();

    ArrayList<SubTask> readSubTasksInEpic(Epic epic);

    //    Удаление всех задач
    void clearTasks(TaskKind taskKind);

    // Удаление одной задачи
    void removeTask(TaskKind taskKind, int id);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);
    //  просмотр задач
    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);


    //    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    ==============================
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);
//    =================================

    List<Task> getHistory();
}
