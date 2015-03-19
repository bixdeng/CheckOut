package com.group12.syde362.checkout;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.Activity;
import java.util.List;

/**
 * Created by Bix Deng on 2/25/2015.
 */
public class ProductListAdapter extends ArrayAdapter{
    private Context context;
    private boolean useList = true;

    public ProductListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
        TextView quantityText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ProductListItem item = (ProductListItem)getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if(useList){
                viewToUse = mInflater.inflate(R.layout.product_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.product_grid_item, null);
            }

            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            holder.quantityText = (TextView)viewToUse.findViewById(R.id.listItemQuantity);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.getItemTitle());
        holder.quantityText.setText(String.valueOf(item.getItemQuantity()));

        return viewToUse;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
