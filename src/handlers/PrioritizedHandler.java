package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Url;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager, null, Url.prioritized);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleTasks(exchange);
    }
}
