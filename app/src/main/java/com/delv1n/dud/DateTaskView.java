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

import java.time.LocalDateTime;

public class DateTaskView extends ConstraintLayout {

    private TextView textViewDate;
    private RecyclerView recyclerView;
    private ImageButton addButton;
    private TaskAdapter adapter;

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
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        // Устанавливаем обработчик для кнопки добавления
        addButton.setOnClickListener(v -> {
//            TaskDialog taskDialog = new TaskDialog(getContext(), (taskName, time, remind, type) -> {
//                // Добавляем задачу в адаптер RecyclerView
//                adapter.addTask(taskName + " | " + time + " | " + type);
//            });
            showTaskDialog();
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

    public interface OnDateSelectedListener {
        LocalDateTime onDateSelected();
    }

    private OnDateSelectedListener onDateSelectedListener;

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }

    public void showTaskDialog() {
        if (onDateSelectedListener != null) {
            LocalDateTime selectedDate = onDateSelectedListener.onDateSelected();
            TaskDialog taskDialog = new TaskDialog(getContext(), selectedDate, (taskName, time, remind, type) -> {
                // Добавляем задачу в адаптер RecyclerView
                adapter.addTask(taskName + " | " + selectedDate + " | " + time + " | " + type);
            });
            taskDialog.show();
        }
    }
}