package com.example.pantrypal.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pantrypal.R;
import com.example.pantrypal.ai.models.RecipeResponse;
import com.example.pantrypal.utils.RecipeStore;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class RecipeDetailsFragment extends Fragment {

    private RecipeResponse recipe;

    private TextView recipeTitle;
    private TextView recipeTime;
    private TextView recipeDifficulty;
    private TextView pantrySummary;
    private TextView wasteTip;

    private LinearLayout ingredientsContainer;
    private LinearLayout missingIngredientsContainer;
    private LinearLayout stepsContainer;

    public RecipeDetailsFragment() {
        super(R.layout.fragment_recipe_details);
    }

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            recipe = (RecipeResponse)
                    getArguments()
                            .getSerializable("recipe");
        }
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recipeTitle =
                view.findViewById(
                        R.id.textRecipeTitle
                );

        recipeTime =
                view.findViewById(
                        R.id.textRecipeTime
                );

        recipeDifficulty =
                view.findViewById(
                        R.id.textRecipeDifficulty
                );

        pantrySummary =
                view.findViewById(
                        R.id.textPantrySummary
                );

        wasteTip =
                view.findViewById(
                        R.id.textWasteTip
                );

        ingredientsContainer =
                view.findViewById(
                        R.id.ingredientsContainer
                );

        missingIngredientsContainer =
                view.findViewById(
                        R.id.missingIngredientsContainer
                );

        stepsContainer =
                view.findViewById(
                        R.id.stepsContainer
                );


        // ---------------------------------------------------------
        // BACK
        // ---------------------------------------------------------

        view.findViewById(
                R.id.buttonBack
        ).setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );


        // ---------------------------------------------------------
        // DELETE
        // ---------------------------------------------------------

        view.findViewById(
                R.id.buttonDelete
        ).setOnClickListener(v ->
                showDeleteConfirmation()
        );


        // ---------------------------------------------------------
        // FAVORITE
        // ---------------------------------------------------------

        view.findViewById(
                R.id.buttonFavorite
        ).setOnClickListener(v -> {

            TextView favorite =
                    (TextView) v;

            if ("♡".contentEquals(
                    favorite.getText())) {

                favorite.setText("♥");

            } else {

                favorite.setText("♡");
            }
        });


        // ---------------------------------------------------------
        // COOK BUTTON
        // ---------------------------------------------------------

        view.findViewById(
                R.id.buttonCookRecipe
        ).setOnClickListener(v -> {

            new MaterialAlertDialogBuilder(
                    requireContext()
            )
                    .setTitle("Ready to cook?")
                    .setMessage(
                            "Follow the steps above and enjoy your meal! 🌿"
                    )
                    .setPositiveButton(
                            "Let's Cook",
                            null
                    )
                    .setNegativeButton(
                            "Later",
                            null
                    )
                    .show();
        });


        if (recipe == null) {
            return;
        }


        // ---------------------------------------------------------
        // BASIC RECIPE INFORMATION
        // ---------------------------------------------------------

        recipeTitle.setText(
                safeText(
                        recipe.getRecipeName(),
                        "Pantry Recipe"
                )
        );

        recipeTime.setText(
                safeText(
                        recipe.getCookTime(),
                        "Time not specified"
                )
        );

        recipeDifficulty.setText(
                safeText(
                        recipe.getDifficulty(),
                        "Easy"
                )
        );


        // ---------------------------------------------------------
        // PANTRY SUMMARY
        // ---------------------------------------------------------

        int used =
                recipe.getIngredientsUsed() == null
                        ? 0
                        : recipe.getIngredientsUsed().size();

        pantrySummary.setText(
                used == 1
                        ? "This recipe uses 1 ingredient from your pantry and helps reduce food waste."
                        : "This recipe uses "
                        + used
                        + " ingredients from your pantry and helps reduce food waste."
        );


        // ---------------------------------------------------------
        // INGREDIENTS
        // ---------------------------------------------------------

        populateIngredients(
                ingredientsContainer,
                recipe.getIngredientsUsed(),
                true
        );


        // ---------------------------------------------------------
        // MISSING INGREDIENTS
        // ---------------------------------------------------------

        populateIngredients(
                missingIngredientsContainer,
                recipe.getMissingIngredients(),
                false
        );


        // ---------------------------------------------------------
        // STEPS
        // ---------------------------------------------------------

        populateSteps(
                recipe.getSteps()
        );


        // ---------------------------------------------------------
        // WASTE TIP
        // ---------------------------------------------------------

        wasteTip.setText(
                safeText(
                        recipe.getWasteTip(),
                        "Use ingredients that are closest to expiry first."
                )
        );
    }


    // =============================================================
    // INGREDIENT LIST
    // =============================================================

    private void populateIngredients(
            LinearLayout container,
            List<String> items,
            boolean available) {

        container.removeAllViews();

        if (items == null || items.isEmpty()) {

            TextView empty =
                    createBodyText(
                            available
                                    ? "No pantry ingredients listed."
                                    : "Nothing! You have everything."
                    );

            container.addView(empty);
            return;
        }


        for (String item : items) {

            if (item == null
                    || item.trim().isEmpty()) {
                continue;
            }

            LinearLayout row =
                    new LinearLayout(
                            requireContext()
                    );

            row.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            row.setGravity(
                    android.view.Gravity.CENTER_VERTICAL
            );

            row.setPadding(
                    0,
                    dp(8),
                    0,
                    dp(8)
            );


            TextView bullet =
                    new TextView(
                            requireContext()
                    );

            bullet.setText(
                    available ? "●" : "●"
            );

            bullet.setTextColor(
                    android.graphics.Color.parseColor(
                            available
                                    ? "#4D8B35"
                                    : "#E5B66E"
                    )
            );

            bullet.setTextSize(13);

            LinearLayout.LayoutParams
                    bulletParams =
                    new LinearLayout.LayoutParams(
                            dp(24),
                            dp(30)
                    );

            bullet.setGravity(
                    android.view.Gravity.CENTER
            );

            row.addView(
                    bullet,
                    bulletParams
            );


            TextView text =
                    createBodyText(
                            item.trim()
                    );

            row.addView(text);


            container.addView(row);
        }
    }


    // =============================================================
    // COOKING STEPS
    // =============================================================

    private void populateSteps(
            List<String> stepList) {

        stepsContainer.removeAllViews();

        if (stepList == null
                || stepList.isEmpty()) {

            stepsContainer.addView(
                    createBodyText(
                            "No cooking steps available."
                    )
            );

            return;
        }


        int number = 1;

        for (String step : stepList) {

            if (step == null
                    || step.trim().isEmpty()) {
                continue;
            }


            LinearLayout row =
                    new LinearLayout(
                            requireContext()
                    );

            row.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            row.setGravity(
                    Gravity.TOP
            );

            row.setPadding(
                    0,
                    dp(10),
                    0,
                    dp(10)
            );


            // -----------------------------------------------------
            // NUMBER CIRCLE
            // -----------------------------------------------------

            TextView numberView =
                    new TextView(
                            requireContext()
                    );

            numberView.setText(
                    String.valueOf(number)
            );

            numberView.setTextColor(
                    Color.parseColor(
                            "#173D28"
                    )
            );

            numberView.setTextSize(18);

            numberView.setTextSize(
                    Typeface.BOLD
            );

            numberView.setGravity(
                    Gravity.CENTER
            );

            numberView.setBackgroundColor(
                    Color.TRANSPARENT
            );


            LinearLayout.LayoutParams
                    numberParams =
                    new LinearLayout.LayoutParams(
                            dp(44),
                            dp(44)
                    );

            row.addView(
                    numberView,
                    numberParams
            );


            // -----------------------------------------------------
            // STEP TEXT
            // -----------------------------------------------------

            TextView stepText =
                    createBodyText(
                            step.trim()
                    );

            stepText.setTextSize(16);
            stepText.setTextColor(
                    Color.parseColor(
                            "#4B4F4B"
                    )
            );

            stepText.setLineSpacing(
                    dp(1),
                    1.05f
            );

            LinearLayout.LayoutParams
                    textParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            textParams.setMargins(
                    dp(12),
                    dp(1),
                    0,
                    0
            );

            row.addView(
                    stepText,
                    textParams
            );


            stepsContainer.addView(row);


            // -----------------------------------------------------
            // DIVIDER
            // -----------------------------------------------------

            if (number < stepList.size()) {

                View divider =
                        new View(
                                requireContext()
                        );

                divider.setBackgroundColor(
                        Color.parseColor(
                                "#EEEAE2"
                        )
                );

                stepsContainer.addView(
                        divider,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dp(1)
                        )
                );
            }

            number++;
        }
    }


    // =============================================================
    // DELETE CONFIRMATION
    // =============================================================

    private void showDeleteConfirmation() {

        String recipeName =
                safeText(
                        recipe.getRecipeName(),
                        "this recipe"
                );


        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Delete recipe?")
                .setMessage(
                        "Remove \""
                                + recipeName
                                + "\" from your saved recipes?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            RecipeStore.deleteRecipe(
                                    requireContext(),
                                    recipe
                            );

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .popBackStack();
                        }
                )
                .show();
    }


    // =============================================================
    // HELPERS
    // =============================================================

    private TextView createBodyText(
            String text) {

        TextView view =
                new TextView(
                        requireContext()
                );

        view.setText(text);
        view.setTextColor(
                android.graphics.Color.parseColor(
                        "#333333"
                )
        );

        view.setTextSize(17);
        view.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        return view;
    }


    private String safeText(
            String value,
            String fallback) {

        return value == null
                || value.trim().isEmpty()
                ? fallback
                : value.trim();
    }


    private int dp(int value) {

        return Math.round(
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
    @Override
    public void onResume() {
        super.onResume();

        View bottomNav = requireActivity().findViewById(R.id.bottomNavCard);

        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        View bottomNav = requireActivity().findViewById(R.id.bottomNavCard);

        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }
}