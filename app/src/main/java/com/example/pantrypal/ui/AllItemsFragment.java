package com.example.pantrypal.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.utils.RiskCalculator;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import android.content.Intent;

import com.google.android.material.card.MaterialCardView;
import com.example.pantrypal.scanner.BarcodeScannerActivity;
import com.example.pantrypal.ai.RecipeRepository;
import com.example.pantrypal.ai.models.RecipeResponse;

import java.util.List;

public class AllItemsFragment extends Fragment {

    private GroceryViewModel viewModel;

    private TextView totalItemsText;
    private TextView healthScore;
    private TextView healthStatus;
    private TextView recipeName;
    private TextView cookTime;
    private TextView difficulty;
    private TextView servings;
    private final RecipeRepository recipeRepository = new RecipeRepository();
    private MaterialCardView cardAdd;
    private MaterialCardView cardScan;
    private MaterialCardView cardGrocery;
    private MaterialCardView cardRecipe;

    public AllItemsFragment() {
        super(R.layout.fragment_all_items_ver2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalItemsText = view.findViewById(R.id.text_total_items);
        healthScore = view.findViewById(R.id.text_health_score);
        healthStatus = view.findViewById(R.id.text_health_status);

        recipeName = view.findViewById(R.id.textRecipeName);
        cookTime = view.findViewById(R.id.textCookTime);
        difficulty = view.findViewById(R.id.textDifficulty);
        servings = view.findViewById(R.id.textServings);

        cardAdd = view.findViewById(R.id.cardAdd);
        cardScan = view.findViewById(R.id.cardScan);
        cardGrocery = view.findViewById(R.id.cardGrocery);
        cardRecipe = view.findViewById(R.id.cardRecipes);

        cardAdd.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddGroceryActivity.class)));

        cardScan.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BarcodeScannerActivity.class)));

        cardRecipe.setOnClickListener(v -> {
            // TODO: Open AI Recipes
        });

        cardGrocery.setOnClickListener(v -> {
            // TODO: Open Grocery List
        });


        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        viewModel.getAllItems().observe(getViewLifecycleOwner(), this::updateDashboard);
    }

    private void updateDashboard(List<GroceryItem> items) {

        if (items == null) return;

        totalItemsText.setText(String.valueOf(items.size()));

        int score = 100;

        for (GroceryItem item : items) {

            int days = RiskCalculator.getDaysLeft(item.getExpiryDate());

            if (days < 0)
                score -= 15;
            else if (days <= 3)
                score -= 5;
        }

        score = Math.max(0, score);

        healthScore.setText(score + "/100");

        if (score >= 80)
            healthStatus.setText("Excellent Pantry 🌿");
        else if (score >= 60)
            healthStatus.setText("Good Pantry 😊");
        else if (score >= 40)
            healthStatus.setText("Needs Attention ⚠");
        else
            healthStatus.setText("Poor Pantry 🚨");

        Log.d("AI_DEBUG", "Calling RecipeRepository");
        recipeRepository.generateRecipe(items, new RecipeRepository.Callback() {

            @Override
            public void onSuccess(RecipeResponse recipe) {

                requireActivity().runOnUiThread(() -> {

                    recipeName.setText(recipe.getRecipeName());

                    cookTime.setText("🕒 " + recipe.getCookTime());
                    difficulty.setText("😊 " + recipe.getDifficulty());
                    servings.setText("👥 2");

                });

            }

            @Override
            public void onError(String error) {

                requireActivity().runOnUiThread(() -> {

                    recipeName.setText("Unable to generate recipe");

                    cookTime.setText("🕒 --");
                    difficulty.setText("⚠ Error");
                    servings.setText("👥 --");

                });

            }

        });
    }
}