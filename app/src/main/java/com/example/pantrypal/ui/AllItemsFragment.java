package com.example.pantrypal.ui;

import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.utils.RiskCalculator;
import com.example.pantrypal.viewmodels.GroceryViewModel;

import java.util.*;

import android.text.*;
import android.text.style.*;

public class AllItemsFragment extends Fragment {

    private GroceryViewModel viewModel;

    private TextView totalItemsText, expiringItemsText, riskScoreText;
    private CustomGaugeView customGauge;

    private PieChart categoryChart;
    private BarChart barChartExpiry;
    private LineChart lineChartTrend;

    private Map<String, Integer> categoryLegendMap = new HashMap<>();

    public AllItemsFragment() {
        super(R.layout.fragment_all_items);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        totalItemsText = view.findViewById(R.id.text_total_items);
        expiringItemsText = view.findViewById(R.id.text_expiring_items);
        riskScoreText = view.findViewById(R.id.text_risk_score);

        customGauge = view.findViewById(R.id.customGauge);

        categoryChart = view.findViewById(R.id.categoryChart);
        barChartExpiry = view.findViewById(R.id.barChartExpiry);
        lineChartTrend = view.findViewById(R.id.lineChartTrend);

        // INFO BUTTONS
        ImageView wasteInfoBtn = view.findViewById(R.id.btnWasteInfo);
        ImageView categoryInfoBtn = view.findViewById(R.id.btnCategoryInfo);

        if (wasteInfoBtn != null) {
            wasteInfoBtn.setOnClickListener(v -> {
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String main = "Risk = 0.6E + 0.3S + 0.1U\n\n";
                SpannableString mainSpan = new SpannableString(main);
                mainSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, main.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.append(mainSpan);
                builder.append("E : Expiry (daysLeft)\n");
                builder.append("S : (qty / (daysLeft + 1)) × 20\n");
                builder.append("U : Urgency");

                new AlertDialog.Builder(requireContext())
                        .setTitle("Waste Risk")
                        .setMessage(builder)
                        .setPositiveButton("OK", null)
                        .show();
            });
        }

        if (categoryInfoBtn != null) {
            categoryInfoBtn.setOnClickListener(v -> showCategoryInfo());
        }

        // QUICK ADD
        TextView addBtn = view.findViewById(R.id.btn_add_item);
        if (addBtn != null) {
            addBtn.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), AddGroceryActivity.class))
            );
        }

        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);
        viewModel.getAllItems().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<GroceryItem> list) {

        if (list == null) return;

        int total = list.size();
        int expiring = 0;
        int totalRisk = 0;

        List<BarEntry> barEntries = new ArrayList<>();
        List<Entry> lineEntries = new ArrayList<>();

        int index = 0;

        for (GroceryItem item : list) {

            int risk = RiskCalculator.calculateRisk(item.getExpiryDate(), item.getQuantity());
            totalRisk += risk;

            if (risk >= 50) expiring++;

            barEntries.add(new BarEntry(index, risk));
            lineEntries.add(new Entry(index, item.getQuantity()));

            index++;
        }

        int avg = total == 0 ? 0 : totalRisk / total;

        // TEXT
        totalItemsText.setText(total + " Items");
        expiringItemsText.setText(expiring + " At Risk");
        riskScoreText.setText(String.valueOf(avg));

        if (customGauge != null) customGauge.setValue(avg);

        // ================= PIE CHART =================
        Map<String, Integer> categoryMap = new HashMap<>();

        for (GroceryItem item : list) {
            String category = item.getCategory();
            if (category == null || category.trim().isEmpty()) category = "Other";
            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + 1);
        }

        categoryLegendMap = categoryMap;

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieSet = new PieDataSet(pieEntries, "");
        pieSet.setColors(
                android.graphics.Color.parseColor("#E8D18A"),
                android.graphics.Color.parseColor("#D6B25E"),
                android.graphics.Color.parseColor("#F1E2A0"),
                android.graphics.Color.parseColor("#C2A24F")
        );
        pieSet.setDrawValues(false);

        PieData pieData = new PieData(pieSet);
        categoryChart.setData(pieData);

        categoryChart.getLegend().setEnabled(false);
        categoryChart.getDescription().setEnabled(false);
        categoryChart.invalidate();

        // ================= BAR CHART =================
        BarDataSet barSet = new BarDataSet(barEntries, "");
        barSet.setColor(android.graphics.Color.parseColor("#D6B25E"));
        barSet.setDrawValues(false);

        BarData barData = new BarData(barSet);
        barChartExpiry.setData(barData);

        barChartExpiry.getDescription().setEnabled(false);
        barChartExpiry.getLegend().setEnabled(false);
        barChartExpiry.getAxisRight().setEnabled(false);

        barChartExpiry.invalidate();

        // ================= LINE CHART =================
        LineDataSet lineSet = new LineDataSet(lineEntries, "");
        lineSet.setColor(android.graphics.Color.parseColor("#8E6B2F"));
        lineSet.setCircleColor(android.graphics.Color.parseColor("#D6B25E"));
        lineSet.setDrawValues(false);

        LineData lineData = new LineData(lineSet);
        lineChartTrend.setData(lineData);

        lineChartTrend.getDescription().setEnabled(false);
        lineChartTrend.getLegend().setEnabled(false);
        lineChartTrend.getAxisRight().setEnabled(false);

        lineChartTrend.invalidate();
    }

    private void showCategoryInfo() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (Map.Entry<String, Integer> entry : categoryLegendMap.entrySet()) {
            builder.append("• ")
                    .append(entry.getKey())
                    .append(" → ")
                    .append(String.valueOf(entry.getValue()))
                    .append("\n");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Category Breakdown")
                .setMessage(builder)
                .setPositiveButton("Got it", null)
                .show();
    }
}