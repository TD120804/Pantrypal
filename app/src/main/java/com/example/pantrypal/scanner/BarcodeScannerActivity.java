package com.example.pantrypal.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.ui.AddGroceryActivity;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.concurrent.Executors;

public class BarcodeScannerActivity extends AppCompatActivity {

    private GroceryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a grocery barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);

        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleScannedBarcode(result.getContents());
                }
            });

    private void handleScannedBarcode(String scannedBarcode) {

        runOnUiThread(() -> {

            Intent intent = new Intent(
                    BarcodeScannerActivity.this,
                    AddGroceryActivity.class
            );

            intent.putExtra("barcode", scannedBarcode);

            startActivity(intent);

            finish();

        });

    }
}
