package com.group12.syde362.checkout;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;
import java.util.logging.LogRecord;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BluetoothHelper {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Called when the activity is first created.
     */
//  COMMENTED OUT (BluetoothHelper)
//    ListView listViewPaired;
//    ListView listViewDetected;
//    ArrayList<String> arrayListpaired;
//    Button buttonSearch, buttonOn, buttonOff, buttonWeight;
//    ArrayAdapter<String> adapter, detectedAdapter;
//    //    static HandleSearch handleSeacrh;
//    BluetoothDevice bdDevice;
//    BluetoothClass bdClass;
//    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    private ButtonClicked clicked;
    ListItemClickedOnPaired listItemClickedOnPaired;
//    BluetoothAdapter bluetoothAdapter = null;
//    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
    ListItemClicked listItemClicked;
    /*
    https://github.com/aron-bordin/Android-with-Arduino-Bluetooth/blob/master/Android/Example/BluetoothArduino.java
     */
//    OutputStream mOut;
//    InputStream mIn;
//    private String TAG = "BluetoothConnector";
//    private BluetoothSocket mBlueSocket = null;
//    private boolean connected = false;
//    BluetoothDevice mDevice;
//    ConnectThread mConnectThread;
//    ConnectedThread mConnectedThread;
//
    private OnFragmentInteractionListener mListener;
//
//    // Message types used by the Handler
//    public static final int MESSAGE_WRITE = 1;
//    public static final int MESSAGE_READ = 2;
//    String readMessage = "";
//    SendReceiveBytes sendReceiveBT = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }


    public void onCreate(LayoutInflater inflater, Bundle savedInstanceState, ViewGroup container) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        // take an instance of BluetoothAdapter - Bluetooth radio
        listViewDetected = (ListView) view.findViewById(R.id.listViewDetected);
        listViewPaired = (ListView) view.findViewById(R.id.listViewPaired);
        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        buttonOn = (Button) view.findViewById(R.id.buttonOn);
        buttonOff = (Button) view.findViewById(R.id.buttonOff);
        buttonWeight = (Button) view.findViewById(R.id.buttonWeight);
        arrayListpaired = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        clicked = new ButtonClicked();
//        handleSeacrh = new HandleSearch();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        /*
         * the above declaration is just for getting the paired bluetooth devices;
         * this helps in the removing the bond between paired devices.
         */
        listItemClickedOnPaired = new ListItemClickedOnPaired();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayListpaired);
        detectedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice);
        listViewDetected.setAdapter(detectedAdapter);
        listItemClicked = new ListItemClicked();
        detectedAdapter.notifyDataSetChanged();
        listViewPaired.setAdapter(adapter);
        // Inflate the layout for this fragment
        return view;
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
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        getPairedDevices();
        buttonOn.setOnClickListener(clicked);
        buttonSearch.setOnClickListener(clicked);
        buttonOff.setOnClickListener(clicked);
        buttonWeight.setOnClickListener(clicked);
        listViewDetected.setOnItemClickListener(listItemClicked);
        listViewPaired.setOnItemClickListener(listItemClickedOnPaired);
        if (mDevice != null) {
            Log.i("mDevice onStart", "" + mDevice.getName());
        }
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                arrayListpaired.add(device.getName() + "\n" + device.getAddress());
                arrayListPairedBluetoothDevices.add(device);
                mDevice = device;
            }
        }
        adapter.notifyDataSetChanged();
    }


