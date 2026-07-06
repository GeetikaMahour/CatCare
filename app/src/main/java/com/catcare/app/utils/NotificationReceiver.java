package com.catcare.app.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.catcare.app.R;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG        = "CatCare";
    private static final String CHANNEL_ID = "catcare_v4";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title     = intent.getStringExtra("title");
        String message   = intent.getStringExtra("message");
        int    requestId = intent.getIntExtra("request_id", 0);
        int    hour      = intent.getIntExtra("hour", -1);
        int    minute    = intent.getIntExtra("minute", 0);

        Log.d(TAG, "Notification received: " + title + " requestId=" + requestId);

        createChannel(context);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_cat_placeholder)
                        .setContentTitle(title != null ? title : "CatCare 🐾")
                        .setContentText(message != null ? message : "Time to care for your cat!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
            Log.d(TAG, "Notification posted!");
        }

        // Reschedule for next day if this is a feeding reminder
        if (hour >= 0 && requestId != 0) {
            Log.d(TAG, "Rescheduling for next day at " + hour + ":" + minute);
            rescheduleNextDay(context, intent, requestId, hour, minute);
        }
    }

    private void rescheduleNextDay(Context context, Intent intent,
                                   int requestId, int hour, int minute) {
        Calendar next = Calendar.getInstance();
        next.add(Calendar.DAY_OF_YEAR, 1);
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);

        PendingIntent pi = PendingIntent.getBroadcast(context,
                requestId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
                } else {
                    am.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
                }
            } else {
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
            }
        } catch (Exception e) {
            Log.e(TAG, "Reschedule error: " + e.getMessage());
        }
    }

    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "CatCare Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Feeding & vet reminders");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 100, 300});
            NotificationManager nm =
                    context.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}