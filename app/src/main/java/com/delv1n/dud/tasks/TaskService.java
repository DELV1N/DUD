package com.delv1n.dud.tasks;

import android.content.Context;
import android.content.Intent;

import com.delv1n.dud.notifications.NotificationReceiver;
import com.delv1n.dud.notifications.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskDao taskDao;
    private final ExecutorService executorService;
    private final Context context;

    public TaskService(Context context) {
        TaskDatabase db = TaskDatabase.getInstance(context);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
        this.context = context;
    }

    public void addTask(String name, LocalDateTime date, boolean remindMe, String type) {
        executorService.execute(() -> {
            Task task = new Task(name, date, remindMe, type);
            taskDao.insert(task);
            if (task.remindMe) {
                Intent intent = new Intent(context, NotificationService.class);
                context.startService(intent);
            }
        });
    }

    public void deleteTask(int taskId) {
        executorService.execute(() -> taskDao.deleteById(taskId));
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("taskId", taskId); // передаем taskId для отмены уведомления
        context.startService(intent);
    }

    public void getAllTasks(Callback<List<Task>> callback) {
        executorService.execute(() -> {
            List<Task> tasks = taskDao.getAllTasks();
            callback.onComplete(tasks);
        });
    }

    public void getTasksByDate(LocalDateTime date, Callback<List<Task>> callback) {
        executorService.execute(() -> {
            List<Task> tasks = taskDao.getTasksByDate(date.toString());
            callback.onComplete(tasks);
        });
    }

    public void getGroupedTasks(Callback<Map<LocalDate, List<Task>>> callback) {
        executorService.execute(() -> {
            List<Task> tasks = taskDao.getAllTasks();
            Map<LocalDate, List<Task>> groupedTasks = tasks.stream()
                    .collect(Collectors.groupingBy(task -> task.date.toLocalDate(), TreeMap::new, Collectors.toList()));
            callback.onComplete(groupedTasks);
        });
    }

    public interface Callback<T> {
        void onComplete(T result);
    }
}