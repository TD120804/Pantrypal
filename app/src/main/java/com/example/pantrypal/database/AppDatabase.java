package com.example.pantrypal.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.pantrypal.models.GroceryItem;

@Database(entities = {GroceryItem.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract GroceryDao groceryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "grocery_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
