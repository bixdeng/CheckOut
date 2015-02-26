package com.group12.syde362.checkout;

/**
 * Created by Bix Deng on 2/25/2015.
 */
public class ProductListItem {

    private String itemTitle;
    private String itemPrice;
    private String itemQuantity;
    private String itemWeight;

    public String getItemTitle(){
        return itemTitle;
    }

    public String getItemPrice(){
        return itemPrice;
    }

    public String getItemQuantity(){
        return itemQuantity;
    }

    public String getItemWeight(){
        return itemWeight;
    }

    public void setItemTitle(String itemTitle){
        this.itemTitle = itemTitle;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public void setItemWeight(String itemWeight) {
        this.itemQuantity = itemWeight;
    }

    public ProductListItem(String title){
        this.itemTitle = title;
    }

    public ProductListItem(String title, String weight, String quantity, String price){
        this.itemTitle = title;
        this.itemPrice = price;
        this.itemQuantity = quantity;
        this.itemWeight = weight;
    }
}
