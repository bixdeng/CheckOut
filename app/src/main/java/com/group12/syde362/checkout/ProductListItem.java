package com.group12.syde362.checkout;

import android.view.View;

/**
 * Created by Bix Deng on 2/25/2015.
 */
public class ProductListItem {

    private String itemName;
    private double itemWeight;
    private double itemPrice;
    private int itemQuantity;



    public String getItemTitle(){
        return itemName;
    }

    public void setItemTitle(String mTitle){
        this.itemName = itemName;
    }

    public ProductListItem(String name, String weight, String price, Integer quantity){

        this.itemName = name;
        this.itemWeight = Double.parseDouble(weight);
        this.itemPrice = Double.parseDouble(price);
        this.itemQuantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public double getItemWeight() {
        return itemWeight;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }


}
