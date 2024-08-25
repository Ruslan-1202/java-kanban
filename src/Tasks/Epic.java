package Tasks;
import Enums.*;

import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, SubTask> subTasks;

    public Epic(String name, String descr, Status status, int id) {
        super(name, descr, status, id);
        subTasks = new HashMap<>();
    }

    public void addSubTask(int id, String name, String descr) {
        SubTask subTask = new SubTask(name, descr, Status.NEW, id, this.id);
        subTasks.put(id, subTask);
    }

    public void addSubTask(int id, SubTask subTask) {
        subTasks.put(id, subTask);
    }

    public HashMap<Integer, SubTask> readSubTasks() {
        return subTasks;
    }

    public void clearSubTasks() {
        subTasks.clear();
        calculateStatus();
    }

    public void calculateStatus() {
//        проверим, все ли DONE
        boolean isAllDone = true;
        boolean isAllNew = true;

        for (SubTask subTask: subTasks.values()) {
            if (!Status.DONE.equals(subTask.getStaus())) {
                isAllDone = false;
            }
            if (!Status.NEW.equals(subTask.getStaus())) {
                isAllNew = false;
            }
            if (!isAllDone && !isAllNew) break;
        }

        if (subTasks.isEmpty()) {
            isAllDone = false;
            isAllNew = true;
        }

        if (isAllDone) {
            this.status = Status.DONE;
        } else if (isAllNew) {
            this.status = Status.NEW;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
    }
}
