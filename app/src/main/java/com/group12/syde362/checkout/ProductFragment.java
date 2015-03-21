package com.group12.syde362.checkout;

import android.app.Activity;
//import android.app.FragmentManager;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;


import com.group12.syde362.checkout.dummy.DummyContent;
import android.widget.Button;
import android.net.Uri;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.app.ProgressDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import static android.view.GestureDetector.*;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ProductFragment extends Fragment implements AbsListView.OnItemClickListener, View.OnDragListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private double totalWeight = 0.0;
    private double totalPrice = 0.0;
    private double errorRoom = 1;

    private List productList = new ArrayList();
    private OnFragmentInteractionListener mListener;
    SwipeDetector swipeDetector = new SwipeDetector();

    TextView totalPriceLabel;
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //productList.add(new ProductListItem("Apple"));
        //productList.add(new ProductListItem("Bananas"));
        //productList.add(new ProductListItem("Coconut"));
        //productList.add(new ProductListItem("Dragonfruit"));
        mAdapter = new ProductListAdapter(getActivity(), productList);
        //addListenerOnRemove();

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);*/

    }

//    private void addListenerOnRemove() {
//        //ImageButton removeButton = (ImageButton) getView().findViewById(R.id.removeButton);
//        View.OnClickListener listenerDel = new View.OnClickListener() {
//        };
//
//        ImageButton removeButton = (ImageButton) getView().findViewById(R.id.removeButton);
//        removeButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                int pos = (int) arg0.getTag();
//                productList.remove(pos);
//            }
//        });
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        Button button = (Button) view.findViewById(R.id.getBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                getWeight(v);
                Log.d("Calculated Weight: ", totalWeight + "kg");
            }
        });

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        totalPriceLabel = (TextView) view.findViewById(R.id.totalPrice);
        totalPriceLabel.setText("$ 0");
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnTouchListener(swipeDetector);
        updateTotalPriceLabel();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        final int removingPosition = position;
        final Animation leftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_left);
        final Animation rightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_right);
        if(swipeDetector.swipeDetected()) {
            if (swipeDetector.getAction().name() == "RL") {
                leftAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        productList.remove(removingPosition);
                        ((ProductListAdapter) mAdapter).notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(leftAnimation);
            }
            if (swipeDetector.getAction().name() == "LR") {
                rightAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        productList.remove(removingPosition);
                        ((ProductListAdapter) mAdapter).notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(rightAnimation);

            }
        }



        /*if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }*/
        else { //if it is only touch action
            ProductListItem item = (ProductListItem) this.productList.get(position);

            //SingleProductFragment singleProductFrag = new SingleProductFragment();
            android.support.v4.app.FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            SingleProductDescrFragment matchingItem = findMatchingSingleProductDescrFragment(fm.getFragments(), item.getItemTitle(), position);
            ft.replace(R.id.container, matchingItem).addToBackStack("tag");
            ft.commit();
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }



    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    /*
    *   Connecting to localhost
    */

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, Double>> productsList;

    // url to get all products list
    private static String url_all_products = "http://192.168.43.196/android_connect/get_all_products.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_WEIGHT = "weight";

    // products JSONArray
    JSONArray products = null;

    //get weight from localhost
    public void getWeight(View v){
        new LoadAllProducts().execute();
        productsList = new ArrayList<HashMap<String, Double>>();
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
/*        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }*/

        /**
         * getting All products from url
         * */
         protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    JSONObject lastReading = products.getJSONObject(products.length() - 1);
                    //for (int i = 0; i < products.length(); i++) {
                        //JSONObject c = products.getJSONObject(i);


                        // Storing each json item in variable
                        double id = lastReading.getDouble(TAG_PID);
                        final double weight = lastReading.getDouble(TAG_WEIGHT);
                        //double id = c.getDouble(TAG_PID);
                        //final double weight = c.getDouble(TAG_WEIGHT);

/*                        TextView getText = (TextView) getView().findViewById(R.id.getTxt);
                        getText.setText(String.valueOf(weight));*/
                        Log.d("WEIGHT: ", "measured: " + String.valueOf(weight) + "calculated: " + totalWeight);
                        System.out.println("measured: " + String.valueOf(weight) + "calculated: " + totalWeight);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView verifyLabel = (TextView) getView().findViewById(R.id.weightVerify);
                                if (weight <= totalWeight + errorRoom && weight >= totalWeight - errorRoom){
                                    verifyLabel.setText("Correct Weight!");
                                    verifyLabel.setTextColor(Color.parseColor("#008000"));
                                }
                                else{
                                    verifyLabel.setText("Wrong Weight!");
                                    verifyLabel.setTextColor(Color.parseColor("#B20000"));
                                }
                            }
                        });

                        // creating new HashMap
                        HashMap<String, Double> map = new HashMap<String, Double>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_WEIGHT, weight);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
               // }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

/*        *//**
         * After completing background task Dismiss the progress dialog
         * **//*
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }*/

    }


    // Method for sending HTTP GET request to a web page
    public OutputStream HttpRequestGet(Uri uri) throws IOException
    {

        HttpGet httpget = new HttpGet(uri.toString());

        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse response = httpclient.execute(httpget);

        HttpEntity httpentity = response.getEntity();

        if (null == httpentity)
        {
            throw(new IOException("HttpEntity is null"));
        }

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        httpentity.writeTo(outstream);

        return outstream;
    }

    public List getProductList(){
        return productList;
    }

    public SingleProductDescrFragment findMatchingSingleProductDescrFragment(List<Fragment> loFragments, String itemName, Integer position){
        SingleProductDescrFragment matchedItemInfo;
        for (int i = 0; i < loFragments.size(); i++) {
            if(loFragments.get(i) instanceof SingleProductDescrFragment){
                matchedItemInfo = (SingleProductDescrFragment) loFragments.get(i);
                if (matchedItemInfo.getName().equals(itemName)){
                    matchedItemInfo.setListPosition(position);
                    return matchedItemInfo;
                }
            }
        }

        return null;
    }

    public void updateTotalWeight(ProductListItem newItem, Integer quantity){
        totalWeight = totalWeight + (quantity * newItem.getItemWeight());
        Log.d("total weight is: ", ""+totalWeight);
        return;

    }

    public void updateTotalPrice(ProductListItem newItem, Integer quantity){
        totalPrice = totalPrice + (quantity * newItem.getItemPrice());
        String totalPriceRounded = String.format("%.2f", totalPrice);
        //((MainActivity) getActivity()).getItemListFragment().totalPriceLabel.setText(("$ " + totalPriceRounded));
        //((TextView) getView().findViewById(R.id.totalPrice)).setText(String.valueOf(totalPrice));
        String label;
        label = (String) this.totalPriceLabel.getText();
        Log.d("total price is: ", ""+totalPrice);
    }

    private void updateTotalPriceLabel() {
        String totalPriceRounded = String.format("%.2f", totalPrice);
        totalPriceLabel.setText(("$ " + totalPriceRounded));
        Log.d("total price rounded: ", totalPriceRounded);
    }

    public void subtractFromTotalWeight(Integer quantity, String weight) {
        totalWeight = totalWeight - (quantity * Double.valueOf(weight));
    }

    public void subtractFromTotalPrice(Integer quantity, String price) {
        totalPrice = totalPrice - (quantity * Double.valueOf(price));
        updateTotalPriceLabel();
    }


    public double getTotalWeight(){
        return totalWeight;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

}
