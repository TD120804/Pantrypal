package com.example.pantrypal.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.repository.GroceryRepository;

import java.util.List;

public class GroceryViewModel extends AndroidViewModel {

    private final GroceryRepository repository;
    private final LiveData<List<GroceryItem>> allItems;
    private final LiveData<List<GroceryItem>> expiringItems;

    public GroceryViewModel(@NonNull Application application) {
        super(application);
        repository = new GroceryRepository(application);
        allItems = repository.getAllItems();
        expiringItems = repository.getExpiringItems();
    }

    public LiveData<List<GroceryItem>> getAllItems() {
        return allItems;
    }

    public LiveData<List<GroceryItem>> getExpiringItems() {
        return expiringItems;
    }

    public void insert(GroceryItem item) {
        repository.insertOrUpdate(item);
    }

    public void update(GroceryItem item) {
        repository.update(item);
    }

    public void delete(GroceryItem item) {
        repository.delete(item);
    }

    // 🔥 This is what BarcodeScannerActivity needs
    public GroceryItem getItemByBarcodeSync(String barcode) {
        return repository.getItemByBarcodeSync(barcode);
    }
}
