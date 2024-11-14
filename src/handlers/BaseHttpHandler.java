package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import enums.TaskKind;
import enums.Url;
import exceptions.ManagerSaveException;
import exceptions.TaskNotFoundException;
import model.Epic;
import model.SubTask;
import model.Task;
import service.HttpTaskServer;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class BaseHttpHandler {

    protected static final int CODE_WRONG_ID = 500;
    protected static final int CODE_SERVER_ERROR = 500;

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected TaskKind taskKind;
    protected Url url;
    protected TaskManager taskManager;

    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, TaskKind taskKind, Url url) {
        this.taskKind = taskKind;
        this.url = url;
        this.taskManager = taskManager;

        gson = HttpTaskServer.getGson();
    }

    public void handleTasks(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);

        if (endpoint.equals(Endpoint.UNKNOWN)) {
            sendTextWithCode(exchange, "Сервис не найден", 404);
            return;
        }

        switch (endpoint) {
            case GET_TASKS -> {
                readTasks(taskKind, exchange);
            }
            case GET_ONE_TASK -> {
                getTask(taskKind, exchange);
            }
            case UPDATE_TASK, CREATE_TASK -> {
                updateTask(taskKind, exchange);
            }
            case DELETE_TASK -> {
                deleteTask(taskKind, exchange);
            }
            case GET_EPIC_SUBTASKS -> {
                getSubtasks(taskKind, exchange);
            }
            case GET_HISTORY -> {
                getHistory(exchange);
            }
            case GET_PRIORITIZED -> {
                getPioritized(exchange);
            }
        }
    }

    private void updateTask(TaskKind taskKind, HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        switch (taskKind) {
            case TASK -> {
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() == 0) {
                    taskManager.addTask(task);
                } else {
                    taskManager.updateTask(task);
                }
            }
            case EPIC -> {
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
                } else {
                    taskManager.updateEpic(epic);
                }
            }
            case SUB_TASK -> {
                SubTask subTask = gson.fromJson(body, SubTask.class);
                if (subTask.getId() == 0) {
                    taskManager.addSubTask(subTask, subTask.getEpicId());
                } else {
                    taskManager.updateSubTask(subTask);
                }
            }
        }

        writeResponse(exchange, "Операция обработана", 200);
    }

    private void getSubtasks(TaskKind taskKind, HttpExchange exchange) throws IOException {
        int rCode = 200;
        Optional<Integer> id;

        if (!taskKind.equals(TaskKind.EPIC)) {
            sendTextWithCode(exchange, "Данная функциональность только для эпиков", CODE_WRONG_ID);
            return;
        }

        id = getId(exchange);
        if (id.isPresent()) {
            try {
                taskManager.readSubTasksInEpic(taskManager.getEpic(id.get()));
            } catch (ManagerSaveException e) {
                sendTextWithCode(exchange, e.getMessage(), CODE_SERVER_ERROR);
                return;
            }
        } else {
            sendTextWithCode(exchange, "Неправильный формат ID", CODE_WRONG_ID);
            return;
        }

        sendTextWithCode(exchange, "Операция обработана", rCode);
    }

    private void getPioritized(HttpExchange exchange) throws IOException {
        int rCode = 200;
        TreeSet<Task> tasks = taskManager.getPrioritizedTasks();

        String response;
        try {
            response = gson.toJson(tasks);
        } catch (Throwable e) {
            response = e.getMessage();
            rCode = CODE_SERVER_ERROR;
        }

        writeResponse(exchange, response, rCode);
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        int rCode = 200;
        List<Task> tasks = taskManager.getHistory();

        String response;
        try {
            response = gson.toJson(tasks);
        } catch (Throwable e) {
            response = e.getMessage();
            rCode = CODE_SERVER_ERROR;
        }

        writeResponse(exchange, response, rCode);
    }

    private void deleteTask(TaskKind taskKind, HttpExchange exchange) throws IOException {
        int rCode = 200;
        Optional<Integer> id;

        id = getId(exchange);
        if (id.isPresent()) {
            try {
                taskManager.removeTask(taskKind, id.get());
            } catch (ManagerSaveException e) {
                sendTextWithCode(exchange, e.getMessage(), CODE_SERVER_ERROR);
                return;
            }
        } else {
            sendTextWithCode(exchange, "Неправильный формат ID", CODE_WRONG_ID);
            return;
        }

        sendTextWithCode(exchange, "Операция обработана", rCode);
    }

    private void readTasks(TaskKind taskKind, HttpExchange exchange) throws IOException {
        int rCode = 200;

        String response;

        List<? extends Task> tasks = null;

        switch (taskKind) {
            case TASK -> {
                tasks = taskManager.readTasks();
            }
            case EPIC -> {
                tasks = taskManager.readEpics();
            }
            case SUB_TASK -> {
                tasks = taskManager.readSubTasks();
            }
        }

        try {
            response = gson.toJson(tasks);
        } catch (Throwable e) {
            response = e.getMessage();
            rCode = CODE_SERVER_ERROR;
        }

        writeResponse(exchange, response, rCode);
    }

    private void sendTextWithCode(HttpExchange exchange, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(rCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                Url url = Url.valueOf(pathParts[1]);
                switch (url) {
                    case tasks, epics, subtasks:
                        if (url == this.url) {
                            return Endpoint.GET_TASKS;
                        }
                    case history:
                        return Endpoint.GET_HISTORY;
                    case prioritized:
                        return Endpoint.GET_PRIORITIZED;
                }
            } else if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_TASK;
            }
        }

        if (pathParts.length == 3 && url.equals(Url.valueOf(pathParts[1]))) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET_ONE_TASK;
                case "POST":
                    return Endpoint.UPDATE_TASK;
                case "DELETE":
                    return Endpoint.DELETE_TASK;
            }
        }

        if (pathParts.length == 4
                && pathParts[1].equals("epics")
                && pathParts[3].equals("subtasks")
                && url.equals(Url.epics)) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }

        return Endpoint.UNKNOWN;
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void getTask(TaskKind taskKind, HttpExchange exchange) throws IOException {
        Task task;
        int rCode = 200;

        Optional<Integer> id = getId(exchange);
        if (id.isPresent()) {
            try {
                task = switch (taskKind) {
                    case TASK -> taskManager.getTask(id.get());
                    case EPIC -> taskManager.getEpic(id.get());
                    case SUB_TASK -> taskManager.getSubTask(id.get());
                };
            } catch (TaskNotFoundException e) {
                sendTextWithCode(exchange, e.getMessage(), 404);
                return;
            }
        } else {
            sendTextWithCode(exchange, "Неправильный формат ID", CODE_WRONG_ID);
            return;
        }

        String response;
        try {
            response = gson.toJson(task);
        } catch (Throwable e) {
            response = e.getMessage();
            rCode = CODE_SERVER_ERROR;
        }

        writeResponse(exchange, response, rCode);
    }

//    protected void sendNotFound(HttpExchange exchange, String text) {
//        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
//        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
//        exchange.sendResponseHeaders(404, resp.length);
//        exchange.getResponseBody().write(resp);
//        exchange.close();
//    }
//
//    protected void sendHasInteractions(HttpExchange exchange, String text) {
//        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
//        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
//        exchange.sendResponseHeaders(404, resp.length);
//        exchange.getResponseBody().write(resp);
//        exchange.close();
//    }
}
