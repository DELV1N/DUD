package com.delv1n.dud.tasks;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM tasks ORDER BY date")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE DATE(date) = DATE(:date) ORDER BY date")
    List<Task> getTasksByDate(String date);
}
