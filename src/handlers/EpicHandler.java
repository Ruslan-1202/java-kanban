package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskKind;
import enums.Url;
import service.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager, TaskKind.EPIC, Url.EPICS);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleTasks(exchange);
    }
}
