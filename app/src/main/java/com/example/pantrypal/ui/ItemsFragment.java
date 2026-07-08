package com.example.pantrypal.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.example.pantrypal.R;
import com.example.pantrypal.adapters.GroceryAdapter;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;
import com.example.pantrypal.utils.RecommendationEngine;
import com.example.pantrypal.utils.RiskCalculator;

public class ItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroceryAdapter adapter;
    private GroceryViewModel viewModel;
    private TextView healthScore;
    private TextView healthStatus;
    private TextView recommendationText;

    public ItemsFragment() {
        super(R.layout.fragment_items);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new GroceryAdapter();
        recyclerView.setAdapter(adapter);

        healthScore = view.findViewById(R.id.text_health_score);
        healthStatus = view.findViewById(R.id.text_health_status);
        recommendationText = view.findViewById(R.id.text_recommendation);

        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        // ✅ LOAD DATA
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {

            if (items != null) {

                adapter.setGroceryList(items);

                int score = 100;

                for (GroceryItem item : items) {

                    int daysLeft =
                            RiskCalculator.getDaysLeft(item.getExpiryDate());

                    if(daysLeft < 0){

                        score -= 15;

                    }else if(daysLeft <=3){

                        score -=5;

                    }

                }

                score = Math.max(0, score);

                healthScore.setText(score + "/100");

                if (score >= 80) {
                    healthStatus.setText("🟢 Excellent Pantry");
                } else if (score >= 60) {
                    healthStatus.setText("🟡 Good Pantry");
                } else if (score >= 40) {
                    healthStatus.setText("🟠 Needs Attention");
                } else {
                    healthStatus.setText("🔴 Poor Pantry");
                }
                recommendationText.setText(
                        RecommendationEngine.getRecommendation(items)
                );
            }
        });

        // 🔥 EDIT ON CLICK (THIS WAS MISSING)
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireContext(), AddGroceryActivity.class);
            intent.putExtra("EDIT_ITEM_ID", item.getId());
            startActivity(intent);
        });

        // 🔥 SWIPE DELETE (WITH CONFIRMATION)
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        int position = viewHolder.getAdapterPosition();
                        GroceryItem item = adapter.getGroceryAt(position);

                        new AlertDialog.Builder(requireContext())
                                .setTitle("Delete Item")
                                .setMessage("Are you sure you want to delete this item?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    viewModel.delete(item);
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    adapter.notifyItemChanged(position);
                                })
                                .show();
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}