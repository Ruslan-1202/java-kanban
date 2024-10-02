package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    final private HashMap<Integer, Node<Task>> historyMap;
    private Node<Task> headNode;
    private Node<Task> tailNode;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public void add(Task task) {
        Integer id = task.getId();
        removeNode(historyMap.get(id));
        var newNode = linkLast(task);
        historyMap.put(id, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (headNode == null) {
            headNode = newNode;
            tailNode = newNode;
        } else {
            tailNode.next = newNode;
            newNode.prev = tailNode;
            tailNode = newNode;
        }

        return newNode;
    }

    public List<Task> getTasks () {
        Node<Task> node = headNode;
        List<Task> tasks = new ArrayList<>();

        while (node != null ) {
            tasks.add(node.data);
            node = node.next;
        }

        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (headNode == null || node == null)
            return;

        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            headNode = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tailNode = prev;
        }

    }
}

class Node<T> {
    T data;
    Node<T> prev;
    Node<T> next;

    public Node(T data) {
        this.data = data;
    }

    public Node(T data, Node<T> prev, Node<T> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
