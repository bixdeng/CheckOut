package com.group12.syde362.checkout;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import org.json.*;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;

/**
 * Created by terencekim on 15-03-18.
 */
public class SingleProductDescrFragment extends Fragment{
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
    private Integer listPosition = 0;

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
    public static SingleProductDescrFragment newInstance(String name, String price, String weight, Integer quantity) {
        SingleProductDescrFragment fragment = new SingleProductDescrFragment();
        //storeNFCData(result);
        fragment.name = name;
        fragment.price = price;
        fragment.weight = weight;
        fragment.quantity = quantity;
        return fragment;

    }

    public SingleProductDescrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View singleProductDescrView = inflater.inflate(R.layout.fragment_single_product_descr, container, false);

        TextView singleProductPrice = (TextView) singleProductDescrView.findViewById(R.id.singleProductPriceDescr);
        TextView singleProductWeight = (TextView) singleProductDescrView.findViewById(R.id.singleProductWeightDescr);
        TextView singleProductName = (TextView) singleProductDescrView.findViewById(R.id.singleProductNameDescr);
        final TextView updatingQuantity = (TextView) singleProductDescrView.findViewById(R.id.updatingQuantityDescr);

        Button quantityMinus = (Button) singleProductDescrView.findViewById(R.id.minusDescr);
        Button quantityPlus = (Button) singleProductDescrView.findViewById(R.id.plusDescr);

        singleProductName.setText(name);
        singleProductPrice.setText("$" + String.valueOf(price));
        singleProductWeight.setText(String.valueOf(weight) + "kg");
        ((TextView) singleProductDescrView.findViewById(R.id.updatingQuantityDescr)).setText(String.valueOf(quantity));


        Button cancelButton = (Button) singleProductDescrView.findViewById(R.id.cancelButtonDescr);
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                //getActivity().getSupportFragmentManager().popBackStack();
                //above line actually removes the single item fragment, which is not what we want.
                // clicking on cancel and then
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ProductFragment itemListFragment = ((MainActivity)getActivity()).getItemListFragment();
                ft.replace(R.id.container, itemListFragment);
                ft.commit();
            }
        });

        Button updateButton = (Button) singleProductDescrView.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Add Item", Toast.LENGTH_SHORT).show();

                ProductFragment itemListFragment = ((MainActivity)getActivity()).getItemListFragment();
                itemListFragment.subtractFromTotalWeight(quantity, weight); //subtracting old weight before adding new weight
                itemListFragment.subtractFromTotalPrice(quantity, price); //subtracting old price before adding new price

                Integer newQuantity = (Integer.parseInt((String) ((TextView) singleProductDescrView.findViewById(R.id.updatingQuantityDescr)).getText()));
                quantity = newQuantity;
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                updateItemInList();
                ft.replace(R.id.container, ((MainActivity)getActivity()).getItemListFragment());
                ft.commit();


            }
        });

        quantityMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer current = Integer.valueOf(String.valueOf(updatingQuantity.getText()));
                Integer newQuantity = current - 1;
                updatingQuantity.setText(String.valueOf(newQuantity));
            }
        });

        quantityPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer current = Integer.valueOf(String.valueOf(updatingQuantity.getText()));
                Integer newQuantity = current + 1;
                updatingQuantity.setText(String.valueOf(newQuantity));
            }
        });

        return singleProductDescrView;
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

    public void updateItemInList(){
        ProductListItem updatedListItem = new ProductListItem(name, weight, price, quantity);
        ((MainActivity)getActivity()).getItemListFragment().updateTotalPrice(updatedListItem, quantity);
        ((MainActivity)getActivity()).getItemListFragment().updateTotalWeight(updatedListItem, quantity);
        ((MainActivity)getActivity()).getItemListFragment().getProductList().set(listPosition, updatedListItem);
    }

    public String getName(){
        return name;
    }
    public Integer getListPosition() {
        return listPosition;
    }
    public void setListPosition(Integer position){
        listPosition = position;
    }

}


