package com.example.pantrypal.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrypal.R;
import com.example.pantrypal.models.GroceryItem;
import com.example.pantrypal.utils.RiskCalculator;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryHolder> {

    private List<GroceryItem> groceryList = new ArrayList<>();
    private OnItemClickListener listener;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @NonNull
    @Override
    public GroceryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grocery, parent, false);
        return new GroceryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryHolder holder, int position) {

        GroceryItem item = groceryList.get(position);

        holder.textName.setText(item.getName());
        holder.textQty.setText("Qty: " + item.getQuantity());
        holder.textCategory.setText(item.getCategory());
        holder.textExpiry.setText(item.getExpiryDate());

        int risk = RiskCalculator.calculateRisk(
                item.getExpiryDate(),
                item.getQuantity()
        );

        long daysLeft = 999;

        // ================= DAYS LEFT =================
        try {
            LocalDate expiry = LocalDate.parse(item.getExpiryDate(), formatter);
            LocalDate today = LocalDate.now();

            daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, expiry);

            if (daysLeft < 0) {
                holder.textDaysLeft.setText("Expired " + Math.abs(daysLeft) + " days ago");
            } else if (daysLeft == 0) {
                holder.textDaysLeft.setText("Expires today");
            } else {
                holder.textDaysLeft.setText(daysLeft + " days left");
            }

        } catch (Exception e) {
            holder.textDaysLeft.setText("");
        }

        // ================= FIXED RISK + BADGE LOGIC =================
        if (daysLeft < 0) {
            holder.textRisk.setText("Risk: HIGH");
            holder.textBadge.setText("⛔ EXPIRED");
            holder.textBadge.setVisibility(View.VISIBLE);
        }
        else if (daysLeft == 0) {
            holder.textRisk.setText("Risk: HIGH");
            holder.textBadge.setText("⚠ EXPIRES TODAY");
            holder.textBadge.setVisibility(View.VISIBLE);
        }
        else if (daysLeft <= 2) {
            holder.textRisk.setText("Risk: MEDIUM");
            holder.textBadge.setText("⚠ EXPIRING SOON");
            holder.textBadge.setVisibility(View.VISIBLE);
        }
        else {
            // fallback to calculated risk
            if (risk >= 80) {
                holder.textRisk.setText("Risk: HIGH");
            } else if (risk >= 50) {
                holder.textRisk.setText("Risk: MEDIUM");
            } else {
                holder.textRisk.setText("Risk: LOW");
            }

            holder.textBadge.setVisibility(View.GONE);
        }

        // ================= COLOR =================
        applyExpiryColor(holder.cardRoot, daysLeft);

        Log.d("ExpiryCheck",
                "Item: " + item.getName() + " DaysLeft: " + daysLeft);
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public void setGroceryList(List<GroceryItem> list) {
        this.groceryList = list;
        notifyDataSetChanged();
    }

    public GroceryItem getGroceryAt(int position) {
        return groceryList.get(position);
    }

    class GroceryHolder extends RecyclerView.ViewHolder {

        TextView textName, textQty, textExpiry, textCategory, textRisk, textBadge, textDaysLeft;
        MaterialCardView cardRoot;

        public GroceryHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_view_name);
            textQty = itemView.findViewById(R.id.text_view_quantity);
            textCategory = itemView.findViewById(R.id.text_view_category);
            textExpiry = itemView.findViewById(R.id.text_view_expiry);
            textRisk = itemView.findViewById(R.id.text_view_risk);
            textBadge = itemView.findViewById(R.id.text_expiring_badge);
            textDaysLeft = itemView.findViewById(R.id.text_days_left);

            cardRoot = itemView.findViewById(R.id.card_root_container);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION)
                    listener.onItemClick(groceryList.get(pos));
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GroceryItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ================= FINAL COLOR LOGIC =================
    private void applyExpiryColor(MaterialCardView card, long daysLeft) {

        if (daysLeft < 0 || daysLeft == 0) {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(card.getContext(), R.color.expiredColor)
            );
        }
        else if (daysLeft <= 2) {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(card.getContext(), R.color.expiringColor)
            );
        }
        else {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(card.getContext(), R.color.normalColor)
            );
        }
    }
}