package com.example.pantrypal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.ai.RecipeRepository;
import com.example.pantrypal.ai.models.RecipeResponse;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.scanner.BarcodeScannerActivity;
import com.example.pantrypal.utils.RecipeStore;
import com.example.pantrypal.utils.RiskCalculator;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllItemsFragment extends Fragment {

    private GroceryViewModel viewModel;

    private TextView greeting;
    private TextView greetingSubtitle;
    private TextView expiringText;
    private TextView recipesText;
    private TextView buyText;
    private TextView atRiskText;
    private TextView healthScore;
    private TextView healthStatus;
    private TextView healthMessage;
    private TextView totalItems;
    private com.mikhaellopez.circularprogressbar.CircularProgressBar healthCircle;
    private TextView recipeName;
    private TextView cookTime;
    private TextView difficulty;
    private TextView servings;
    private TextView ingredientAvailability;
    private TextView textWeeklyMission;
    private TextView textWeeklyProgress;
    private TextView textWeeklyReward;

    private MaterialCardView cardWeeklyComeback;

    private static final int WEEKLY_TARGET = 2;
    private static final String WEEKLY_PREFS = "weekly_comeback";
    private static final String KEY_WEEK = "week";
    private static final String KEY_PROGRESS = "progress";

    private MaterialCardView cardAdd;
    private MaterialCardView cardScan;
    private MaterialCardView cardGrocery;
    private View cardExpiring;
    private MaterialCardView cardRecipes;
    private MaterialCardView cardRecipe;
    private MaterialButton btnCookNow;
    private MaterialButton btnHealthDetails;

    private RecipeResponse currentRecipe;
    private final RecipeRepository recipeRepository = new RecipeRepository();
    private String lastPantrySignature = "";
    private boolean recipeLoading;

    public AllItemsFragment() {
        super(R.layout.fragment_all_items_ver2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        greeting = view.findViewById(R.id.txtGreeting);
        greetingSubtitle = view.findViewById(R.id.txtGreetingSubtitle);
        expiringText = view.findViewById(R.id.textExpiring);
        recipesText = view.findViewById(R.id.textRecipes);
        buyText = view.findViewById(R.id.textBuy);
        atRiskText = view.findViewById(R.id.textSaved);
        totalItems = view.findViewById(R.id.text_total_items);
        healthScore = view.findViewById(R.id.text_health_score);
        healthCircle = view.findViewById(R.id.healthCircle);
        healthStatus = view.findViewById(R.id.text_health_status);
        healthMessage = view.findViewById(R.id.textHealthMessage);
        recipeName = view.findViewById(R.id.textRecipeName);
        cookTime = view.findViewById(R.id.textCookTime);
        difficulty = view.findViewById(R.id.textDifficulty);
        servings = view.findViewById(R.id.textServings);
        ingredientAvailability = view.findViewById(R.id.textIngredientAvailability);
        textWeeklyMission = view.findViewById(R.id.textWeeklyMission);
        textWeeklyProgress = view.findViewById(R.id.textWeeklyProgress);
        textWeeklyReward = view.findViewById(R.id.textWeeklyReward);

        cardWeeklyComeback = view.findViewById(R.id.cardWeeklyComeback);

        cardAdd = view.findViewById(R.id.cardAdd);
        cardScan = view.findViewById(R.id.cardScan);
        cardGrocery = view.findViewById(R.id.cardGrocery);
        cardRecipes = view.findViewById(R.id.cardRecipes);
        cardExpiring = view.findViewById(R.id.cardExpiring);
        cardRecipe = view.findViewById(R.id.cardAI);
        btnCookNow = view.findViewById(R.id.btnCookNow);
        btnHealthDetails = view.findViewById(R.id.btnHealthDetails);

        updateGreeting();

        cardAdd.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddGroceryActivity.class)));

        cardScan.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BarcodeScannerActivity.class)));

        cardExpiring.setOnClickListener(v -> openFragment(new ExpiringSoonFragment()));

        btnHealthDetails.setOnClickListener(v ->
                openFragment(new PantryAnalysisFragment()));

        cardRecipes.setOnClickListener(v -> openCurrentRecipe());
        cardRecipe.setOnClickListener(v -> openCurrentRecipe());
        btnCookNow.setOnClickListener(v -> openCurrentRecipe());

        cardGrocery.setOnClickListener(v -> showShoppingList());
        cardWeeklyComeback.setOnClickListener(v -> showWeeklyComebackDialog());

        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);
        viewModel.getAllItems().observe(getViewLifecycleOwner(), this::updateDashboard);
    }

    private void updateGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String partOfDay;

        if (hour < 12) {
            partOfDay = "Good Morning";
        } else if (hour < 17) {
            partOfDay = "Good Afternoon";
        } else {
            partOfDay = "Good Evening";
        }

        greeting.setText(partOfDay + ", Trisha 🌿");
    }

    private void updateDashboard(List<GroceryItem> items) {
        if (items == null) return;

        int total = items.size();
        totalItems.setText(String.valueOf(total));
        int expiring = 0;
        int lowStock = 0;
        int atRisk = 0;

        for (GroceryItem item : items) {
            int days = RiskCalculator.getDaysLeft(item.getExpiryDate());

            if (days <= 7) {
                expiring++;
            }

            if (item.getQuantity() <= 1) {
                lowStock++;
            }

            if (days <= 3) {
                atRisk++;
            }
        }

        int score = calculateHealthScore(items);

        expiringText.setText(String.valueOf(expiring));
        recipesText.setText(total == 0 ? "0" : "1");
        buyText.setText(String.valueOf(lowStock));
        atRiskText.setText(String.valueOf(atRisk));

        healthScore.setText(score + "%");
        healthCircle.setProgress(score);
        healthStatus.setText(getHealthStatus(score));
        healthMessage.setText(getHealthMessage(score, expiring));
        updateWeeklyComeback();
        if (total == 0) {
            greetingSubtitle.setText("Your kitchen is ready for its first ingredients.");
            recipeName.setText("Add ingredients to get an AI recipe");
            cookTime.setText("🕒 —");
            difficulty.setText("😊 —");
            servings.setText("♟ —");
            ingredientAvailability.setText("Ingredients\nAvailable\n—");
            currentRecipe = null;
            lastPantrySignature = "";
            return;
        }

        greetingSubtitle.setText(getGreetingSubtitle(score, expiring));

        String signature = buildPantrySignature(items);
        if (!signature.equals(lastPantrySignature) && !recipeLoading) {
            lastPantrySignature = signature;
            loadRecipe(items);
        }
    }

    private int calculateHealthScore(List<GroceryItem> items) {
        if (items.isEmpty()) return 0;

        int score = 100;
        for (GroceryItem item : items) {
            int days = RiskCalculator.getDaysLeft(item.getExpiryDate());
            if (days < 0) {
                score -= 15;
            } else if (days <= 3) {
                score -= 5;
            }
        }
        return Math.max(0, score);
    }

    private String getHealthStatus(int score) {
        if (score >= 80) return "Excellent";
        if (score >= 60) return "Good";
        if (score >= 40) return "Needs Attention";
        return "Needs Action";
    }

    private String getHealthMessage(int score, int expiring) {
        if (score >= 80 && expiring == 0) {
            return "Great job! Your pantry is well managed this week.";
        }
        if (expiring > 0) {
            return expiring + " item(s) need your attention soon.";
        }
        return "A few small actions can keep your pantry healthy.";
    }

    private String getGreetingSubtitle(int score, int expiring) {
        if (score >= 80 && expiring == 0) {
            return "Your kitchen is looking healthy today.";
        }
        if (expiring > 0) {
            return "You have " + expiring + " item(s) worth checking today.";
        }
        return "Let's keep your pantry healthy and waste-free.";
    }

    private void loadRecipe(List<GroceryItem> items) {
        recipeLoading = true;
        recipeName.setText("Finding your best pantry recipe…");
        cookTime.setText("🕒 Thinking");
        difficulty.setText("😊 AI");
        servings.setText("♟ 2");
        ingredientAvailability.setText("Ingredients\nAvailable\n—");

        recipeRepository.generateRecipe(new ArrayList<>(items), new RecipeRepository.Callback() {
            @Override
            public void onSuccess(RecipeResponse recipe) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    recipeLoading = false;
                    currentRecipe = recipe;

                    RecipeStore.saveRecipe(requireContext(), recipe);

                    recipeName.setText(safeText(recipe.getRecipeName(), "Pantry recipe"));
                    cookTime.setText("🕒 " + safeText(recipe.getCookTime(), "—"));
                    difficulty.setText("😊 " + safeText(recipe.getDifficulty(), "Easy"));
                    servings.setText("♟ 2");
                    ingredientAvailability.setText("Ingredients\nAvailable\n—");
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    recipeLoading = false;
                    currentRecipe = null;
                    recipeName.setText("AI recipe unavailable right now");
                    cookTime.setText("🕒 Try again later");
                    difficulty.setText("😊 —");
                    servings.setText("♟ —");
                    ingredientAvailability.setText("Ingredients\nAvailable\n—");
                });
            }
        });
    }

    private String getIngredientAvailability(RecipeResponse recipe) {
        int used = recipe.getIngredientsUsed() == null ? 0 : recipe.getIngredientsUsed().size();
        int missing = recipe.getMissingIngredients() == null ? 0 : recipe.getMissingIngredients().size();
        int total = used + missing;
        if (total == 0) return "Ingredients\nAvailable\n—";
        int percent = Math.round((used * 100f) / total);
        return "Ingredients\nAvailable\n" + percent + "%";
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String buildPantrySignature(List<GroceryItem> items) {
        StringBuilder signature = new StringBuilder();
        for (GroceryItem item : items) {
            signature.append(item.getId()).append('|')
                    .append(item.getName()).append('|')
                    .append(item.getQuantity()).append('|')
                    .append(item.getExpiryDate()).append(';');
        }
        return signature.toString();
    }

    private void openCurrentRecipe() {
        if (currentRecipe == null) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("No recipe yet")
                    .setMessage("Add a few pantry items and PantryPal will generate a recipe for you.")
                    .setPositiveButton("Add Item", (dialog, which) ->
                            startActivity(new Intent(requireContext(), AddGroceryActivity.class)))
                    .setNegativeButton("Close", null)
                    .show();
            return;
        }

        showRecipePreview(currentRecipe);
    }

    private void showShoppingList() {
        List<GroceryItem> items = viewModel.getAllItems().getValue();
        if (items == null) items = new ArrayList<>();

        StringBuilder shopping = new StringBuilder();
        int count = 0;

        for (GroceryItem item : items) {
            if (item.getQuantity() <= 1) {
                shopping.append("• ")
                        .append(item.getName())
                        .append(" (low stock)\n");
                count++;
            }
        }

        if (count == 0) {
            shopping.append("Your pantry is well stocked right now. 🌿");
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Grocery List")
                .setMessage(shopping.toString().trim())
                .setPositiveButton("View Pantry", (dialog, which) ->
                        openFragment(new ItemsFragment()))
                .setNegativeButton("Close", null)
                .show();
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showRecipePreview(RecipeResponse recipe) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.recipe_preview_dialog, null);

        TextView dialogRecipeName = dialogView.findViewById(R.id.dialogRecipeName);
        TextView dialogRecipeInfo = dialogView.findViewById(R.id.dialogRecipeInfo);
        TextView dialogHave = dialogView.findViewById(R.id.dialogHave);
        TextView dialogMissing = dialogView.findViewById(R.id.dialogMissing);

        dialogRecipeName.setText(safeText(recipe.getRecipeName(), "Pantry Recipe"));
        dialogRecipeInfo.setText(
                "🕒 " + safeText(recipe.getCookTime(), "—")
                        + "   😊 " + safeText(recipe.getDifficulty(), "Easy")
        );

        if (recipe.getIngredientsUsed() != null && !recipe.getIngredientsUsed().isEmpty()) {
            StringBuilder haveText = new StringBuilder();
            for (String ingredient : recipe.getIngredientsUsed()) {
                haveText.append("✓ ").append(ingredient).append("\n");
            }
            dialogHave.setText(haveText.toString().trim());
        } else {
            dialogHave.setText("No pantry ingredients listed.");
        }

        if (recipe.getMissingIngredients() != null && !recipe.getMissingIngredients().isEmpty()) {
            StringBuilder missingText = new StringBuilder();
            for (String ingredient : recipe.getMissingIngredients()) {
                missingText.append("• ").append(ingredient).append("\n");
            }
            dialogMissing.setText(missingText.toString().trim());
        } else {
            dialogMissing.setText("Nothing! You have everything 🎉");
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Maybe Later", null)
                .setPositiveButton("View Recipe →", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("recipe", recipe);

                    RecipeDetailsFragment details = new RecipeDetailsFragment();
                    details.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, details)
                            .addToBackStack(null)
                            .commit();
                })
                .show();
    }

    private void updateWeeklyComeback() {
        android.content.SharedPreferences prefs =
                requireContext().getSharedPreferences(WEEKLY_PREFS, android.content.Context.MODE_PRIVATE);

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        int year = calendar.get(java.util.Calendar.YEAR);
        int week = calendar.get(java.util.Calendar.WEEK_OF_YEAR);

        String currentWeek = year + "-" + week;

        String savedWeek = prefs.getString(KEY_WEEK, "");

        if (!currentWeek.equals(savedWeek)) {
            prefs.edit()
                    .putString(KEY_WEEK, currentWeek)
                    .putInt(KEY_PROGRESS, 0)
                    .apply();
        }

        int progress = prefs.getInt(KEY_PROGRESS, 0);
        progress = Math.min(progress, WEEKLY_TARGET);

        textWeeklyMission.setText("Mission: Use 2 items before they expire");
        textWeeklyProgress.setText("Progress: " + progress + " / " + WEEKLY_TARGET);
        textWeeklyReward.setText("Reward: Kitchen Champion 🏆");

        if (progress >= WEEKLY_TARGET) {
            textWeeklyProgress.setText("Progress: 2 / 2 ✓");
            textWeeklyReward.setText("Reward: Kitchen Champion 🏆 🎉");
        }
    }
    private List<GroceryItem> getWeeklyEligibleItems() {
        List<GroceryItem> allItems = viewModel.getAllItems().getValue();
        List<GroceryItem> eligibleItems = new ArrayList<>();

        if (allItems == null) {
            return eligibleItems;
        }

        for (GroceryItem item : allItems) {
            int daysLeft = RiskCalculator.getDaysLeft(item.getExpiryDate());

            // Item must still be usable and expire within 7 days.
            if (daysLeft >= 0 && daysLeft <= 7 && item.getQuantity() > 0) {
                eligibleItems.add(item);
            }
        }

        return eligibleItems;
    }
    private void showWeeklyComebackDialog() {

        android.content.SharedPreferences prefs =
                requireContext().getSharedPreferences(
                        WEEKLY_PREFS,
                        android.content.Context.MODE_PRIVATE
                );

        int progress = prefs.getInt(KEY_PROGRESS, 0);

        if (progress >= WEEKLY_TARGET) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Weekly Comeback 🎉")
                    .setMessage(
                            "Mission complete!\n\n" +
                                    "You used 2 items before they expired.\n\n" +
                                    "Reward: Kitchen Champion 🏆"
                    )
                    .setPositiveButton("Nice!", null)
                    .show();

            return;
        }


        List<GroceryItem> eligibleItems = getWeeklyEligibleItems();

        if (eligibleItems.isEmpty()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Weekly Comeback 🌱")
                    .setMessage(
                            "No items are currently expiring within 7 days.\n\n" +
                                    "Add or keep an eye on expiring pantry items to complete this week's mission."
                    )
                    .setPositiveButton("Got it", null)
                    .show();

            return;
        }

        String[] itemNames = new String[eligibleItems.size()];

        for (int i = 0; i < eligibleItems.size(); i++) {
            GroceryItem item = eligibleItems.get(i);

            itemNames[i] =
                    item.getName()
                            + "  •  Qty " + item.getQuantity()
                            + "  •  " + RiskCalculator.getDaysLeft(item.getExpiryDate())
                            + " day(s) left";
        }

        final int[] selectedIndex = {0};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("What did you use? 🌱")
                .setSingleChoiceItems(
                        itemNames,
                        0,
                        (dialog, which) -> selectedIndex[0] = which
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Use 1", (dialog, which) -> {

                    GroceryItem selectedItem =
                            eligibleItems.get(selectedIndex[0]);

                    useOneItemForWeeklyMission(selectedItem);
                })
                .show();
    }
    private void incrementWeeklyProgress() {

        android.content.SharedPreferences prefs =
                requireContext().getSharedPreferences(
                        WEEKLY_PREFS,
                        android.content.Context.MODE_PRIVATE
                );

        int progress = prefs.getInt(KEY_PROGRESS, 0);

        if (progress < WEEKLY_TARGET) {
            progress++;
        }

        prefs.edit()
                .putInt(KEY_PROGRESS, progress)
                .apply();

        updateWeeklyComeback();

        if (progress >= WEEKLY_TARGET) {

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Mission Complete! 🎉")
                    .setMessage(
                            "You used 2 items before they expired.\n\n" +
                                    "You've earned the Kitchen Champion reward! 🏆"
                    )
                    .setPositiveButton("Awesome!", null)
                    .show();
        }
    }
    private void useOneItemForWeeklyMission(GroceryItem item) {

        int currentQuantity = item.getQuantity();

        if (currentQuantity <= 0) {
            return;
        }

        if (currentQuantity == 1) {
            // If only 1 is left, remove the item
            viewModel.delete(item);
        } else {
            // Otherwise reduce quantity by 1
            item.setQuantity(currentQuantity - 1);
            viewModel.update(item);
        }

        // Increase weekly mission progress
        incrementWeeklyProgress();
    }
}
