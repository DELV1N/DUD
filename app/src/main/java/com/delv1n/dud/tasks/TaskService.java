package com.delv1n.dud.tasks;

import android.content.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskService {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskService(Context context) {
        TaskDatabase db = TaskDatabase.getInstance(context);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void addTask(String name, LocalDateTime date, boolean remindMe, String type) {
        executorService.execute(() -> {
            Task task = new Task(name, date, remindMe, type);
            taskDao.insert(task);
        });
    }

    public void deleteTask(int taskId) {
        executorService.execute(() -> taskDao.deleteById(taskId));
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

    public interface Callback<T> {
        void onComplete(T result);
    }
}