package com.example.pantrypal.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PantryAnalysisFragment extends Fragment {

    // =====================================================
    // KPI TEXT VIEWS
    // =====================================================

    private TextView totalItems;
    private TextView wasteRisk;
    private TextView atRiskItems;
    private TextView expiringItems;
    private TextView recipeAvailable;

    // =====================================================
    // CHARTS
    // =====================================================

    private LineChart healthTrendChart;
    private PieChart inventoryCategoryChart;

    // =====================================================
    // FILTERS
    // =====================================================

    private MaterialButton btnTimeFilter;
    private MaterialButton btnCategoryFilter;

    // =====================================================
    // INFO BUTTONS
    // =====================================================

    private MaterialButton btnWasteRiskInfo;
    private ImageButton btnCategoryInfo;

    // =====================================================
    // DATA
    // =====================================================

    private List<GroceryItem> currentItems = new ArrayList<>();

    private String selectedTimeFilter = "This Week";
    private String selectedCategoryFilter = "All Categories";

    // =====================================================
    // VIEW MODEL
    // =====================================================

    private GroceryViewModel viewModel;

    // =====================================================
    // HEALTH HISTORY
    // =====================================================

    private static final String ANALYTICS_PREFS =
            "pantrypal_analytics";

    private static final String HEALTH_HISTORY_KEY =
            "health_history";

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantryAnalysisFragment() {
        super(R.layout.fragment_pantry_analysis);
    }

    // =====================================================
    // VIEW CREATED
    // =====================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        // -------------------------------------------------
        // KPI VIEWS
        // -------------------------------------------------

        totalItems =
                view.findViewById(R.id.textTotalItems);

        wasteRisk =
                view.findViewById(R.id.textWasteRisk);

        atRiskItems =
                view.findViewById(R.id.textAtRiskItems);

        expiringItems =
                view.findViewById(R.id.textExpiringItems);

        recipeAvailable =
                view.findViewById(R.id.textRecipeAvailable);

        // -------------------------------------------------
        // CHARTS
        // -------------------------------------------------

        healthTrendChart =
                view.findViewById(R.id.healthTrendChart);

        inventoryCategoryChart =
                view.findViewById(R.id.inventoryCategoryChart);

        // -------------------------------------------------
        // FILTER BUTTONS
        // -------------------------------------------------

        btnTimeFilter =
                view.findViewById(R.id.btnTimeFilter);

        btnCategoryFilter =
                view.findViewById(R.id.btnCategoryFilter);

        // -------------------------------------------------
        // INFO BUTTONS
        // -------------------------------------------------

        btnWasteRiskInfo =
                view.findViewById(R.id.btnWasteRiskInfo);

        btnCategoryInfo =
                view.findViewById(R.id.btnCategoryInfo);

        // -------------------------------------------------
        // FILTER LISTENERS
        // -------------------------------------------------

        btnTimeFilter.setOnClickListener(
                v -> showTimeFilter()
        );

        btnCategoryFilter.setOnClickListener(
                v -> showCategoryFilter()
        );

        // -------------------------------------------------
        // INFO LISTENERS
        // -------------------------------------------------

        btnWasteRiskInfo.setOnClickListener(
                v -> showWasteRiskInfo()
        );

        btnCategoryInfo.setOnClickListener(
                v -> showCategoryInfo()
        );

        // -------------------------------------------------
        // VIEW MODEL
        // -------------------------------------------------

        viewModel =
                new ViewModelProvider(requireActivity())
                        .get(GroceryViewModel.class);

        viewModel
                .getAllItems()
                .observe(
                        getViewLifecycleOwner(),
                        items -> {

                            if (items == null) {
                                return;
                            }

                            currentItems =
                                    new ArrayList<>(items);

                            updateAnalytics(currentItems);
                        }
                );
    }

    // =====================================================
    // MAIN ANALYTICS UPDATE
    // =====================================================

    private void updateAnalytics(
            List<GroceryItem> items
    ) {

        if (items == null) {
            return;
        }

        // -------------------------------------------------
        // CALCULATE VALUES
        // -------------------------------------------------

        int healthScore =
                calculateHealthScore(items);

        int total =
                items.size();

        int averageWasteRisk =
                calculateAverageWasteRisk(items);

        int atRisk =
                countAtRisk(items);

        int expiring =
                countExpiring(items);

        /*
         * PantryPal does not currently store completed
         * recipe history.
         *
         * Therefore this represents whether the current
         * pantry can provide an AI recipe suggestion.
         */
        int recipeCount =
                items.isEmpty() ? 0 : 1;

        // -------------------------------------------------
        // KPI VALUES
        // -------------------------------------------------

        totalItems.setText(
                String.valueOf(total)
        );

        wasteRisk.setText(
                averageWasteRisk + "%"
        );

        atRiskItems.setText(
                String.valueOf(atRisk)
        );

        expiringItems.setText(
                String.valueOf(expiring)
        );

        recipeAvailable.setText(
                String.valueOf(recipeCount)
        );

        // -------------------------------------------------
        // SAVE TODAY'S HEALTH SCORE
        // -------------------------------------------------

        saveTodayHealthScore(healthScore);

        // -------------------------------------------------
        // UPDATE CHARTS
        // -------------------------------------------------

        createHealthChart();

        createCategoryChart(
                getFilteredItems(items)
        );
    }

    // =====================================================
    // PANTRY HEALTH SCORE
    // =====================================================

    private int calculateHealthScore(
            List<GroceryItem> items
    ) {

        if (items == null || items.isEmpty()) {
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

    // =====================================================
    // AVERAGE WASTE RISK
    // =====================================================

    private int calculateAverageWasteRisk(
            List<GroceryItem> items
    ) {

        if (items == null || items.isEmpty()) {
            return 0;
        }

        int totalRisk = 0;
        int validItems = 0;

        for (GroceryItem item : items) {

            int risk =
                    RiskCalculator.calculateRisk(
                            item.getExpiryDate(),
                            item.getQuantity()
                    );

            totalRisk += risk;
            validItems++;
        }

        if (validItems == 0) {
            return 0;
        }

        return totalRisk / validItems;
    }

    // =====================================================
    // AT RISK COUNT
    // =====================================================

    private int countAtRisk(
            List<GroceryItem> items
    ) {

        int count = 0;

        for (GroceryItem item : items) {

            int days =
                    RiskCalculator.getDaysLeft(
                            item.getExpiryDate()
                    );

            if (days <= 3) {
                count++;
            }
        }

        return count;
    }

    // =====================================================
    // EXPIRING SOON COUNT
    // =====================================================

    private int countExpiring(
            List<GroceryItem> items
    ) {

        int count = 0;

        for (GroceryItem item : items) {

            int days =
                    RiskCalculator.getDaysLeft(
                            item.getExpiryDate()
                    );

            if (days >= 0 && days <= 7) {
                count++;
            }
        }

        return count;
    }

    // =====================================================
    // SAVE TODAY'S HEALTH SCORE
    // =====================================================

    private void saveTodayHealthScore(
            int score
    ) {

        SharedPreferences prefs =
                requireContext()
                        .getSharedPreferences(
                                ANALYTICS_PREFS,
                                Context.MODE_PRIVATE
                        );

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        String history =
                prefs.getString(
                        HEALTH_HISTORY_KEY,
                        ""
                );

        Map<String, Integer> historyMap =
                parseHealthHistory(history);

        historyMap.put(today, score);

        // Keep latest 7 dates
        List<String> dates =
                new ArrayList<>(
                        historyMap.keySet()
                );

        Collections.sort(dates);

        while (dates.size() > 7) {

            historyMap.remove(
                    dates.get(0)
            );

            dates.remove(0);
        }

        prefs.edit()
                .putString(
                        HEALTH_HISTORY_KEY,
                        serializeHealthHistory(
                                historyMap
                        )
                )
                .apply();
    }

    // =====================================================
    // PARSE HEALTH HISTORY
    // =====================================================

    private Map<String, Integer> parseHealthHistory(
            String history
    ) {

        Map<String, Integer> result =
                new HashMap<>();

        if (history == null || history.isEmpty()) {
            return result;
        }

        String[] entries =
                history.split(",");

        for (String entry : entries) {

            String[] parts =
                    entry.split(":");

            if (parts.length != 2) {
                continue;
            }

            try {

                result.put(
                        parts[0],
                        Integer.parseInt(parts[1])
                );

            } catch (NumberFormatException ignored) {
                // Ignore malformed entries
            }
        }

        return result;
    }

    // =====================================================
    // SERIALIZE HEALTH HISTORY
    // =====================================================

    private String serializeHealthHistory(
            Map<String, Integer> history
    ) {

        StringBuilder builder =
                new StringBuilder();

        List<String> dates =
                new ArrayList<>(
                        history.keySet()
                );

        Collections.sort(dates);

        for (String date : dates) {

            if (builder.length() > 0) {
                builder.append(",");
            }

            builder.append(date)
                    .append(":")
                    .append(history.get(date));
        }

        return builder.toString();
    }

    // =====================================================
    // HEALTH TREND CHART
    // =====================================================

    private void createHealthChart() {

        SharedPreferences prefs =
                requireContext()
                        .getSharedPreferences(
                                ANALYTICS_PREFS,
                                Context.MODE_PRIVATE
                        );

        String history =
                prefs.getString(
                        HEALTH_HISTORY_KEY,
                        ""
                );

        Map<String, Integer> historyMap =
                parseHealthHistory(history);

        ArrayList<Entry> entries =
                new ArrayList<>();

        ArrayList<String> labels =
                new ArrayList<>();

        // -------------------------------------------------
        // SORT DATES
        // -------------------------------------------------

        ArrayList<String> dates =
                new ArrayList<>(
                        historyMap.keySet()
                );

        Collections.sort(dates);

        // -------------------------------------------------
        // CREATE REAL X/Y POINTS
        // -------------------------------------------------

        for (int i = 0; i < dates.size(); i++) {

            String date =
                    dates.get(i);

            Integer score =
                    historyMap.get(date);

            if (score == null) {
                continue;
            }

            entries.add(
                    new Entry(
                            i,
                            score
                    )
            );

            try {

                Date parsedDate =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        ).parse(date);

                String displayDate =
                        new SimpleDateFormat(
                                "dd/MM",
                                Locale.getDefault()
                        ).format(parsedDate);

                labels.add(displayDate);

            } catch (Exception e) {

                labels.add(date);
            }
        }

        // -------------------------------------------------
        // FALLBACK
        // -------------------------------------------------

        if (entries.isEmpty() &&
                !currentItems.isEmpty()) {

            int currentScore =
                    calculateHealthScore(
                            currentItems
                    );

            entries.add(
                    new Entry(
                            0,
                            currentScore
                    )
            );

            labels.add("Today");
        }

        // -------------------------------------------------
        // NO DATA
        // -------------------------------------------------

        if (entries.isEmpty()) {

            healthTrendChart.clear();

            healthTrendChart.setNoDataText(
                    "Your health trend will appear here"
            );

            healthTrendChart.setNoDataTextColor(
                    Color.rgb(
                            120,
                            120,
                            120
                    )
            );

            healthTrendChart.invalidate();

            return;
        }

        // -------------------------------------------------
        // DATASET
        // -------------------------------------------------

        LineDataSet dataSet =
                new LineDataSet(
                        entries,
                        "Pantry Health"
                );

        int green =
                Color.rgb(
                        85,
                        122,
                        57
                );

        dataSet.setColor(green);
        dataSet.setCircleColor(green);

        dataSet.setLineWidth(3.5f);

        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(5.5f);
        dataSet.setCircleHoleRadius(2f);

        dataSet.setMode(
                LineDataSet.Mode.LINEAR
        );

        dataSet.setDrawValues(true);

        dataSet.setValueTextColor(
                Color.rgb(
                        70,
                        70,
                        70
                )
        );

        dataSet.setValueTextSize(10f);

        dataSet.setDrawFilled(true);

        dataSet.setFillColor(
                Color.rgb(
                        220,
                        237,
                        207
                )
        );

        dataSet.setFillAlpha(100);

        dataSet.setDrawHighlightIndicators(false);

        // -------------------------------------------------
        // LINE DATA
        // -------------------------------------------------

        healthTrendChart.setData(
                new LineData(dataSet)
        );

        healthTrendChart
                .getDescription()
                .setEnabled(false);

        healthTrendChart
                .getLegend()
                .setEnabled(false);

        // -------------------------------------------------
        // Y AXIS
        // -------------------------------------------------

        healthTrendChart
                .getAxisLeft()
                .setAxisMinimum(0f);

        healthTrendChart
                .getAxisLeft()
                .setAxisMaximum(100f);

        healthTrendChart
                .getAxisLeft()
                .setLabelCount(
                        6,
                        true
                );

        healthTrendChart
                .getAxisLeft()
                .setTextColor(
                        Color.rgb(
                                120,
                                120,
                                120
                        )
                );

        healthTrendChart
                .getAxisLeft()
                .setTextSize(10f);

        healthTrendChart
                .getAxisLeft()
                .setDrawGridLines(true);

        // -------------------------------------------------
        // RIGHT AXIS
        // -------------------------------------------------

        healthTrendChart
                .getAxisRight()
                .setEnabled(false);

        // -------------------------------------------------
        // X AXIS
        // -------------------------------------------------

        XAxis xAxis =
                healthTrendChart.getXAxis();

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        xAxis.setTextColor(
                Color.rgb(
                        120,
                        120,
                        120
                )
        );

        xAxis.setTextSize(10f);

        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(labels)
        );

        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setLabelCount(
                labels.size(),
                true
        );

        xAxis.setAxisMinimum(0f);

        if (entries.size() > 1) {

            xAxis.setAxisMaximum(
                    entries.size() - 1
            );

        } else {

            xAxis.setAxisMaximum(1f);
        }

        // -------------------------------------------------
        // SPACING
        // -------------------------------------------------

        healthTrendChart.setExtraBottomOffset(8f);
        healthTrendChart.setExtraLeftOffset(8f);
        healthTrendChart.setExtraRightOffset(8f);

        // -------------------------------------------------
        // INTERACTION
        // -------------------------------------------------

        healthTrendChart.setTouchEnabled(false);
        healthTrendChart.setDragEnabled(false);
        healthTrendChart.setScaleEnabled(false);

        // -------------------------------------------------
        // ANIMATION
        // -------------------------------------------------

        healthTrendChart.animateX(700);

        healthTrendChart.invalidate();
    }

    // =====================================================
    // FILTERED ITEMS
    // =====================================================

    private List<GroceryItem> getFilteredItems(
            List<GroceryItem> items
    ) {

        if (items == null) {
            return new ArrayList<>();
        }

        if (selectedCategoryFilter.equals(
                "All Categories"
        )) {

            return new ArrayList<>(items);
        }

        List<GroceryItem> filtered =
                new ArrayList<>();

        for (GroceryItem item : items) {

            String category =
                    item.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Others";
            }

            if (category.equals(
                    selectedCategoryFilter
            )) {

                filtered.add(item);
            }
        }

        return filtered;
    }

    // =====================================================
    // CATEGORY FILTER
    // =====================================================

    private void showCategoryFilter() {

        Set<String> categories =
                new LinkedHashSet<>();

        categories.add(
                "All Categories"
        );

        for (GroceryItem item : currentItems) {

            String category =
                    item.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Others";
            }

            categories.add(category);
        }

        String[] categoryArray =
                categories.toArray(
                        new String[0]
                );

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Choose Category")
                .setSingleChoiceItems(
                        categoryArray,
                        getSelectedCategoryIndex(
                                categoryArray
                        ),
                        (dialog, which) -> {

                            selectedCategoryFilter =
                                    categoryArray[which];

                            btnCategoryFilter.setText(
                                    selectedCategoryFilter
                                            + "  ▼"
                            );

                            createCategoryChart(
                                    getFilteredItems(
                                            currentItems
                                    )
                            );

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // =====================================================
    // SELECTED CATEGORY INDEX
    // =====================================================

    private int getSelectedCategoryIndex(
            String[] categories
    ) {

        for (int i = 0;
             i < categories.length;
             i++) {

            if (categories[i].equals(
                    selectedCategoryFilter
            )) {

                return i;
            }
        }

        return 0;
    }

    // =====================================================
    // TIME FILTER
    // =====================================================

    private void showTimeFilter() {

        String[] options = {
                "This Week",
                "Last 7 Days",
                "All Time"
        };

        int selected = 0;

        for (int i = 0;
             i < options.length;
             i++) {

            if (options[i].equals(
                    selectedTimeFilter
            )) {

                selected = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Health History")
                .setSingleChoiceItems(
                        options,
                        selected,
                        (dialog, which) -> {

                            selectedTimeFilter =
                                    options[which];

                            btnTimeFilter.setText(
                                    selectedTimeFilter
                                            + "  ▼"
                            );

                            createHealthChart();

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // =====================================================
    // INVENTORY CATEGORY CHART
    // =====================================================

    private void createCategoryChart(
            List<GroceryItem> items
    ) {

        // -------------------------------------------------
        // EMPTY STATE
        // -------------------------------------------------

        if (items == null ||
                items.isEmpty()) {

            inventoryCategoryChart.clear();

            inventoryCategoryChart.setCenterText(
                    "No\nItems"
            );

            inventoryCategoryChart.setCenterTextColor(
                    Color.rgb(
                            43,
                            33,
                            24
                    )
            );

            inventoryCategoryChart.invalidate();

            return;
        }

        // -------------------------------------------------
        // CATEGORY COUNTS
        // -------------------------------------------------

        Map<String, Integer> categoryCounts =
                new HashMap<>();

        for (GroceryItem item : items) {

            String category =
                    item.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

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

        // -------------------------------------------------
        // PIE ENTRIES
        // -------------------------------------------------

        ArrayList<PieEntry> entries =
                new ArrayList<>();

        for (
                Map.Entry<String, Integer> entry :
                categoryCounts.entrySet()
        ) {

            entries.add(
                    new PieEntry(
                            entry.getValue(),
                            entry.getKey()
                    )
            );
        }

        // -------------------------------------------------
        // DATASET
        // -------------------------------------------------

        PieDataSet dataSet =
                new PieDataSet(
                        entries,
                        "Categories"
                );

        // -------------------------------------------------
        // COLORS
        // -------------------------------------------------

        ArrayList<Integer> colors =
                new ArrayList<>();

        colors.add(
                Color.rgb(
                        110,
                        158,
                        74
                )
        );

        colors.add(
                Color.rgb(
                        229,
                        168,
                        86
                )
        );

        colors.add(
                Color.rgb(
                        218,
                        126,
                        111
                )
        );

        colors.add(
                Color.rgb(
                        126,
                        164,
                        201
                )
        );

        colors.add(
                Color.rgb(
                        151,
                        126,
                        181
                )
        );

        colors.add(
                Color.rgb(
                        102,
                        174,
                        158
                )
        );

        colors.add(
                Color.rgb(
                        194,
                        151,
                        103
                )
        );

        dataSet.setColors(colors);

        // -------------------------------------------------
        // SLICE STYLE
        // -------------------------------------------------

        dataSet.setDrawValues(true);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(10f);

        dataSet.setValueTextColor(
                Color.WHITE
        );

        // -------------------------------------------------
        // PIE DATA
        // -------------------------------------------------

        PieData pieData =
                new PieData(dataSet);

        inventoryCategoryChart.setData(
                pieData
        );

        inventoryCategoryChart.setUsePercentValues(
                true
        );

        // Solid pie
        inventoryCategoryChart.setDrawHoleEnabled(
                false
        );

        inventoryCategoryChart.setEntryLabelColor(
                Color.WHITE
        );

        inventoryCategoryChart.setEntryLabelTextSize(
                10f
        );

        inventoryCategoryChart
                .getDescription()
                .setEnabled(false);

        inventoryCategoryChart
                .getLegend()
                .setEnabled(false);

        inventoryCategoryChart.invalidate();
    }

    // =====================================================
    // WASTE RISK INFO
    // =====================================================

    private void showWasteRiskInfo() {

        View dialogView =
                getLayoutInflater()
                        .inflate(
                                R.layout.dialog_risk_info,
                                null
                        );

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setView(dialogView)
                .setPositiveButton(
                        "Got it",
                        null
                )
                .show();
    }

    // =====================================================
    // CATEGORY INFO
    // =====================================================

    private void showCategoryInfo() {

        List<GroceryItem> filteredItems =
                getFilteredItems(
                        currentItems
                );

        if (filteredItems == null ||
                filteredItems.isEmpty()) {

            new MaterialAlertDialogBuilder(
                    requireContext()
            )
                    .setTitle(
                            "Inventory by Category"
                    )
                    .setMessage(
                            "Add some pantry items to see how your inventory is distributed."
                    )
                    .setPositiveButton(
                            "Got it",
                            null
                    )
                    .show();

            return;
        }

        Map<String, Integer> categoryCounts =
                new HashMap<>();

        for (GroceryItem item :
                filteredItems) {

            String category =
                    item.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

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

        StringBuilder message =
                new StringBuilder();

        int total =
                filteredItems.size();

        message.append(
                "Your pantry is currently distributed across these categories:\n\n"
        );

        for (
                Map.Entry<String, Integer> entry :
                categoryCounts.entrySet()
        ) {

            int percentage =
                    Math.round(
                            (entry.getValue() * 100f)
                                    / total
                    );

            message.append("• ")
                    .append(entry.getKey())
                    .append(" — ")
                    .append(percentage)
                    .append("%")
                    .append("\n");
        }

        message.append(
                "\nThe percentage represents the share of items in each category."
        );

        new MaterialAlertDialogBuilder(
                requireContext()
        )
                .setTitle("Category Guide")
                .setMessage(
                        message.toString()
                )
                .setPositiveButton(
                        "Got it",
                        null
                )
                .show();
    }
}