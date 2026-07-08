package com.example.pantrypal.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pantrypal.R;
import com.example.pantrypal.database.AppDatabase;
import com.example.pantrypal.models.GroceryItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;

public class NotificationHelper {

    private static final String CHANNEL_ID = "pantrypal_notifications";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PantryPal Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Expiry reminders");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null)
                manager.createNotificationChannel(channel);
        }
    }

    public static void checkExpiringItems(Context context) {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<GroceryItem> items =
                    AppDatabase.getInstance(context)
                            .groceryDao()
                            .getAllItemsSync();

            if (items == null || items.isEmpty())
                return;

            LocalDate today = LocalDate.now();

            GroceryItem nearest = null;
            long nearestDays = Long.MAX_VALUE;

            for (GroceryItem item : items) {

                try {

                    LocalDate expiry =
                            LocalDate.parse(item.getExpiryDate(), FORMATTER);

                    long days =
                            ChronoUnit.DAYS.between(today, expiry);

                    if (days >= 0 && days < nearestDays) {

                        nearest = item;
                        nearestDays = days;

                    }

                } catch (Exception ignored) {
                }

            }

            if (nearest == null)
                return;

            String message;

            if (nearestDays == 0) {

                message = nearest.getName() + " expires TODAY!";

            } else if (nearestDays == 1) {

                message = nearest.getName() + " expires tomorrow.";

            } else {

                message = nearest.getName()
                        + " expires in "
                        + nearestDays
                        + " days.";

            }

            showNotification(
                    context,
                    "🥕 PantryPal Reminder",
                    message
            );

        });

    }

    public static void showNotification(
            Context context,
            String title,
            String message
    ) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        try {

            NotificationManagerCompat
                    .from(context)
                    .notify(1001, builder.build());

        } catch (SecurityException ignored) {
        }

    }

}