package com.example.pantrypal.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pantrypal.utils.NotificationHelper;

public class ExpiryWorker extends Worker {

    public ExpiryWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @Override
    @NonNull
    public Result doWork() {

        NotificationHelper.checkExpiringItems(getApplicationContext());

        return Result.success();
    }
}