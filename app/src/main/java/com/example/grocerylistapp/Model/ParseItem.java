package com.example.grocerylistapp.Model;

public class ParseItem {
    private String imgUrl;
    private String title;
    private String prepTime;
    private String cookTime;
    private String serves;
    private String difficulty;
    private String ingredients;

    public ParseItem(){}

    public ParseItem(String imgUrl, String title, String prepTime, String cookTime, String serves, String difficulty, String ingredients) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.serves = serves;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getServes() {
        return serves;
    }

    public void setServes(String serves) {
        this.serves = serves;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
