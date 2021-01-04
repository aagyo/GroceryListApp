package com.example.grocerylistapp.Model;

public class  Product {
    String name;
    int amount;
    String note;
    String id;

    public Product(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product(String name, int amount, String note, String id) {
        this.name = name;
        this.amount = amount;
        this.note = note;
        this.id = id;

    }
}