package com.group12.syde362.checkout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import org.json.*;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleProductFragment extends BluetoothHelper {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "param1";
    private static final String ARG_WEIGHT = "param2";
    private static final String ARG_PRICE = "param3";
    private static final String ARG_QUANTITY = "1";



    // TODO: Rename and change types of parameters
    private String weight;
    private String name;
    private String price;
    private Integer quantity = 1;
    private Double totalPrice;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param result Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleProductFragment newInstance(String result, String param2) {
        SingleProductFragment fragment = new SingleProductFragment();
        //storeNFCData(result);

        try {
            JSONObject obj = new JSONObject(result);
            String tempName = obj.getString("name");
            String tempWeight = obj.getString("weight");
            String tempPrice = obj.getString("price");
            Bundle args = new Bundle();
            args.putString(ARG_NAME, tempName);
            args.putString(ARG_WEIGHT, tempWeight);
            args.putString(ARG_PRICE, tempPrice);
            args.putInt(ARG_QUANTITY, 1);
            fragment.setArguments(args);
        }
        catch (JSONException e) {
        }
        return fragment;

    }

    public SingleProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            weight = getArguments().getString(ARG_WEIGHT);
            price = getArguments().getString(ARG_PRICE);
            quantity = getArguments().getInt(ARG_QUANTITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View singleProductView = inflater.inflate(R.layout.fragment_single_product, container, false);

        TextView singleProductPrice = (TextView) singleProductView.findViewById(R.id.singleProductPrice);
        TextView singleProductWeight = (TextView) singleProductView.findViewById(R.id.singleProductWeight);
        TextView singleProductName = (TextView) singleProductView.findViewById(R.id.singleProductName);
        final TextView updatingQuantity = (TextView) singleProductView.findViewById(R.id.updatingQuantity);
        final TextView totalProductPrice = (TextView) singleProductView.findViewById(R.id.totalProductPrice);


        final Button quantityMinus = (Button) singleProductView.findViewById(R.id.minus);
        final Button quantityPlus = (Button) singleProductView.findViewById(R.id.plus);
        final Animation buttonAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.button_pressed);

        Bundle bundle = getArguments();

        singleProductName.setText(bundle.getString(ARG_NAME));
        singleProductPrice.setText("$" + bundle.getString(ARG_PRICE));
        singleProductWeight.setText("" + bundle.getString(ARG_WEIGHT) + "kg");
        totalProductPrice.setText("$" + bundle.getString(ARG_PRICE));
        ((TextView) singleProductView.findViewById(R.id.updatingQuantity)).setText(String.valueOf(quantity));


        final Button cancelButton = (Button) singleProductView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                //getActivity().getSupportFragmentManager().popBackStack();
                //above line actually removes the single item fragment, which is not what we want.
                // clicking on cancel and then
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ProductFragment itemListFragment = ((MainActivity)getActivity()).getItemListFragment();
                ft.replace(R.id.container, itemListFragment, "List");
                ft.commit();
                itemListFragment.removeSingleFragment(name);
            }
        });

        final Button addButton = (Button) singleProductView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Add Item", Toast.LENGTH_SHORT).show();
                quantity = (Integer.parseInt((String) ((TextView) singleProductView.findViewById(R.id.updatingQuantity)).getText()));
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                /*
                data transfer
                 */
                getWeight();
                addItemToList();
                ft.replace(R.id.container, ((MainActivity)getActivity()).getItemListFragment(), "List");
                ft.commit();
            }
        });

        quantityMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer current = Integer.valueOf(String.valueOf(updatingQuantity.getText()));
                if (current <= 1){
                    quantityMinus.startAnimation(buttonAnim);
                    updatingQuantity.setText(String.valueOf(1));
                }
                else{
                    Integer newQuantity = current - 1;
                    quantityMinus.startAnimation(buttonAnim);
                    updatingQuantity.setText(String.valueOf(newQuantity));
                    totalProductPrice.setText("$" + String.valueOf(String.format("%.2f", calcTotalPrice(newQuantity, price))));
                }

            }
        });

        quantityPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityPlus.startAnimation(buttonAnim);
                Integer current = Integer.valueOf(String.valueOf(updatingQuantity.getText()));
                Integer newQuantity = current + 1;
                updatingQuantity.setText(String.valueOf(newQuantity));
                totalProductPrice.setText("$"+String.valueOf(String.format("%.2f",calcTotalPrice(newQuantity, price))));
            }
        });

        return singleProductView;
    }

    public void getWeight() {
//        if (mConnectedThread == null){
//            Log.e("Error Connected ", "null");
//            return;
//        }
        mConnectedThread = new BluetoothHelper.ConnectedThread(mConnectThread.getSocket());
        mConnectedThread.start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        public void onFragmentInteraction(Uri uri);
    }

    public void addItemToList(){


        ProductListItem item = new ProductListItem(this.name, this.weight, this.price, this.quantity);
        //Fragment itemListFragment = this.getActivity().getSupportFragmentManager().findFragmentById(R.id.);
        System.out.println("new item: " + item);
        System.out.println("new item quantity: " + quantity);

        ((MainActivity)getActivity()).getItemListFragment().getProductList().add(item);
        ((MainActivity)getActivity()).getItemListFragment().updateTotalWeight(item, quantity);
        ((MainActivity)getActivity()).getItemListFragment().updateTotalPrice(item, quantity);

        SingleProductDescrFragment itemDescr = SingleProductDescrFragment.newInstance(this.name, this.price, this.weight, this.quantity);
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, itemDescr);
        ft.addToBackStack(null);
        ft.commit();
    }

    public Double calcTotalPrice(Integer quantity, String price){

        Double unitPrice = Double.valueOf(price);
        Double newTotalPrice = unitPrice * quantity;
        totalPrice = newTotalPrice;
        return newTotalPrice;
    }

    public String getName(){
        return name;
    }

}
