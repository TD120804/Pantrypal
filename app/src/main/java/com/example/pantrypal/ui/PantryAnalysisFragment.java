package com.example.pantrypal.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.utils.RiskCalculator;
import com.example.pantrypal.viewmodels.GroceryViewModel;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantryAnalysisFragment extends Fragment {

    private GroceryViewModel viewModel;

    private TextView analysisScore;
    private TextView analysisStatus;

    private TextView moneySaved;
    private TextView itemsAvoided;
    private TextView recipesCooked;

    private LineChart healthTrendChart;
    private PieChart inventoryCategoryChart;

    public PantryAnalysisFragment() {
        super(R.layout.fragment_pantry_analysis);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        analysisScore =
                view.findViewById(R.id.textAnalysisScore);

        analysisStatus =
                view.findViewById(R.id.textAnalysisStatus);

        moneySaved =
                view.findViewById(R.id.textMoneySaved);

        itemsAvoided =
                view.findViewById(R.id.textItemsAvoided);

        recipesCooked =
                view.findViewById(R.id.textRecipesCooked);

        healthTrendChart =
                view.findViewById(R.id.healthTrendChart);

        inventoryCategoryChart =
                view.findViewById(R.id.inventoryCategoryChart);

        viewModel = new ViewModelProvider(requireActivity())
                .get(GroceryViewModel.class);

        viewModel.getAllItems().observe(
                getViewLifecycleOwner(),
                this::updateAnalytics
        );
    }

    private void updateAnalytics(List<GroceryItem> items) {

        if (items == null) {
            return;
        }

        int healthScore = calculateHealthScore(items);

        analysisScore.setText(
                healthScore + "%"
        );

        updateHealthStatus(healthScore);

        /*
         * These values will become real historical
         * analytics once we add tracking for savings,
         * waste and cooked recipes.
         *
         * For now we deliberately show 0 instead
         * of inventing data.
         */
        moneySaved.setText("₹0");
        itemsAvoided.setText("0");
        recipesCooked.setText("0");

        createHealthChart(healthScore);

        createCategoryChart(items);
    }

    // =====================================================
    // PANTRY HEALTH
    // =====================================================

    private int calculateHealthScore(
            List<GroceryItem> items
    ) {

        if (items.isEmpty()) {
            return 0;
        }

        int score = 100;

        for (GroceryItem item : items) {

            int days =
                    RiskCalculator.getDaysLeft(
                            item.getExpiryDate()
                    );

            if (days < 0) {

                score -= 15;

            } else if (days <= 3) {

                score -= 5;
            }
        }

        return Math.max(0, score);
    }

    private void updateHealthStatus(int score) {

        if (score >= 80) {

            analysisStatus.setText(
                    "Excellent"
            );

        } else if (score >= 60) {

            analysisStatus.setText(
                    "Good"
            );

        } else if (score >= 40) {

            analysisStatus.setText(
                    "Needs Attention"
            );

        } else {

            analysisStatus.setText(
                    "Poor"
            );
        }
    }

    // =====================================================
    // HEALTH CHART
    // =====================================================

    private void createHealthChart(int score) {

        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, score));

        LineDataSet dataSet = new LineDataSet(
                entries,
                "Pantry Health"
        );

        // 🌿 PantryPal green
        dataSet.setColor(
                android.graphics.Color.rgb(110, 158, 74)
        );

        dataSet.setCircleColor(
                android.graphics.Color.rgb(110, 158, 74)
        );

        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);

        dataSet.setDrawValues(true);

        dataSet.setValueTextColor(
                android.graphics.Color.rgb(70, 70, 70)
        );

        dataSet.setValueTextSize(11f);

        LineData lineData = new LineData(dataSet);

        healthTrendChart.setData(lineData);

        // Remove unnecessary chart elements
        healthTrendChart.getDescription().setEnabled(false);
        healthTrendChart.getLegend().setEnabled(false);

        // Y axis
        healthTrendChart.getAxisLeft().setAxisMinimum(0);
        healthTrendChart.getAxisLeft().setAxisMaximum(120);

        healthTrendChart.getAxisLeft().setTextColor(
                android.graphics.Color.rgb(120, 120, 120)
        );

        healthTrendChart.getAxisLeft().setTextSize(10f);

        healthTrendChart.getAxisLeft().setDrawGridLines(true);

        // Right axis OFF
        healthTrendChart.getAxisRight().setEnabled(false);

        // X axis
        XAxis xAxis = healthTrendChart.getXAxis();

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        xAxis.setTextColor(
                android.graphics.Color.rgb(120, 120, 120)
        );

        xAxis.setTextSize(10f);

        xAxis.setDrawGridLines(false);

        // Extra spacing
        healthTrendChart.setExtraBottomOffset(8f);
        healthTrendChart.setExtraLeftOffset(8f);
        healthTrendChart.setExtraRightOffset(8f);

        healthTrendChart.setTouchEnabled(false);
        healthTrendChart.setDragEnabled(false);
        healthTrendChart.setScaleEnabled(false);

        healthTrendChart.invalidate();
    }

    // =====================================================
    // INVENTORY BY CATEGORY
    // =====================================================

    private void createCategoryChart(
            List<GroceryItem> items
    ) {

        Map<String, Integer> categoryCounts =
                new HashMap<>();

        for (GroceryItem item : items) {

            String category = item.getCategory();

            if (category == null
                    || category.trim().isEmpty()) {

                category = "Others";
            }

            categoryCounts.put(
                    category,
                    categoryCounts.getOrDefault(
                            category,
                            0
                    ) + 1
            );
        }

        ArrayList<PieEntry> entries =
                new ArrayList<>();

        for (Map.Entry<String, Integer> entry
                : categoryCounts.entrySet()) {

            entries.add(
                    new PieEntry(
                            entry.getValue(),
                            entry.getKey()
                    )
            );
        }

        PieDataSet dataSet =
                new PieDataSet(
                        entries,
                        "Categories"
                );

        dataSet.setDrawValues(true);
        dataSet.setSliceSpace(2f);

        PieData pieData =
                new PieData(dataSet);

        inventoryCategoryChart.setData(
                pieData
        );

        inventoryCategoryChart
                .setUsePercentValues(true);

        inventoryCategoryChart
                .setDrawHoleEnabled(true);

        inventoryCategoryChart
                .setHoleRadius(55f);

        inventoryCategoryChart
                .setTransparentCircleRadius(60f);

        inventoryCategoryChart
                .setCenterText(
                        items.size()
                                + "\nItems"
                );

        inventoryCategoryChart
                .setCenterTextSize(14f);

        inventoryCategoryChart
                .getDescription()
                .setEnabled(false);

        inventoryCategoryChart
                .getLegend()
                .setEnabled(true);

        inventoryCategoryChart.invalidate();
    }
}