package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskKind;
import enums.Url;
import service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager, TaskKind.TASK, Url.tasks);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleTasks(exchange);
    }
}
