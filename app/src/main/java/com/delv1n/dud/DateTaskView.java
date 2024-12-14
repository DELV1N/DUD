package com.delv1n.dud;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Добавляем новую задачу в список
                adapter.addTask("New task " + (adapter.getItemCount() + 1));
            }
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
}