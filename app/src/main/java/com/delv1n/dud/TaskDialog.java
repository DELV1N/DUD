package com.delv1n.dud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
    private final OnTaskAddedListener listener;

    private LocalDateTime selectedDate;

    public TaskDialog(Context context, LocalDateTime selectedDate, OnTaskAddedListener listener) {
        this.context = context;
        this.selectedDate = selectedDate;
        this.listener = listener;
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

        // Обработчик кнопки OK
        okButton.setOnClickListener(v -> {
            String taskName = taskNameInput.getText().toString();
            //String time = Integer.toString(taskTimeInput.getHour());
            boolean remind = remindSwitch.isChecked();
            String type = taskTypeSpinner.getSelectedItem().toString();
            int hour = taskTimeInput.getHour();
            int minute = taskTimeInput.getMinute();
            selectedDate = selectedDate.withHour(hour).withMinute(minute);

            // Передача данных в callback
            if (!taskName.isEmpty()) {
                TaskService taskService = new TaskService(context);
                taskService.addTask(taskName, selectedDate, remind, type);
                Toast.makeText(context, "Task added!", Toast.LENGTH_SHORT).show();
//                listener.onTaskAdded(taskName, time, remind, type);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик кнопки Cancel
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}
