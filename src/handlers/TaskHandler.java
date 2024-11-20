package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskKind;
import enums.Url;
import service.TaskManager;

import java.io.IOException;
import java.util.Arrays;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager, TaskKind.TASK, Url.TASKS);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            handleTasks(exchange);
        } catch (IOException e) {
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
        } catch (Exception e) {
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
        }
    }
}
