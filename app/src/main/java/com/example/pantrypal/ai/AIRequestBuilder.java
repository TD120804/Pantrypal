package com.example.pantrypal.ai;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;

public class AIRequestBuilder {

    private AIRequestBuilder() {
        // Utility class
    }

    public static String buildRecipePrompt(List<GroceryItem> pantryItems) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are PantryPal AI.

Using ONLY the pantry items below, suggest ONE simple recipe.

Rules:
- Give the recipe name.
- Mention which pantry ingredients are used.
- Mention any optional missing ingredients.
- Give short numbered cooking steps.
- Keep the answer under 250 words.
- Do not use Markdown.
- Be concise.

Available Pantry:
""");

        for (GroceryItem item : pantryItems) {

            prompt.append("- ")
                    .append(item.getName())
                    .append(" (Qty: ")
                    .append(item.getQuantity())
                    .append(")\n");
        }

        return prompt.toString();
    }
}