package com.group12.syde362.checkout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import org.json.*;
import android.util.Log;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "param1";
    private static final String ARG_WEIGHT = "param2";
    private static final String ARG_PRICE = "param3";



    // TODO: Rename and change types of parameters
    private String weight;
    private String name;
    private String price;

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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View singleProductView = inflater.inflate(R.layout.fragment_single_product, container, false);
        TextView singleProductPrice = (TextView) singleProductView.findViewById(R.id.singleProductPrice);
        TextView singleProductWeight = (TextView) singleProductView.findViewById(R.id.singleProductWeight);
        TextView singleProductName = (TextView) singleProductView.findViewById(R.id.singleProductName);

        Bundle bundle = getArguments();

        singleProductName.setText("Name: " + bundle.getString(ARG_NAME));
        singleProductPrice.setText("Price: " + bundle.getString(ARG_PRICE));
        singleProductWeight.setText("Weight: " + bundle.getString(ARG_WEIGHT));

        return singleProductView;
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


}
