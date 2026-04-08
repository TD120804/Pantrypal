package com.example.pantrypal.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grocery_table")
public class GroceryItem implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "expiry_date")
    private String expiryDate;

    @ColumnInfo(name = "notes")
    private String notes;

    @NonNull
    @ColumnInfo(name = "barcode")
    private String barcode;

    public GroceryItem(@NonNull String name, int quantity, String category,
                       String expiryDate, String notes, @NonNull String barcode) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.barcode = barcode;
    }

    protected GroceryItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        quantity = in.readInt();
        category = in.readString();
        expiryDate = in.readString();
        notes = in.readString();
        barcode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(category);
        dest.writeString(expiryDate);
        dest.writeString(notes);
        dest.writeString(barcode);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<GroceryItem> CREATOR = new Creator<GroceryItem>() {
        @Override public GroceryItem createFromParcel(Parcel in) { return new GroceryItem(in); }
        @Override public GroceryItem[] newArray(int size) { return new GroceryItem[size]; }
    };

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    @NonNull public String getBarcode() { return barcode; }
    public void setBarcode(@NonNull String barcode) { this.barcode = barcode; }
}
