package com.example.pantrypal.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;

@Dao
public interface GroceryDao {

    @Insert
    void insert(GroceryItem item);

    @Update
    void update(GroceryItem item);

    @Delete
    void delete(GroceryItem item);

    @Query("SELECT * FROM grocery_table ORDER BY expiry_date ASC")
    LiveData<List<GroceryItem>> getAllItems();

    @Query("SELECT * FROM grocery_table WHERE expiry_date <= date('now','+7 day') ORDER BY expiry_date ASC")
    LiveData<List<GroceryItem>> getExpiringItems();

    @Query("SELECT * FROM grocery_table WHERE barcode = :barcode LIMIT 1")
    GroceryItem getItemByBarcodeSync(String barcode);

    // 🔥 NEW — directly increase quantity for duplicates
    @Query("UPDATE grocery_table SET quantity = quantity + :qty WHERE barcode = :barcode")
    void increaseQuantity(String barcode, int qty);
}

