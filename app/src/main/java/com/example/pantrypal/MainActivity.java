package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pantrypal.ui.AllItemsFragment;
import com.example.pantrypal.ui.ItemsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        // Default screen
        loadFragment(new AllItemsFragment());

        nav.setOnItemSelectedListener(item -> {

            Fragment selected = null;

            if (item.getItemId() == R.id.nav_home) {
                selected = new AllItemsFragment();
            }
            else if (item.getItemId() == R.id.nav_items) {
                selected = new ItemsFragment();
            }
            else if (item.getItemId() == R.id.nav_scan) {
                startActivity(new Intent(this, com.example.pantrypal.scanner.BarcodeScannerActivity.class));
                return true;
            }

            return loadFragment(selected);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) return false;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        return true;
    }
}