package com.group12.syde362.checkout;

/**
 * Created by Bix Deng on 2/25/2015.
 */
public class ProductListItem {

    private String itemName;
    private String itemWeight;
    private String itemPrice;


    public String getItemTitle(){
        return itemName;
    }

    public void setItemTitle(String mTitle){
        this.itemName = itemName;
    }

    public ProductListItem(String name, String weight, String price){

        this.itemName = name;
        this.itemWeight = weight;
        this.itemPrice = price;
    }
}
