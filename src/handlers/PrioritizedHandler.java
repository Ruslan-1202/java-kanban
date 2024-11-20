package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Url;
import service.TaskManager;

import java.util.Arrays;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager, null, Url.PRIORITIZED);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            handleTasks(exchange);
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
        }
    }
}
