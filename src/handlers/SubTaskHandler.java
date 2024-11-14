package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskKind;
import enums.Url;
import service.TaskManager;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager, TaskKind.SUB_TASK, Url.subtasks);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleTasks(exchange);
    }
}
