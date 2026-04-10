package com.example.pantrypal.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.google.android.material.card.MaterialCardView;

public class EditGroceryActivity extends AppCompatActivity {

    private GroceryViewModel viewModel;

    private EditText edtName, edtExpiry, edtNotes, edtBarcode;
    private TextView textQuantity;
    private Spinner spinnerCategory;

    private MaterialCardView btnSave;

    private GroceryItem currentItem;

    private int quantity = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        // 🔥 NEW UI REFERENCES
        edtName = findViewById(R.id.editTextName);
        edtExpiry = findViewById(R.id.editTextExpiry);
        edtNotes = findViewById(R.id.editTextNotes);
        edtBarcode = findViewById(R.id.editTextBarcode);

        textQuantity = findViewById(R.id.textQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        TextView btnPlus = findViewById(R.id.btnPlus);
        TextView btnMinus = findViewById(R.id.btnMinus);

        btnSave = findViewById(R.id.buttonSave);

        // 🔥 SET BUTTON TEXT
        ((TextView) btnSave.getChildAt(0)).setText("Update Item");

        // 🔥 SPINNER DATA (same as Add screen)
        String[] categories = {"Dairy", "Fruits", "Vegetables", "Snacks", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        spinnerCategory.setAdapter(adapter);

        // 🔥 RECEIVE ITEM
        currentItem = getIntent().getParcelableExtra("item");

        if (currentItem == null) {
            Toast.makeText(this, "Error: No item received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔥 PREFILL DATA
        edtName.setText(currentItem.getName());
        edtExpiry.setText(currentItem.getExpiryDate());
        edtNotes.setText(currentItem.getNotes());
        edtBarcode.setText(currentItem.getBarcode());

        quantity = currentItem.getQuantity();
        textQuantity.setText(String.valueOf(quantity));

        // set category spinner
        int pos = adapter.getPosition(currentItem.getCategory());
        spinnerCategory.setSelection(pos);

        // 🔥 COUNTER LOGIC
        btnPlus.setOnClickListener(v -> {
            quantity++;
            textQuantity.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                textQuantity.setText(String.valueOf(quantity));
            }
        });

        // 🔥 SAVE
        btnSave.setOnClickListener(v -> updateItem());
    }

    private void updateItem() {

        String name = edtName.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();
        String barcode = edtBarcode.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this, "Name & Expiry required", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 APPLY CHANGES
        currentItem.setName(name);
        currentItem.setQuantity(quantity);
        currentItem.setCategory(category);
        currentItem.setExpiryDate(expiry);
        currentItem.setNotes(notes);
        currentItem.setBarcode(barcode);

        viewModel.update(currentItem);

        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}