package com.delv1n.dud;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delv1n.dud.tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DateTaskView extends ConstraintLayout {

    private TextView textViewDate;
    private RecyclerView recyclerView;
    private ImageButton addButton;
    private TaskAdapter adapter;

    public LocalDateTime selectedDate;

    public DateTaskView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DateTaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTaskView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // Инициализация пользовательского вида
    private void init(Context context) {
        // "Раздуваем" разметку
        LayoutInflater.from(context).inflate(R.layout.date_task_item, this, true);

        // Привязываем элементы интерфейса
        textViewDate = findViewById(R.id.textView2);
        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.imageButton2);

        // Инициализируем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new TaskAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Устанавливаем обработчик для кнопки добавления
        addButton.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(getContext(), selectedDate, (taskName, time, remind, type) -> {
                // Добавляем задачу в адаптер RecyclerView
                Task task = new Task(taskName, LocalDateTime.parse(time), remind, type);
                adapter.addTask(task);
            });
            taskDialog.show();
        });
    }

    // --- Методы для работы с элементами ---
    public void setDateText(String date) {
        textViewDate.setText(date);
    }

    public String getDateText() {
        return textViewDate.getText().toString();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    // --- Listener для кнопки добавления ---
    private OnAddButtonClickListener onAddButtonClickListener;

    public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
        this.onAddButtonClickListener = listener;
    }

    public interface OnAddButtonClickListener {
        void onAddClick();
    }

    // Добавить метод для получения адаптера
    public TaskAdapter getAdapter() {
        return adapter;
    }
}