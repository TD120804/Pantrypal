package com.example.pantrypal.ai;

import android.util.Log;

import com.example.pantrypal.ai.models.RecipeResponse;
import com.example.pantrypal.models.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {

    public interface Callback {
        void onSuccess(RecipeResponse recipe);
        void onError(String error);
    }

    private final GeminiClient geminiClient = new GeminiClient();

    public void generateRecipe(
            List<GroceryItem> pantryItems,
            Callback callback
    ) {

        Log.d("AI_DEBUG", "RecipeRepository.generateRecipe()");

        if (pantryItems == null || pantryItems.isEmpty()) {
            callback.onError("Your pantry is empty. Add some items first!");
            return;
        }

        String prompt = RecipePromptBuilder.buildPrompt(pantryItems);

        geminiClient.generateRecipe(
                prompt,
                new GeminiClient.GeminiCallback() {

                    @Override
                    public void onSuccess(String response) {

                        try {

                            Log.d("AI_DEBUG", "Raw AI response:\n" + response);

                            RecipeResponse recipe = parseResponse(response);

                            if (recipe.getRecipeName() == null
                                    || recipe.getRecipeName().isEmpty()) {

                                callback.onError(
                                        "Could not understand the recipe response."
                                );

                                return;
                            }

                            callback.onSuccess(recipe);

                        } catch (Exception e) {

                            Log.e(
                                    "AI_DEBUG",
                                    "Recipe parsing failed",
                                    e
                            );

                            callback.onError(
                                    "Failed to process the recipe."
                            );
                        }
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "AI_DEBUG",
                                "AI generation failed: " + error
                        );

                        callback.onError(error);
                    }
                }
        );
    }

    private RecipeResponse parseResponse(String text) {

        RecipeResponse recipe = new RecipeResponse();

        List<String> ingredients = new ArrayList<>();
        List<String> missingIngredients = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        String currentSection = "";

        String[] lines = text.split("\\r?\\n");

        for (String rawLine : lines) {

            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("Recipe Name:")) {

                recipe.setRecipeName(
                        getValue(line, "Recipe Name:")
                );
                currentSection = "";

            } else if (line.startsWith("Cooking Time:")) {

                recipe.setCookTime(
                        getValue(line, "Cooking Time:")
                );
                currentSection = "";

            } else if (line.startsWith("Difficulty:")) {

                recipe.setDifficulty(
                        getValue(line, "Difficulty:")
                );
                currentSection = "";

            } else if (line.equalsIgnoreCase("Ingredients Used:")) {

                currentSection = "ingredients";

            } else if (line.equalsIgnoreCase("Missing Ingredients:")) {

                currentSection = "missing";

            } else if (line.equalsIgnoreCase("Steps:")) {

                currentSection = "steps";

            } else if (line.startsWith("Waste Tip:")) {

                recipe.setWasteTip(
                        getValue(line, "Waste Tip:")
                );
                currentSection = "";

            } else if (line.startsWith("Nutrition:")) {

                recipe.setNutrition(
                        getValue(line, "Nutrition:")
                );
                currentSection = "";

            } else if (currentSection.equals("ingredients")
                    && line.startsWith("-")) {

                ingredients.add(
                        line.substring(1).trim()
                );

            } else if (currentSection.equals("missing")
                    && line.startsWith("-")) {

                missingIngredients.add(
                        line.substring(1).trim()
                );

            } else if (currentSection.equals("steps")
                    && line.matches("^\\d+\\..*")) {

                steps.add(line);
            }
        }

        recipe.setIngredientsUsed(ingredients);
        recipe.setMissingIngredients(missingIngredients);
        recipe.setSteps(steps);

        return recipe;
    }

    private String getValue(String line, String label) {

        return line.substring(label.length()).trim();
    }
}