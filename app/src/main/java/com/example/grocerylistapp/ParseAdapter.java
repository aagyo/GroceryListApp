package com.example.grocerylistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerylistapp.Model.ParseItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {

    private final ArrayList<ParseItem> parseItems;

    public ParseAdapter(ArrayList<ParseItem> parseItems, Context context) {
        this.parseItems = parseItems;
    }

    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {
        ParseItem parseItem = parseItems.get(position);
        Picasso.get().load(parseItem.getImgUrl()).into(holder.recipeImage);
        holder.title.setText(parseItem.getTitle());
        holder.prepTime.setText(parseItem.getPrepTime());
        holder.cookTime.setText(parseItem.getCookTime());
        holder.serves.setText(parseItem.getServes());
        holder.difficulty.setText(parseItem.getDifficulty());
        holder.ingredients.setText(parseItem.getIngredients());

        holder.itemView.findViewById(R.id.ingredients_dropdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton expandImageButton = holder.itemView.findViewById(R.id.ingredients_dropdown);
                TextView container = holder.itemView.findViewById(R.id.ingredients_text);
                expandImageButton.setRotation(container.getVisibility() == View.GONE ? 180 : 0);
                container.setVisibility(container.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parseItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView recipeImage;
        TextView title;
        TextView prepTime;
        TextView cookTime;
        TextView serves;
        TextView difficulty;
        TextView ingredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            title = itemView.findViewById(R.id.recipe_title);
            prepTime = itemView.findViewById(R.id.prep_time);
            cookTime = itemView.findViewById(R.id.cook_time);
            serves = itemView.findViewById(R.id.serves_number);
            difficulty = itemView.findViewById(R.id.difficulty);
            ingredients = itemView.findViewById(R.id.ingredients_text);
        }
    }
}
