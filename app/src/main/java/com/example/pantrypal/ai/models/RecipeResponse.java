package com.example.pantrypal.ai.models;

import java.util.List;

public class RecipeResponse {

    private String recipeName;
    private String cookTime;
    private String difficulty;

    private List<String> ingredientsUsed;
    private List<String> missingIngredients;
    private List<String> steps;

    private String wasteTip;
    private String nutrition;

    public RecipeResponse() {
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getIngredientsUsed() {
        return ingredientsUsed;
    }

    public void setIngredientsUsed(List<String> ingredientsUsed) {
        this.ingredientsUsed = ingredientsUsed;
    }

    public List<String> getMissingIngredients() {
        return missingIngredients;
    }

    public void setMissingIngredients(List<String> missingIngredients) {
        this.missingIngredients = missingIngredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getWasteTip() {
        return wasteTip;
    }

    public void setWasteTip(String wasteTip) {
        this.wasteTip = wasteTip;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }
}