package com.example.grocerylistapp;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private final TextView mProductName;
    private final TextView mProductNote;
    private final TextView mAmount;

    public MyViewHolder(View itemView) {
        super(itemView);
        mProductName = itemView.findViewById(R.id.product_name);
        mProductNote = itemView.findViewById(R.id.product_note);
        mAmount = itemView.findViewById(R.id.product_amount);
    }

    public void setProductName(String name){
        mProductName.setText(name);
    }

    public void setProductNote(String note){
        mProductNote.setText(note);
    }

    public void setAmount(String amount){
        String amountValue = "x" + amount;
        mAmount.setText(amountValue);
    }
}