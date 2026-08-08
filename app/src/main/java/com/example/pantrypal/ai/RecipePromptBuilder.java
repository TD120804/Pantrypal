package com.example.pantrypal.ai;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;

public class RecipePromptBuilder {

    public static String buildPrompt(List<GroceryItem> items) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are PantryPal AI, an intelligent kitchen assistant.

Your task is to generate ONE recipe using the available pantry items.

IMPORTANT RULES:
- Prefer ingredients that expire soon.
- Minimize food waste.
- Keep the recipe simple.
- If an ingredient is missing, mention it separately.
- Keep the response concise.
- Return ONLY the format below.
- Do NOT use Markdown.
- Do NOT add introductions or explanations.

FORMAT:

Recipe Name:
Cooking Time:
Difficulty:

Ingredients Used:
- item
- item

Missing Ingredients:
- item
- item

Steps:
1.
2.
3.
4.

Waste Tip:

Nutrition:

Available Pantry Items:
""");

        for (GroceryItem item : items) {

            prompt.append("- ")
                    .append(item.getName())
                    .append(" (Qty: ")
                    .append(item.getQuantity())
                    .append(")\n");
        }

        return prompt.toString();
    }
}