//    /*
//    Connecting with Arduino via Bluetooth
//     */
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//        final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//
//        public ConnectThread(BluetoothDevice device) {
//            Log.i("ConnectThread", "Started");
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//            try {
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) {
//            }
//            mmSocket = tmp;
//        }
//
//        public BluetoothSocket getSocket(){
//            return mmSocket;
//        }
//
//        public void run() {
//            Log.i("ConnectThread", "Ran");
//            bluetoothAdapter.cancelDiscovery();
//            try {
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) {
//                }
//                return;
//            }
////            mConnectedThread = new ConnectedThread(mmSocket);
////            mConnectedThread.start();
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
//        }
//    }

//    /*
//    http://stackoverflow.com/questions/23540754/send-data-from-arduino-to-android-app-via-bluetooth
//     */
//    // The Handler that gets information back from the Socket
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_WRITE:
//                    //Do something when writing
//                    break;
//                case MESSAGE_READ:
//                    //Get the bytes from the msg.obj
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    readMessage = new String(readBuf, 0, msg.arg1);
//                    Log.i("Weight: ", ""+readMessage);
//                    break;
//            }
//        }
//    };

    /* DATA TRANSFER
    Writing and Reading to/from Arduino
     */
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            Log.i("ConnectedThread", "Started");
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//            }
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            sendReceiveBT = new SendReceiveBytes(mmSocket);
//            new Thread(sendReceiveBT).start();
//            String red = "r";
//            byte[] myByte = stringToBytesUTFCustom(red);
//            sendReceiveBT.write(myByte);
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
//        }
//    }


//    public static byte[] stringToBytesUTFCustom(String str) {
//        char[] buffer = str.toCharArray();
//        byte[] b = new byte[buffer.length << 1];
//        for (int i = 0; i < buffer.length; i++) {
//            int bpos = i << 1;
//            b[bpos] = (byte) ((buffer[i] & 0xFF00) >> 8);
//            b[bpos + 1] = (byte) (buffer[i] & 0x00FF);
//        }
//        return b;
//    }

    //        private BluetoothSocket btSocket;
//        private InputStream btInputStream = null;
//        ;
//        private OutputStream btOutputStream = null;
//        String TAG = "SendReceiveBytes";
//
//        public SendReceiveBytes(BluetoothSocket socket) {
//            btSocket = socket;
//            try {
//                btInputStream = btSocket.getInputStream();
//                btOutputStream = btSocket.getOutputStream();
//            } catch (IOException streamError) {
//                Log.e(TAG, "Error when getting input or output Stream");
//            }
//        }
//
//        public void run() {
//    private class SendReceiveBytes implements Runnable {
//            byte[] buffer = new byte[1024]; // buffer store for the stream
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = btInputStream.read(buffer);
//                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    Log.e(TAG, "Error reading from btInputStream");
//                    break;
//                }
//            }
//        }
//
//        public void write(byte[] bytes) {
//            try {
//                btOutputStream.write(bytes);
//            }
//            catch (IOException e) {
//                Log.e(TAG, "Error when writing to btOutputStream");
//            }
//        }
//    }


    class ListItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            bdDevice = arrayListBluetoothDevices.get(position);
            //bdClass = arrayListBluetoothDevices.get(position);
            //Log.i("Log", "The dvice : " + bdDevice.toString());
            /*
             * here below we can do pairing without calling the callthread(), we can directly call the
             * connect(). but for the safer side we must usethe threading object.
             */
            //callThread();
            //connect(bdDevice);

            Log.i("Device Name: ", "" + bdDevice.getName());
            mConnectThread = new ConnectThread(bdDevice);
            mConnectThread.start();


//            Boolean isBonded = false;
//            try {
//                isBonded = createBond(bdDevice);
//                if(isBonded)
//                {
//                    //arrayListpaired.add(bdDevice.getName()+"mout\n"+bdDevice.getAddress());
//                    //adapter.notifyDataSetChanged();
//                    getPairedDevices();
//                    adapter.notifyDataSetChanged();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }//connect(bdDevice);
//
//            Log.i("Log", "The bond is created: "+isBonded);
            connected = true;
        }
    }

    class ListItemClickedOnPaired implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bdDevice = arrayListPairedBluetoothDevices.get(position);
            try {
                Boolean removeBonding = removeBond(bdDevice);
                if (removeBonding) {
                    arrayListpaired.remove(position);
                    adapter.notifyDataSetChanged();
                }


                Log.i("Log", "Removed" + removeBonding);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


/*    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
//            Log.i("Log", "devicesss: "+bdDevice.getName());

        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    }*/




    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


/*    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }*/

    class ButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.buttonOn:
                    onBluetooth();
                    break;
                case R.id.buttonSearch:
                    arrayListBluetoothDevices.clear();
                    startSearching();
                    break;
                case R.id.buttonOff:
                    offBluetooth();
                    break;
                case R.id.buttonWeight:
                    getWeight();
                    break;
                default:
                    break;
            }
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    //device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                    //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                } catch (Exception e) {
                    Log.i("Log", "Inside the exception: ");
                    e.printStackTrace();
                }

                if (arrayListBluetoothDevices.size() < 1) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for (int i = 0; i < arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void startSearching() {
        Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private void onBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }

    private void offBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    public void getWeight() {
//        if (mConnectedThread == null){
//            Log.e("Error Connected ", "null");
//            return;
//        }
        mConnectedThread = new ConnectedThread(mConnectThread.getSocket());
        mConnectedThread.start();
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



    @Override
    public void onPause()
    {
        try {
            getActivity().unregisterReceiver(myReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        super.onStop();
    }





}
