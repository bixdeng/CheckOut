package com.group12.syde362.checkout;

/**
 * Created by Bix Deng on 2/25/2015.
 */
public class ProductListItem {

    private String itemTitle;

    public String getItemTitle(){
        return itemTitle;
    }

    public void setItemTitle(String mTitle){
        this.itemTitle = itemTitle;
    }

    public ProductListItem(String title){
        this.itemTitle = title;
    }
}
