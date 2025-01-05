package com.delv1n.dud.notifications;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.delv1n.dud.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "TaskReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("task_name");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getPackageManager().getApplicationLabel(context.getApplicationInfo()))
                .setContentText(context.getString(R.string.upcoming_task) + " " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
