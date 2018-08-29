package com.tradingsupervisor.database.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assortment {
    @SerializedName("name")
    @Expose
    private String name;

    public Assortment(){

    }

    public Assortment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
