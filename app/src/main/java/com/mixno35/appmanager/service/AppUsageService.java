package com.mixno35.appmanager.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mixno35.appmanager.R;
import com.mixno35.appmanager.data.Data;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("ProtectedPermissions")
public class AppUsageService extends Service {

    private UsageStatsManager usageStatsManager;
    private long lastTime;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AppUsageServiceChannel";

    SharedPreferences prefs;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        lastTime = System.currentTimeMillis();
        createNotificationChannel();
        startTracking();
        prefs = getSharedPreferences(Data.PREFS_NAME(getApplicationContext()), Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("stop_service")) {
                stopTracking();
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopTracking();
        super.onDestroy();
    }

    private void startTracking() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    executor.execute(() -> updateUsageStats());
                } catch (Exception ignored) {}
                try {
                    handler.postDelayed(this, 10000);
                } catch (Exception ignored) {}
            }
        }, 0);
    }

    private void stopTracking() {
        executor.shutdown();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateUsageStats() {
        long currentTime = System.currentTimeMillis();

        try {
            List<UsageStats> statsYEARLY = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, lastTime, currentTime);
            List<UsageStats> statsMONTHLY = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, lastTime, currentTime);
            List<UsageStats> statsWEEKLY = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, lastTime, currentTime);
            List<UsageStats> statsDAILY = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, lastTime, currentTime);

            if (statsYEARLY != null) {
                for (UsageStats usageStats : statsYEARLY) {
                    String packageName = usageStats.getPackageName();
                    long timeInForeground = usageStats.getTotalTimeInForeground();

                    prefs.edit().putLong("timeInY." + packageName, timeInForeground).apply();
                }
            } if (statsMONTHLY != null) {
                for (UsageStats usageStats : statsMONTHLY) {
                    String packageName = usageStats.getPackageName();
                    long timeInForeground = usageStats.getTotalTimeInForeground();

                    prefs.edit().putLong("timeInM." + packageName, timeInForeground).apply();
                }
            } if (statsWEEKLY != null) {
                for (UsageStats usageStats : statsWEEKLY) {
                    String packageName = usageStats.getPackageName();
                    long timeInForeground = usageStats.getTotalTimeInForeground();

                    prefs.edit().putLong("timeInW." + packageName, timeInForeground).apply();
                }
            } if (statsDAILY != null) {
                for (UsageStats usageStats : statsDAILY) {
                    String packageName = usageStats.getPackageName();
                    long timeInForeground = usageStats.getTotalTimeInForeground();

                    if (prefs.getLong("timeInD." + packageName, 0) < timeInForeground) showNotification(packageName, timeInForeground);
                    prefs.edit().putLong("timeInD." + packageName, timeInForeground).apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastTime = currentTime;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    @SuppressLint({"UnspecifiedImmutableFlag", "StringFormatMatches", "StringFormatInvalid"})
    private void showNotification(String packageName, long timeInForeground) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.text_used_app, packageName))
                .setContentText(getString(R.string.message_used_app, Data.formatMillisToDHMS(timeInForeground)))
                .setSmallIcon(R.drawable.baseline_adb_24)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @SuppressLint("WrongConstant")
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_app_usage);
        int importance = NotificationManager.IMPORTANCE_MIN;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}