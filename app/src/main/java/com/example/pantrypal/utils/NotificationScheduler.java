package com.example.pantrypal.utils;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.pantrypal.workers.ExpiryWorker;

import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    public static void schedule(Context context) {

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        ExpiryWorker.class,
                        15,
                        TimeUnit.MINUTES
                )
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        "ExpiryNotification",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                );
    }
}