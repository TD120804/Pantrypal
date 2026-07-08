package com.example.pantrypal.utils;

import com.example.pantrypal.models.GroceryItem;

import java.util.List;

public class RecommendationEngine {

    public static String getRecommendation(List<GroceryItem> items) {

        if (items == null || items.isEmpty()) {
            return "🛒 Your pantry is empty. Add some groceries!";
        }

        for (GroceryItem item : items) {

            int days = RiskCalculator.getDaysLeft(item.getExpiryDate());

            if (days < 0) {
                return "❌ " + item.getName() + " has expired. Remove it.";
            }

            if (days <= 2) {
                return "⚠ Use " + item.getName() + " within " + days + " day(s).";
            }

            if (item.getQuantity() <= 1) {
                return "🛒 Running low on " + item.getName() + ".";
            }
        }

        return "✅ Your pantry looks healthy!";
    }
}