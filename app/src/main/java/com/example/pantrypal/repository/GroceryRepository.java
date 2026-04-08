package com.example.pantrypal.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.pantrypal.database.AppDatabase;
import com.example.pantrypal.database.GroceryDao;
import com.example.pantrypal.models.GroceryItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryRepository {

    private GroceryDao groceryDao;
    private LiveData<List<GroceryItem>> allItems;
    private LiveData<List<GroceryItem>> expiringItems;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GroceryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        groceryDao = db.groceryDao();
        allItems = groceryDao.getAllItems();
        expiringItems = groceryDao.getExpiringItems();
    }

    public LiveData<List<GroceryItem>> getAllItems() {
        return allItems;
    }

    public LiveData<List<GroceryItem>> getExpiringItems() {
        return expiringItems;
    }

    // 🚀 Replace insert() with automatic insert/update
    public void insertOrUpdate(GroceryItem item) {
        executor.execute(() -> {
            GroceryItem existing = groceryDao.getItemByBarcodeSync(item.getBarcode());
            if (existing != null) {
                groceryDao.increaseQuantity(item.getBarcode(), item.getQuantity());
            } else {
                groceryDao.insert(item);
            }
        });
    }

    public void update(GroceryItem item) {
        executor.execute(() -> groceryDao.update(item));
    }

    public void delete(GroceryItem item) {
        executor.execute(() -> groceryDao.delete(item));
    }

    public GroceryItem getItemByBarcodeSync(String barcode) {
        return groceryDao.getItemByBarcodeSync(barcode);
    }

}
