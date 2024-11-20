package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import enums.Status;
import enums.TaskKind;
import handlers.*;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.addTask(new Task("Task 1", "Descr task 1"));
        System.out.println(task1.getId());

        Epic epic1 = taskManager.addEpic(new Epic("Epic 1", "Descr epic 1", 2,
                Status.NEW, null, Duration.ofMinutes(2)));

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        String taskJson = HttpTaskServer.getGson().toJson(task1);

        httpTaskServer.start();

    }

    public static Gson getGson() {

        LocalDateTimeTypeAdapter localTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();

        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localTimeTypeAdapter)
                .registerTypeAdapter(Duration.class, durationTypeAdapter)
                .create();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        // связываем конкретный путь и его обработчик
        createContexts();

    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    private void createContexts() {
//        /TASKS, /subtasks, /EPICS, /history и /prioritized

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));

        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }


    private static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localTime) throws IOException {
            if (localTime == null) {
                jsonWriter.nullValue();
                return;
            }

            jsonWriter.value(localTime.format(timeFormatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
        }
    }

    private static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }

            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            return Duration.ofMinutes(jsonReader.nextLong());
        }
    }

    private static class TaskListTypeToken extends TypeToken<List<Task>> {

    }

    private static class EpicListTypeToken extends TypeToken<List<Epic>> {

    }

    private static class SubTaskListTypeToken extends TypeToken<List<SubTask>> {

    }

    public static Type getTaskType(TaskKind taskKind) {
        switch (taskKind) {
            case TASK:
                return new TaskListTypeToken().getType();
            case EPIC:
                return new EpicListTypeToken().getType();
            case SUB_TASK:
                return new SubTaskListTypeToken().getType();
            default:
                return null;
        }
    }
}
