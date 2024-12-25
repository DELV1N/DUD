package com.delv1n.dud;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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

        // Инициализация DateTaskView
        DateTaskView dateTaskView = findViewById(R.id.dateTaskView);

        // Передача выбранной даты
        dateTaskView.setOnDateSelectedListener(() -> {
            // Здесь вместо `getSelectedDateFromCalendar()` напишите логику получения даты из календаря
            return getSelectedDateFromCalendar();
        });
    }

    private LocalDateTime getSelectedDateFromCalendar() {
        // Логика получения выбранной даты из календаря
        CalendarView calendarView = findViewById(R.id.calendarView);
        LocalDateTime date = Instant.ofEpochMilli(calendarView.getDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .toLocalDate()
                .atStartOfDay();
        return date; // Например, возвращаем фиксированную дату
    }
}