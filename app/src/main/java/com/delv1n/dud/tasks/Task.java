package com.delv1n.dud.tasks;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    @TypeConverters(DateTimeConverter.class)
    public LocalDateTime date;

    public boolean remindMe;
    public String type;

    public Task(String name, LocalDateTime date, boolean remindMe, String type) {
        this.name = name;
        this.date = date;
        this.remindMe = remindMe;
        this.type = type;
    }
}
