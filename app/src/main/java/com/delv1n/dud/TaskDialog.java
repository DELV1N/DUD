package com.delv1n.dud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.delv1n.dud.tasks.TaskService;

import java.time.LocalDateTime;

public class TaskDialog {

    public interface OnTaskAddedListener {
        void onTaskAdded(String taskName, String time, boolean remind, String type);
    }

    private final Context context;
    private final LocalDateTime currentDate;
    private final OnTaskAddedListener listener;

    public TaskDialog(Context context, LocalDateTime currentDate, OnTaskAddedListener listener) {
        this.context = context;
        this.currentDate = currentDate;
        this.listener = listener;
    }

    // Метод для проверки, есть ли разрешение на уведомления
    private boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.areNotificationsEnabled();
        }
        return true;  // Для более старых версий считаем, что разрешение есть
    }

    public void show() {
        // Создаём диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_dialog, null);
        builder.setView(view);

        // Инициализация элементов
        EditText taskNameInput = view.findViewById(R.id.taskNameInput);
        TimePicker taskTimeInput = view.findViewById(R.id.taskTimeInput);
        Switch remindSwitch = view.findViewById(R.id.remindMeSwitch);
        Spinner taskTypeSpinner = view.findViewById(R.id.taskTypeSpinner);
        Button okButton = view.findViewById(R.id.okButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        // Создаём и показываем диалог
        Dialog dialog = builder.create();
        dialog.show();

        taskTimeInput.setIs24HourView(true);

        // Обработчик для изменения состояния Switch
        remindSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Проверяем, есть ли разрешение на отправку уведомлений
                if (!isNotificationPermissionGranted(context)) {
                    // Если нет разрешения, показываем диалог с просьбой активировать его
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.permission_title))
                            .setMessage(context.getString(R.string.permission_message))
                            .setPositiveButton(context.getString(R.string.go_to_settings), (dialog1, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                                context.startActivity(intent);
                            })
                            .setNegativeButton(context.getString(R.string.cancel), (dialog1, which) -> {
                                // Если пользователь отказывается, можно деактивировать Switch обратно
                                remindSwitch.setChecked(false);
                            })
                            .show();
                }
            }
        });

        // Обработчик кнопки OK
        okButton.setOnClickListener(v -> {
            String taskName = taskNameInput.getText().toString();
            LocalDateTime time = currentDate.withHour(taskTimeInput.getHour()).withMinute(taskTimeInput.getMinute());
            boolean remind = remindSwitch.isChecked();
            String type = taskTypeSpinner.getSelectedItem().toString();

            // Передача данных в callback
            if (!taskName.isEmpty()) {
                TaskService taskService = new TaskService(context);
                taskService.addTask(taskName, time, remind, type);
                dialog.dismiss();
                MainActivity mainActivity = (MainActivity) context;
                if (mainActivity.listContainer.getVisibility() != View.VISIBLE)
                    mainActivity.updateTasksForDate(time.toLocalDate().atStartOfDay());
                else
                    mainActivity.loadGroupedTasks();
            } else {
                Toast.makeText(context, context.getString(R.string.empty_task_name), Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик кнопки Cancel
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}
