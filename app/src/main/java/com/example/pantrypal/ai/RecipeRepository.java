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

    public void generateRecipe(List<GroceryItem> pantryItems,
                               Callback callback) {
        Log.d("AI_DEBUG", "RecipeRepository.generateRecipe()");
        String prompt = RecipePromptBuilder.buildPrompt(pantryItems);

        geminiClient.generateRecipe(prompt, new GeminiClient.GeminiCallback() {

            @Override
            public void onSuccess(String response) {

                try {

                    RecipeResponse recipe = parseResponse(response);

                    callback.onSuccess(recipe);

                } catch (Exception e) {

                    callback.onError(e.getMessage());

                }

            }

            @Override
            public void onError(String error) {

                callback.onError(error);

            }

        });

    }

    private RecipeResponse parseResponse(String text) {

        RecipeResponse recipe = new RecipeResponse();

        String[] lines = text.split("\n");

        List<String> ingredients = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        String currentSection = "";

        for (String line : lines) {

            line = line.trim();

            if (line.startsWith("Recipe Name:")) {

                recipe.setRecipeName(
                        line.replace("Recipe Name:", "").trim());

            }

            else if (line.startsWith("Cooking Time:")) {

                recipe.setCookTime(
                        line.replace("Cooking Time:", "").trim());

            }

            else if (line.startsWith("Difficulty:")) {

                recipe.setDifficulty(
                        line.replace("Difficulty:", "").trim());

            }

            else if (line.equalsIgnoreCase("Ingredients Used:")) {

                currentSection = "ingredients";

            }

            else if (line.equalsIgnoreCase("Missing Ingredients:")) {

                currentSection = "missing";

            }

            else if (line.equalsIgnoreCase("Steps:")) {

                currentSection = "steps";

            }

            else if (line.startsWith("Waste Tip:")) {

                currentSection = "";

                recipe.setWasteTip(
                        line.replace("Waste Tip:", "").trim());

            }

            else if (line.startsWith("Nutrition:")) {

                currentSection = "";

                recipe.setNutrition(
                        line.replace("Nutrition:", "").trim());

            }

            else if (line.startsWith("-")) {

                if (currentSection.equals("ingredients"))

                    ingredients.add(
                            line.replace("-", "").trim());

                else if (currentSection.equals("missing"))

                    missing.add(
                            line.replace("-", "").trim());

            }

            else if (line.matches("\\d+\\..*")) {

                if (currentSection.equals("steps"))

                    steps.add(line);

            }

        }

        recipe.setIngredientsUsed(ingredients);
        recipe.setMissingIngredients(missing);
        recipe.setSteps(steps);

        return recipe;

    }

}