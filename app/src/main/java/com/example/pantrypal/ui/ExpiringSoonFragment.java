package com.example.pantrypal.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrypal.R;
import com.example.pantrypal.adapters.GroceryAdapter;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.viewmodels.GroceryViewModel;

import java.util.List;

public class ExpiringSoonFragment extends Fragment {

    private GroceryAdapter adapter;
    private GroceryViewModel viewModel;
    private RecyclerView recyclerView;
    private ImageView emptyStateImage;
    private TextView emptyStateText;

    public ExpiringSoonFragment() {
        super(R.layout.fragment_expiring_soon);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_expiring);
        emptyStateImage = view.findViewById(R.id.image_empty_expiring);
        emptyStateText = view.findViewById(R.id.text_empty_expiring);

        adapter = new GroceryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        viewModel.getExpiringItems().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<GroceryItem> items) {
        adapter.setGroceryList(items);

        boolean isEmpty = items == null || items.isEmpty();
        emptyStateImage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
