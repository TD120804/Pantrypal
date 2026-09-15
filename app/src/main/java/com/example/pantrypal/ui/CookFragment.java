package com.example.pantrypal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pantrypal.R;
import com.example.pantrypal.ai.models.RecipeResponse;
import com.example.pantrypal.utils.RecipeStore;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CookFragment extends Fragment {

    private LinearLayout recipeContainer;
    private TextView textRecipeCount;
    private MaterialCardView emptyRecipeCard;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_cook,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recipeContainer = view.findViewById(R.id.recipeContainer);
        textRecipeCount = view.findViewById(R.id.textRecipeCount);
        emptyRecipeCard = view.findViewById(R.id.emptyRecipeCard);

        loadRecipes();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recipeContainer != null) {
            loadRecipes();
        }
    }

    private void loadRecipes() {

        List<RecipeResponse> recipes =
                RecipeStore.getRecipes(requireContext());

        recipeContainer.removeAllViews();

        int count = recipes.size();

        if (count == 0) {

            textRecipeCount.setText("0 recipes");
            emptyRecipeCard.setVisibility(View.VISIBLE);
            return;
        }

        emptyRecipeCard.setVisibility(View.GONE);

        textRecipeCount.setText(
                count == 1
                        ? "1 recipe"
                        : count + " recipes"
        );

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        for (RecipeResponse recipe : recipes) {

            View card = inflater.inflate(
                    R.layout.recipe_card,
                    recipeContainer,
                    false
            );

            TextView recipeName =
                    card.findViewById(R.id.textRecipeName);

            TextView cookTime =
                    card.findViewById(R.id.textCookTime);

            TextView difficulty =
                    card.findViewById(R.id.textDifficulty);

            TextView usedIngredients =
                    card.findViewById(R.id.textUsedIngredients);

            TextView missingIngredients =
                    card.findViewById(R.id.textMissingIngredients);

            MaterialCardView viewRecipe =
                    card.findViewById(R.id.buttonViewRecipe);

            ImageView deleteRecipe =
                    card.findViewById(R.id.buttonDeleteRecipe);

            recipeName.setText(
                    safeText(
                            recipe.getRecipeName(),
                            "Pantry Recipe"
                    )
            );

            cookTime.setText(
                    safeText(
                            recipe.getCookTime(),
                            "Time not specified"
                    )
            );

            difficulty.setText(
                    safeText(
                            recipe.getDifficulty(),
                            "Easy"
                    )
            );

            int used = recipe.getIngredientsUsed() == null
                    ? 0
                    : recipe.getIngredientsUsed().size();

            int missing = recipe.getMissingIngredients() == null
                    ? 0
                    : recipe.getMissingIngredients().size();

            usedIngredients.setText(
                    used == 1
                            ? "Uses 1 pantry ingredient"
                            : "Uses " + used + " pantry ingredients"
            );

            if (missing == 0) {

                missingIngredients.setText(
                        "You have everything you need"
                );

            } else {

                missingIngredients.setText(
                        missing == 1
                                ? "Needs 1 additional ingredient"
                                : "Needs " + missing + " additional ingredients"
                );
            }

            View.OnClickListener listener =
                    v -> showRecipeDetails(recipe);

            card.setOnClickListener(listener);
            viewRecipe.setOnClickListener(listener);

            deleteRecipe.setOnClickListener(v ->
                    showDeleteConfirmation(recipe)
            );

            recipeContainer.addView(card);
        }
    }

    private void showDeleteConfirmation(
            RecipeResponse recipe) {

        String recipeName =
                safeText(
                        recipe.getRecipeName(),
                        "this recipe"
                );

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete recipe?")
                .setMessage(
                        "Remove \"" + recipeName
                                + "\" from your saved recipes?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            RecipeStore.deleteRecipe(
                                    requireContext(),
                                    recipe
                            );

                            loadRecipes();
                        }
                )
                .show();
    }

    private void showRecipeDetails(RecipeResponse recipe) {

        Bundle bundle = new Bundle();

        bundle.putSerializable(
                "recipe",
                recipe
        );

        RecipeDetailsFragment details =
                new RecipeDetailsFragment();

        details.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        details
                )
                .addToBackStack(null)
                .commit();
    }

    private String formatList(
            List<String> items,
            String emptyText) {

        if (items == null || items.isEmpty()) {
            return emptyText;
        }

        StringBuilder builder = new StringBuilder();

        for (String item : items) {

            if (item == null || item.trim().isEmpty()) {
                continue;
            }

            builder.append("• ")
                    .append(item.trim())
                    .append("\n");
        }

        String result = builder.toString().trim();

        return result.isEmpty()
                ? emptyText
                : result;
    }

    private String formatNumberedList(
            List<String> items,
            String emptyText) {

        if (items == null || items.isEmpty()) {
            return emptyText;
        }

        StringBuilder builder = new StringBuilder();

        int number = 1;

        for (String item : items) {

            if (item == null || item.trim().isEmpty()) {
                continue;
            }

            builder.append(number)
                    .append(". ")
                    .append(item.trim())
                    .append("\n\n");

            number++;
        }

        String result = builder.toString().trim();

        return result.isEmpty()
                ? emptyText
                : result;
    }

    private String safeText(
            String value,
            String fallback) {

        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}