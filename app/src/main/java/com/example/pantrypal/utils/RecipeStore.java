package com.example.pantrypal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pantrypal.ai.models.RecipeResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeStore {

    private static final String PREFS_NAME = "pantrypal_recipes";
    private static final String KEY_RECIPES = "saved_recipes";

    private RecipeStore() {
        // Utility class
    }

    // ============================================================
    // SAVE RECIPE
    // ============================================================

    public static void saveRecipe(Context context, RecipeResponse recipe) {

        if (recipe == null) return;

        String recipeName = safe(recipe.getRecipeName());

        if (recipeName.isEmpty()) return;

        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        JSONArray existingRecipes;

        try {
            existingRecipes =
                    new JSONArray(prefs.getString(KEY_RECIPES, "[]"));
        } catch (Exception e) {
            existingRecipes = new JSONArray();
        }

        String newName = normalize(recipeName);
        String newIngredients =
                normalizeIngredients(recipe.getIngredientsUsed());

        // --------------------------------------------------------
        // Prevent duplicate recipes
        // --------------------------------------------------------

        for (int i = 0; i < existingRecipes.length(); i++) {

            try {

                JSONObject existing =
                        existingRecipes.getJSONObject(i);

                String existingName =
                        normalize(existing.optString("recipeName", ""));

                List<String> existingIngredientList =
                        split(existing.optString("ingredientsUsed", ""));

                String existingIngredients =
                        normalizeIngredients(existingIngredientList);

                if (existingName.equals(newName)
                        && existingIngredients.equals(newIngredients)) {

                    return;
                }

            } catch (Exception ignored) {
            }
        }

        // --------------------------------------------------------
        // Create recipe JSON
        // --------------------------------------------------------

        try {

            JSONObject object = new JSONObject();

            object.put(
                    "recipeName",
                    safe(recipe.getRecipeName())
            );

            object.put(
                    "cookTime",
                    safe(recipe.getCookTime())
            );

            object.put(
                    "difficulty",
                    safe(recipe.getDifficulty())
            );

            object.put(
                    "wasteTip",
                    safe(recipe.getWasteTip())
            );

            object.put(
                    "nutrition",
                    safe(recipe.getNutrition())
            );

            object.put(
                    "ingredientsUsed",
                    join(recipe.getIngredientsUsed())
            );

            object.put(
                    "missingIngredients",
                    join(recipe.getMissingIngredients())
            );

            object.put(
                    "steps",
                    join(recipe.getSteps())
            );

            // Newest recipe first
            JSONArray updatedRecipes =
                    new JSONArray();

            updatedRecipes.put(object);

            for (int i = 0;
                 i < existingRecipes.length();
                 i++) {

                updatedRecipes.put(
                        existingRecipes.getJSONObject(i)
                );
            }

            prefs.edit()
                    .putString(
                            KEY_RECIPES,
                            updatedRecipes.toString()
                    )
                    .apply();

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // GET RECIPES
    // ============================================================

    public static List<RecipeResponse> getRecipes(Context context) {

        List<RecipeResponse> recipes =
                new ArrayList<>();

        // Used to identify duplicate recipes
        Set<String> seenKeys =
                new HashSet<>();

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        try {

            JSONArray array =
                    new JSONArray(
                            prefs.getString(
                                    KEY_RECIPES,
                                    "[]"
                            )
                    );

            // Cleaned version of the stored recipes
            JSONArray cleanedArray =
                    new JSONArray();

            boolean changed = false;

            // ----------------------------------------------------
            // Read every stored recipe
            // ----------------------------------------------------

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                String name =
                        object.optString(
                                "recipeName",
                                ""
                        );

                List<String> ingredients =
                        split(
                                object.optString(
                                        "ingredientsUsed",
                                        ""
                                )
                        );

                // Create a normalized identity for the recipe
                String key =
                        normalize(name)
                                + "|||"
                                + normalizeIngredients(
                                ingredients
                        );

                // ------------------------------------------------
                // Remove duplicates
                // ------------------------------------------------

                if (name.trim().isEmpty()
                        || !seenKeys.add(key)) {

                    changed = true;
                    continue;
                }

                // Keep this recipe
                cleanedArray.put(object);

                // ------------------------------------------------
                // Convert JSON → RecipeResponse
                // ------------------------------------------------

                RecipeResponse recipe =
                        new RecipeResponse();

                recipe.setRecipeName(
                        object.optString(
                                "recipeName",
                                ""
                        )
                );

                recipe.setCookTime(
                        object.optString(
                                "cookTime",
                                ""
                        )
                );

                recipe.setDifficulty(
                        object.optString(
                                "difficulty",
                                ""
                        )
                );

                recipe.setWasteTip(
                        object.optString(
                                "wasteTip",
                                ""
                        )
                );

                recipe.setNutrition(
                        object.optString(
                                "nutrition",
                                ""
                        )
                );

                recipe.setIngredientsUsed(
                        split(
                                object.optString(
                                        "ingredientsUsed",
                                        ""
                                )
                        )
                );

                recipe.setMissingIngredients(
                        split(
                                object.optString(
                                        "missingIngredients",
                                        ""
                                )
                        )
                );

                recipe.setSteps(
                        split(
                                object.optString(
                                        "steps",
                                        ""
                                )
                        )
                );

                recipes.add(recipe);
            }

            // ----------------------------------------------------
            // Rewrite storage only if duplicates were removed
            // ----------------------------------------------------

            if (changed) {

                prefs.edit()
                        .putString(
                                KEY_RECIPES,
                                cleanedArray.toString()
                        )
                        .apply();
            }

        } catch (Exception ignored) {
        }

        return recipes;
    }

    // ============================================================
    // NORMALIZE RECIPE NAME
    // ============================================================

    private static String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.US)
                .replaceAll("\\s+", " ");
    }

    // ============================================================
    // NORMALIZE INGREDIENTS
    // ============================================================

    private static String normalizeIngredients(
            List<String> ingredients) {

        if (ingredients == null
                || ingredients.isEmpty()) {

            return "";
        }

        List<String> normalized =
                new ArrayList<>();

        for (String ingredient : ingredients) {

            if (ingredient == null) {
                continue;
            }

            String value =
                    ingredient
                            .trim()
                            .toLowerCase(Locale.US)
                            .replaceAll("\\s+", " ");

            if (!value.isEmpty()) {
                normalized.add(value);
            }
        }

        // Order should not matter
        Collections.sort(normalized);

        return android.text.TextUtils.join(
                "|||",
                normalized
        );
    }

    // ============================================================
    // JOIN LIST → STRING
    // ============================================================

    private static String join(List<String> list) {

        if (list == null
                || list.isEmpty()) {

            return "";
        }

        return android.text.TextUtils.join(
                "|||",
                list
        );
    }

    // ============================================================
    // STRING → LIST
    // ============================================================

    private static List<String> split(String value) {

        List<String> list =
                new ArrayList<>();

        if (value == null
                || value.trim().isEmpty()) {

            return list;
        }

        String[] parts =
                value.split("\\|\\|\\|");

        for (String part : parts) {

            if (part != null
                    && !part.trim().isEmpty()) {

                list.add(part.trim());
            }
        }

        return list;
    }

    // ============================================================
    // SAFE STRING
    // ============================================================

    private static String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    public static void deleteRecipe(
            Context context,
            RecipeResponse recipe) {

        if (recipe == null) return;

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        try {

            JSONArray existingRecipes =
                    new JSONArray(
                            prefs.getString(
                                    KEY_RECIPES,
                                    "[]"
                            )
                    );

            JSONArray updatedRecipes =
                    new JSONArray();

            String targetName =
                    normalize(recipe.getRecipeName());

            String targetIngredients =
                    normalizeIngredients(
                            recipe.getIngredientsUsed()
                    );

            boolean deleted = false;

            for (int i = 0;
                 i < existingRecipes.length();
                 i++) {

                JSONObject existing =
                        existingRecipes.getJSONObject(i);

                String existingName =
                        normalize(
                                existing.optString(
                                        "recipeName",
                                        ""
                                )
                        );

                String existingIngredients =
                        normalizeIngredients(
                                split(
                                        existing.optString(
                                                "ingredientsUsed",
                                                ""
                                        )
                                )
                        );

                // Skip the matching recipe once
                if (!deleted
                        && existingName.equals(targetName)
                        && existingIngredients.equals(
                        targetIngredients
                )) {

                    deleted = true;
                    continue;
                }

                updatedRecipes.put(existing);
            }

            if (deleted) {

                prefs.edit()
                        .putString(
                                KEY_RECIPES,
                                updatedRecipes.toString()
                        )
                        .apply();
            }

        } catch (Exception ignored) {
        }
    }
}