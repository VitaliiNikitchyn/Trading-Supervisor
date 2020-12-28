package com.tradingsupervisor.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("category")
    @Expose
    private String category;



    public String getName() { return name; }
    public String getCategory() { return category; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
}
