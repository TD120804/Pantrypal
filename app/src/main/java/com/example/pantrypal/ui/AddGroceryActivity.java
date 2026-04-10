package com.example.pantrypal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.scanner.BarcodeScannerActivity;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AddGroceryActivity extends AppCompatActivity {

    // UI
    private EditText editName, editExpiry, editNotes, editBarcode;
    private TextView textQuantity;
    private Spinner spinnerUnit, spinnerCategory;

    private View btnBranded, btnLoose;
    private View layoutBarcode;

    private GroceryViewModel viewModel;

    private int quantity = 1;
    private int editItemId = -1;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        // 🔥 INIT VIEWS
        editName = findViewById(R.id.editTextName);
        editExpiry = findViewById(R.id.editTextExpiry);
        editNotes = findViewById(R.id.editTextNotes);
        editBarcode = findViewById(R.id.editTextBarcode);

        textQuantity = findViewById(R.id.textQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        TextView btnPlus = findViewById(R.id.btnPlus);
        TextView btnMinus = findViewById(R.id.btnMinus);

        MaterialCardView buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonScan = findViewById(R.id.buttonScan);

        btnBranded = findViewById(R.id.btnBranded);
        btnLoose = findViewById(R.id.btnLoose);

        layoutBarcode = findViewById(R.id.layoutBarcode);

        // 🔥 SPINNERS
        String[] units = {"kg", "g", "L", "ml", "pcs"};
        String[] categories = {"Dairy", "Fruits", "Vegetables", "Snacks", "Other"};

        spinnerUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units));

        spinnerCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));

        // 🔥 COUNTER
        textQuantity.setText(String.valueOf(quantity));

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

        // 🔥 DATE PICKER
        editExpiry.setFocusable(false);
        editExpiry.setOnClickListener(v -> showDatePicker());

        // 🔥 TOGGLE (BRANDED / LOOSE)

        btnBranded.setOnClickListener(v -> {
            btnBranded.setBackgroundResource(R.drawable.bg_toggle_selected_pearl);
            btnLoose.setBackgroundResource(android.R.color.transparent);

            layoutBarcode.setVisibility(View.VISIBLE);
        });

        btnLoose.setOnClickListener(v -> {
            btnLoose.setBackgroundResource(R.drawable.bg_toggle_selected_soft);
            btnBranded.setBackgroundResource(android.R.color.transparent);

            layoutBarcode.setVisibility(View.GONE);
            editBarcode.setText("");
        });

        // DEFAULT
        btnBranded.performClick();

        // 🔥 SCANNER BUTTON
        buttonScan.setOnClickListener(v -> {
            Intent intent = new Intent(AddGroceryActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, 100);
        });

        // 🔥 SAVE / CANCEL
        buttonSave.setOnClickListener(v -> saveItem());
        buttonCancel.setOnClickListener(v -> finish());

        // 🔥 EDIT MODE
        editItemId = getIntent().getIntExtra("EDIT_ITEM_ID", -1);

        if (editItemId != -1) {
            setTitle("Edit Item");
            loadItemData(editItemId);
        } else {
            setTitle("Add Item");
        }
    }

    // 🔥 RECEIVE SCAN RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String barcode = data.getStringExtra("barcode");

            if (barcode != null) {
                editBarcode.setText(barcode);
            }
        }
    }

    private void loadItemData(int id) {

        viewModel.getAllItems().observe(this, list -> {
            for (GroceryItem item : list) {
                if (item.getId() == id) {

                    editName.setText(item.getName());
                    editExpiry.setText(item.getExpiryDate());
                    editNotes.setText(item.getNotes());
                    editBarcode.setText(item.getBarcode());

                    quantity = item.getQuantity();
                    textQuantity.setText(String.valueOf(quantity));

                    // category
                    ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                    int pos = adapter.getPosition(item.getCategory());
                    spinnerCategory.setSelection(pos);

                    // toggle
                    if (item.getBarcode() != null && !item.getBarcode().isEmpty()) {
                        btnBranded.performClick();
                    } else {
                        btnLoose.performClick();
                    }

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
        String category = spinnerCategory.getSelectedItem().toString();
        String expiry = editExpiry.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();
        String barcode = editBarcode.getText().toString().trim();

        if (name.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this, "Name & Expiry required", Toast.LENGTH_SHORT).show();
            return;
        }

        GroceryItem item = new GroceryItem(
                name,
                quantity,
                category,
                expiry,
                notes,
                barcode
        );

        if (editItemId != -1) {
            item.setId(editItemId);
            viewModel.update(item);
            Toast.makeText(this, "Item Updated!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(item);
            Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}