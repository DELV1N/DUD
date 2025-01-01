package com.delv1n.dud;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.delv1n.dud.tasks.Task;
import com.delv1n.dud.tasks.TaskService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private DateTaskView dateTaskView;
    private TaskService taskService;
    private LocalDateTime selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskService = new TaskService(this);
        dateTaskView = findViewById(R.id.dateTaskView);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);
            updateTasksForDate(selectedDate);
        });
        updateTasksForDate(Instant.ofEpochMilli(calendarView.getDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .toLocalDate()
                .atStartOfDay());
    }

    private void updateTasksForDate(LocalDateTime date) {
        // Обновляем дату в DateTaskView
        dateTaskView.setDateText(date.toString());

        // Загружаем задачи из базы
        taskService.getTasksByDate(date, tasks -> runOnUiThread(() -> {
            List<String> taskDescriptions = new ArrayList<>();
            for (Task task : tasks) {
                taskDescriptions.add(task.name + " | " + task.date + " | " + task.type);
            }
            dateTaskView.getAdapter().setTasks(taskDescriptions);
        }));
    }
}