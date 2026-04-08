package com.example.pantrypal.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;

public class EditGroceryActivity extends AppCompatActivity {

    private GroceryViewModel viewModel;

    private EditText edtName, edtQuantity, edtCategory, edtExpiry, edtNotes;
    private Button btnSave;

    private GroceryItem currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        edtName     = findViewById(R.id.editTextName);
        edtQuantity = findViewById(R.id.editTextQuantity);
        edtCategory = findViewById(R.id.editTextCategory);
        edtExpiry   = findViewById(R.id.editTextExpiry);
        edtNotes    = findViewById(R.id.editTextNotes);
        btnSave     = findViewById(R.id.buttonSave);

        btnSave.setText("Update Item");

        // Receive the item via Intent
        currentItem = getIntent().getParcelableExtra("item");

        if (currentItem == null) {
            Toast.makeText(this, "Error: No item received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Pre-fill fields
        edtName.setText(currentItem.getName());
        edtQuantity.setText(String.valueOf(currentItem.getQuantity()));
        edtCategory.setText(currentItem.getCategory());
        edtExpiry.setText(currentItem.getExpiryDate());
        edtNotes.setText(currentItem.getNotes());

        btnSave.setOnClickListener(v -> updateItem());
    }

    private void updateItem() {
        String name = edtName.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityText)) {
            Toast.makeText(this, "Name and Quantity are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (Exception e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Apply changes
        currentItem.setName(name);
        currentItem.setQuantity(quantity);
        currentItem.setCategory(category);
        currentItem.setExpiryDate(expiry);
        currentItem.setNotes(notes);

        viewModel.update(currentItem);
        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
