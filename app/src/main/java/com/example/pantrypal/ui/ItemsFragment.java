package com.example.pantrypal.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.example.pantrypal.R;
import com.example.pantrypal.adapters.GroceryAdapter;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;

public class ItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroceryAdapter adapter;
    private GroceryViewModel viewModel;

    public ItemsFragment() {
        super(R.layout.fragment_items);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new GroceryAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        // ✅ LOAD DATA
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setGroceryList(items);
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