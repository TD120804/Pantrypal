package com.example.pantrypal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pantrypal.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        View settings = view.findViewById(R.id.buttonProfileSettings);
        View editProfile = view.findViewById(R.id.buttonEditProfile);

        View myRecipes = view.findViewById(R.id.cardMyRecipes);
        View preferences = view.findViewById(R.id.cardPreferences);
        View sustainability = view.findViewById(R.id.cardSustainability);
        View notifications = view.findViewById(R.id.cardNotifications);
        View shopGroceries = view.findViewById(R.id.cardShopGroceries);
        View logout = view.findViewById(R.id.cardLogout);

        settings.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Settings coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        editProfile.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Profile editing coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        myRecipes.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Your saved recipes are available in Cook",
                        Toast.LENGTH_SHORT
                ).show()
        );

        preferences.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Preferences coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        sustainability.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Sustainability insights coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        notifications.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Notifications are already enabled",
                        Toast.LENGTH_SHORT
                ).show()
        );

        shopGroceries.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Grocery shopping links coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        logout.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Log out?")
                        .setMessage("Are you sure you want to log out?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Log Out", (dialog, which) -> {
                            Toast.makeText(
                                    requireContext(),
                                    "Logged out",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .show()
        );
    }
}