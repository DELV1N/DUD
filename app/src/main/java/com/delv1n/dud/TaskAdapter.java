package com.delv1n.dud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delv1n.dud.tasks.Task;
import com.delv1n.dud.tasks.TaskService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public List<Task> tasksList = new ArrayList<>();
    private final Context context;

    public TaskAdapter(@NonNull Context context) {
        this.context = context;
    }

    public void addTask(Task task) {
        tasksList.add(task);
        notifyItemInserted(tasksList.size() - 1);
    }

    // Метод для замены всех задач
    public void setTasks(List<Task> newTasks) {
        tasksList.clear();
        tasksList.addAll(newTasks);
        notifyDataSetChanged(); // Обновляем весь список
    }

    public void removeTask(int position) {
        if (position >= 0 && position < tasksList.size()) {
            tasksList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasksList.get(position);
        holder.textView.setText(String.format("%s | %s | %s", task.date.format(DateTimeFormatter.ofPattern("HH:mm")), task.name,task.type));

        // Обработчик удаления элемента
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                TaskService taskService = new TaskService(v.getContext());
                taskService.deleteTask(task.id);

                removeTask(currentPosition);

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.runOnUiThread(mainActivity::removeEmptyDateTaskViews);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            deleteButton = itemView.findViewById(R.id.imageView);
        }
    }
}
