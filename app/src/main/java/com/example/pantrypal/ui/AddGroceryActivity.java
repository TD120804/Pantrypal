package com.example.pantrypal.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AddGroceryActivity extends AppCompatActivity {
    private EditText editName, editQty, editCategory, editExpiry, editNotes;
    private GroceryViewModel viewModel;

    private String scannedBarcode = "";
    private int editItemId = -1;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        editName = findViewById(R.id.editTextName);
        editQty = findViewById(R.id.editTextQuantity);
        editCategory = findViewById(R.id.editTextCategory);
        editExpiry = findViewById(R.id.editTextExpiry);
        editNotes = findViewById(R.id.editTextNotes);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Date picker
        editExpiry.setFocusable(false);
        editExpiry.setClickable(true);
        editExpiry.setOnClickListener(v -> showDatePicker());

        // Check edit mode
        editItemId = getIntent().getIntExtra("EDIT_ITEM_ID", -1);

        if (editItemId != -1) {
            setTitle("Edit Item");
            loadItemData(editItemId);
        } else {
            setTitle("Add Item");
        }

        // Barcode (optional now)
        if (getIntent().hasExtra("barcode")) {
            scannedBarcode = getIntent().getStringExtra("barcode");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    private void loadItemData(int id) {

        viewModel.getAllItems().observe(this, list -> {
            for (GroceryItem item : list) {
                if (item.getId() == id) {

                    editName.setText(item.getName());
                    editQty.setText(String.valueOf(item.getQuantity()));
                    editCategory.setText(item.getCategory());
                    editExpiry.setText(item.getExpiryDate());
                    editNotes.setText(item.getNotes());

                    scannedBarcode = item.getBarcode(); // preserve barcode
                    break;
                }
            }
        });
    }

    private void showDatePicker() {

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Expiry Date")
                        .setCalendarConstraints(constraints)
                        .build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {

            LocalDate selectedDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            editExpiry.setText(selectedDate.format(formatter));
        });
    }

    private void saveItem() {

        String name = editName.getText().toString().trim();
        String qtyStr = editQty.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this, "Name, Quantity & Expiry required", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = Integer.parseInt(qtyStr);

        if (editItemId != -1) {
            // UPDATE MODE
            GroceryItem updatedItem = new GroceryItem(
                    name, qty, category, expiry, notes, scannedBarcode
            );
            updatedItem.setId(editItemId);

            viewModel.update(updatedItem);

            Toast.makeText(this, "Item Updated!", Toast.LENGTH_SHORT).show();

        } else {
            // INSERT MODE (barcode optional now)
            GroceryItem newItem = new GroceryItem(
                    name,
                    qty,
                    category,
                    expiry,
                    notes,
                    scannedBarcode == null ? "" : scannedBarcode
            );

            viewModel.insert(newItem);

            Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
