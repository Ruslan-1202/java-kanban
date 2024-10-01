package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_HISTORY = 10;
    private LinkedList<Task> history;

    InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_HISTORY) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> copyHistory = (List) history.clone();
        return copyHistory;
    }
}
