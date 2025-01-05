package com.delv1n.dud.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.delv1n.dud.tasks.Task;
import com.delv1n.dud.tasks.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "TaskReminderChannel";
    private TaskService taskService;
    private ExecutorService executor;

    @Override
    public void onCreate() {
        super.onCreate();
        taskService = new TaskService(this);
        executor = Executors.newSingleThreadExecutor();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Проверяем, есть ли в Intent taskId
        int taskId = intent.getIntExtra("taskId", -1); // Получаем taskId из Intent

        if (taskId != -1) {
            // Если taskId присутствует, отменяем уведомление для этой задачи
            cancelNotification(taskId);
        } else {
            // Если taskId нет, продолжаем обработку уведомлений
            checkAndScheduleNotifications();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for tasks.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void checkAndScheduleNotifications() {
        executor.execute(() -> {
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            taskService.getAllTasks(tasks -> {
                for (Task task : tasks) {
                    if (task.remindMe) {
                        LocalDateTime taskDateTime = task.date;

                        // Проверяем условия
                        if (taskDateTime.isAfter(now.plusHours(1)) && taskDateTime.toLocalDate().isEqual(today)) {
                            scheduleNotification(task);
                        }
                    }
                }
            });
        });
    }

    public void scheduleNotification(Task task) {
        LocalDateTime notificationTime = task.date.minusHours(1);
        long triggerAtMillis = notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("task_name", task.name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                task.id, // уникальный идентификатор для задачи
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    public void cancelNotification(int taskId) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                taskId, // Используем идентификатор задачи
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
