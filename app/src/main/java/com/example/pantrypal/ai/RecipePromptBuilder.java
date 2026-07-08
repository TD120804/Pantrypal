package com.example.pantrypal.ai;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;

public class RecipePromptBuilder {

    public static String buildPrompt(List<GroceryItem> items) {

        StringBuilder prompt = new StringBuilder();

        prompt.append(
                "You are PantryPal AI, an intelligent kitchen assistant.\n\n");

        prompt.append(
                "Using ONLY the pantry items below, suggest ONE Indian recipe.\n");

        prompt.append(
                "Mention:\n");

        prompt.append("- Recipe Name\n");
        prompt.append("- Cooking Time\n");
        prompt.append("- Ingredients Used\n");
        prompt.append("- Missing Ingredients\n");
        prompt.append("- Step-by-step Instructions\n");
        prompt.append("- A tip to reduce food waste.\n\n");

        prompt.append("Available Pantry Items:\n");

        for (GroceryItem item : items) {

            prompt.append("- ")
                    .append(item.getName())
                    .append("\n");
        }

        return prompt.toString();
    }

}