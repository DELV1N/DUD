package com.delv1n.dud;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delv1n.dud.tasks.Task;
import com.delv1n.dud.tasks.TaskService;
import com.google.android.material.tabs.TabLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private DateTaskView dateTaskView;
    private TaskService taskService;
    public LinearLayout listContainer;
    public LocalDateTime lastSelectedDate;

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
        listContainer = findViewById(R.id.listContainer);

        setupTabs();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            lastSelectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);
            updateTasksForDate(lastSelectedDate);
        });

        lastSelectedDate = Instant.ofEpochMilli(calendarView.getDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .toLocalDate()
                .atStartOfDay();

        updateTasksForDate(lastSelectedDate);
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // Calendar Tab
                    calendarView.setVisibility(View.VISIBLE);
                    dateTaskView.setVisibility(View.VISIBLE);
                    listContainer.setVisibility(View.GONE);
                    updateTasksForDate(lastSelectedDate);
                } else { // List Tab
                    calendarView.setVisibility(View.GONE);
                    dateTaskView.setVisibility(View.GONE);
                    listContainer.setVisibility(View.VISIBLE);
                    loadGroupedTasks();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void updateTasksForDate(LocalDateTime date) {
        // Обновляем дату в DateTaskView
        dateTaskView.setDateText(date.format(DateTimeFormatter.ofPattern("d MMMM")));
        dateTaskView.selectedDate = date;

        // Загружаем задачи из базы
        taskService.getTasksByDate(date, tasks -> runOnUiThread(() -> {
            dateTaskView.getAdapter().setTasks(tasks);
        }));
    }

    public void loadGroupedTasks() {
        taskService.getGroupedTasks(groupedTasks -> MainActivity.this.runOnUiThread(() -> {
            listContainer.removeAllViews();
            groupedTasks.forEach((date, tasks) -> {
                DateTaskView dateTaskViewItem = new DateTaskView(this);
                dateTaskViewItem.setDateText(date.format(DateTimeFormatter.ofPattern("d MMMM")));
                dateTaskViewItem.selectedDate = date.atStartOfDay();
                dateTaskViewItem.getAdapter().setTasks(tasks);
                listContainer.addView(dateTaskViewItem);
            });
            removeEmptyDateTaskViews();
        }));
    }

    public void removeEmptyDateTaskViews() {
        if (listContainer.getVisibility() == View.VISIBLE) {
            for (int i = listContainer.getChildCount() - 1; i >= 0; i--) {
                DateTaskView dateTaskView = (DateTaskView) listContainer.getChildAt(i);
                if (dateTaskView.getAdapter().getItemCount() == 0) {
                    listContainer.removeViewAt(i);
                }
            }
        }
    }
}