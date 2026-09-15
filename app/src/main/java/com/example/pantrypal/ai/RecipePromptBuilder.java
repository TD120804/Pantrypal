package com.example.pantrypal.ai;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;
import java.util.Random;

public class RecipePromptBuilder {

    public static String buildPrompt(List<GroceryItem> items) {

        StringBuilder prompt = new StringBuilder();

        // Give the AI a different creative direction each time
        String[] recipeDirections = {
                "Try a homestyle curry, sabzi, or gravy-based dish.",
                "Try a rice-based dish such as pulao, fried rice, or khichdi.",
                "Try a roti, paratha, wrap, or stuffed flatbread style dish.",
                "Try a quick stir-fry, sauté, or one-pan dish.",
                "Try a dal, lentil, or protein-focused dish if suitable ingredients exist.",
                "Try a breakfast or light-meal style dish such as poha, upma, cheela, or similar.",
                "Try a snack, chaat, cutlet, or quick bite if the ingredients support it.",
                "Try a soup, stew, or comforting bowl-style dish if appropriate.",
                "Try a creative Indian-style fusion dish using the pantry ingredients.",
                "Try a completely different preparation method from a typical stir-fry or pan-seared dish."
        };

        String creativeDirection =
                recipeDirections[
                        new Random().nextInt(recipeDirections.length)
                        ];

        prompt.append("""
        You are PantryPal AI, an intelligent kitchen assistant designed primarily
        for Indian households.

        Generate ONE practical, realistic recipe using the user's pantry items.

        IMPORTANT RECIPE VARIETY RULE:
        - Every generated recipe should feel meaningfully different.
        - Avoid repeatedly generating the same type of dish with only a slightly
          different name.
        - Do NOT default to chicken stir-fry, pan-seared chicken, or similar dishes
          when another preparation is practical.
        - Vary the cooking method, dish type, texture, and flavor profile whenever
          the pantry allows it.
        - Do not simply rename a previous style of recipe.
        - Prefer a genuinely different recipe concept rather than a minor variation.

        CURRENT CREATIVE DIRECTION:
        """);

        prompt.append(creativeDirection);
        prompt.append("""

        CUISINE PREFERENCE:
        - Prefer Indian recipes whenever the available pantry ingredients support one.
        - Prioritize familiar Indian home-style dishes over generic Western recipes.
        - Consider common Indian cooking styles such as sabzi, dal, pulao, khichdi,
          poha, upma, paratha, roti, raita, chutney, kheer and similar dishes
          when appropriate.
        - If an Indian recipe is not practical, create an Indian-style adaptation
          only when the ingredients genuinely support it.
        - Use international recipes only when they are a better fit for the pantry.
        - Do NOT force Indian ingredients, spices or cooking methods into a recipe
          when they do not make culinary sense.

        PANTRY RULES:
        - Use the user's actual pantry ingredients.
        - Do NOT assume an ingredient is available unless it appears in the pantry.
        - You may suggest reasonable missing ingredients, but clearly list them
          under Missing Ingredients.
        - Prioritize ingredients that are nearing their expiry date.
        - Help reduce food waste.
        - Prefer recipes that use multiple available ingredients.
        - Keep the recipe practical for a normal Indian household kitchen.

        RECIPE QUALITY:
        - Give realistic cooking instructions.
        - Use familiar Indian measurements where appropriate
          (cup, tablespoon, teaspoon, etc.).
        - Keep the recipe simple enough for a home cook.
        - Do not make the recipe unnecessarily complicated.
        - If the pantry contains only a few ingredients, create a simple recipe
          rather than inventing many additional ingredients.

        RETURN YOUR RESPONSE IN EXACTLY THIS FORMAT.
        Do not use Markdown.
        Do not add any introduction before "Recipe Name:".
        Do not add extra sections.

        Recipe Name: [recipe name]
        Cooking Time: [time]
        Difficulty: [Easy/Medium/Hard]

        Ingredients Used:
        - [ingredient]
        - [ingredient]

        Missing Ingredients:
        - [ingredient]
        - [ingredient]

        Steps:
        1. [step]
        2. [step]
        3. [step]
        4. [step]

        Waste Tip: [short food waste reduction tip]

        Nutrition: [short nutrition summary]

        USER'S PANTRY:
        """);

        for (GroceryItem item : items) {

            prompt.append("- ")
                    .append(item.getName())
                    .append(" | Quantity: ")
                    .append(item.getQuantity())
                    .append(" | Expiry: ")
                    .append(item.getExpiryDate())
                    .append("\n");
        }

        return prompt.toString();
    }
}