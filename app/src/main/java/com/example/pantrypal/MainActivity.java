package com.example.pantrypal;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pantrypal.ui.AllItemsFragment;
import com.example.pantrypal.ui.ItemsFragment;
import com.example.pantrypal.ui.PantryAnalysisFragment;
import com.example.pantrypal.utils.NotificationHelper;
import com.example.pantrypal.ui.CookFragment;
import com.example.pantrypal.ui.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private final int ACTIVE_COLOR = Color.rgb(98, 154, 50);
    private final int INACTIVE_COLOR = Color.rgb(85, 81, 76);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        NotificationHelper.createNotificationChannel(this);

        // Default screen
        loadFragment(new AllItemsFragment());
        setSelectedNav("home");

        // Navigation buttons
        View navHome = findViewById(R.id.navHome);
        View navPantry = findViewById(R.id.navPantry);
        View navCook = findViewById(R.id.navCook);
        View navAnalytics = findViewById(R.id.navAnalytics);
        View navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            loadFragment(new AllItemsFragment());
            setSelectedNav("home");
        });

        navPantry.setOnClickListener(v -> {
            loadFragment(new ItemsFragment());
            setSelectedNav("pantry");
        });

        navCook.setOnClickListener(v -> {
            loadFragment(new CookFragment());
            setSelectedNav("cook");
        });

        navAnalytics.setOnClickListener(v -> {
            loadFragment(new PantryAnalysisFragment());
            setSelectedNav("analytics");
        });

        navProfile.setOnClickListener(v -> {
            // Temporary until the real ProfileFragment is created
            loadFragment(new ProfileFragment());
            setSelectedNav("profile");
        });
    }


    private void setSelectedNav(String selected) {

        TextView labelHome = findViewById(R.id.labelHome);
        TextView labelPantry = findViewById(R.id.labelPantry);
        TextView labelCook = findViewById(R.id.labelCook);
        TextView labelAnalytics = findViewById(R.id.labelAnalytics);
        TextView labelProfile = findViewById(R.id.labelProfile);

        ImageView iconHome = findViewById(R.id.iconHome);
        ImageView iconPantry = findViewById(R.id.iconPantry);
        ImageView iconCook = findViewById(R.id.iconCook);
        ImageView iconAnalytics = findViewById(R.id.iconAnalytics);
        ImageView iconProfile = findViewById(R.id.iconProfile);


        // Reset everything
        labelHome.setTextColor(INACTIVE_COLOR);
        labelPantry.setTextColor(INACTIVE_COLOR);
        labelCook.setTextColor(INACTIVE_COLOR);
        labelAnalytics.setTextColor(INACTIVE_COLOR);
        labelProfile.setTextColor(INACTIVE_COLOR);

        iconHome.setColorFilter(INACTIVE_COLOR);
        iconPantry.setColorFilter(INACTIVE_COLOR);
        iconCook.setColorFilter(INACTIVE_COLOR);
        iconAnalytics.setColorFilter(INACTIVE_COLOR);

        // Highlight selected item
        switch (selected) {

            case "home":
                labelHome.setTextColor(ACTIVE_COLOR);
                iconHome.setColorFilter(ACTIVE_COLOR);
                break;

            case "pantry":
                labelPantry.setTextColor(ACTIVE_COLOR);
                iconPantry.setColorFilter(ACTIVE_COLOR);
                break;

            case "cook":
                labelCook.setTextColor(ACTIVE_COLOR);
                iconCook.setColorFilter(ACTIVE_COLOR);
                break;

            case "analytics":
                labelAnalytics.setTextColor(ACTIVE_COLOR);
                iconAnalytics.setColorFilter(ACTIVE_COLOR);
                break;

            case "profile":
                labelProfile.setTextColor(ACTIVE_COLOR);
                break;
        }
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment == null) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        return true;
    }
}