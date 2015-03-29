package com.group12.syde362.checkout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static android.os.Looper.*;

/**
 * Created by Andy Seo on 3/19/2015.
 */
public abstract class BluetoothHelper extends Fragment {

    ListView listViewPaired;
    ListView listViewDetected;
    ArrayList<String> arrayListpaired;
    Button buttonSearch, buttonOn, buttonOff, buttonWeight;
    ArrayAdapter<String> adapter, detectedAdapter;
    //    static HandleSearch handleSeacrh;
    static BluetoothDevice bdDevice;
    BluetoothClass bdClass;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    //private ButtonClicked clicked;
    //ListItemClickedOnPaired listItemClickedOnPaired;
    BluetoothAdapter bluetoothAdapter = null;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
    //ListItemClicked listItemClicked;

    /*
https://github.com/aron-bordin/Android-with-Arduino-Bluetooth/blob/master/Android/Example/BluetoothArduino.java
 */
    OutputStream mOut;
    InputStream mIn;
    private String TAG = "BluetoothConnector";
    private BluetoothSocket mBlueSocket;
     boolean connected = false;
    BluetoothDevice mDevice;
    static ConnectThread mConnectThread;
    static ConnectedThread mConnectedThread = null;


  //  private OnFragmentInteractionListener mListener;

    // Message types used by the Handler
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_READ = 2;
    static String readMessage = "";
    static String readMessage1 = "";
    SendReceiveBytes sendReceiveBT = null;
    static double measuredWeight;
    double checkWeight;
    static myHandler mHandler = new myHandler();
    static int counter = 1;



    /*
    Connecting with Arduino via Bluetooth
     */
    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            Log.i("ConnectThread", "Started");
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                //tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public BluetoothSocket getSocket(){
            //Log.i("Socket", ""+mmSocket);
            return mmSocket;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
//            mConnectedThread = new ConnectedThread(mmSocket);
//            mConnectedThread.start();
//            Toast.makeText(getActivity(), "Connected to "+bdDevice.getName(), Toast.LENGTH_SHORT).show();
            Log.i("bluetooth", "connected!!");
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.i("ConnectedThread", "Started");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i("ConnectedThread", "ConnectedThread run()");
            sendReceiveBT = new SendReceiveBytes(mmSocket);
            new Thread(sendReceiveBT).start();
            String red = "r";
            byte[] myByte = stringToBytesUTFCustom(red);
            sendReceiveBT.write(myByte);
        }

        public void cancel() {
            try {
                mmSocket.close();
//                mmInStream.close();
//                mmOutStream.close();
            } catch (IOException e) {
            }
        }
    }


    public static byte[] stringToBytesUTFCustom(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length << 1];
        for (int i = 0; i < buffer.length; i++) {
            int bpos = i << 1;
            b[bpos] = (byte) ((buffer[i] & 0xFF00) >> 8);
            b[bpos + 1] = (byte) (buffer[i] & 0x00FF);
        }
        return b;
    }


    private class SendReceiveBytes implements Runnable {
        private BluetoothSocket btSocket;
        private InputStream btInputStream = null;
        private OutputStream btOutputStream = null;
        String TAG = "SendReceiveBytes";

        public SendReceiveBytes(BluetoothSocket socket) {
            btSocket = socket;
            try {
                btInputStream = btSocket.getInputStream();
                btOutputStream = btSocket.getOutputStream();
            } catch (IOException streamError) {
                Log.e(TAG, "Error when getting input or output Stream");
            }
        }

        public void run() {
            Log.i("SendReceiveBytes", "SendReceiveBytes run()");
            byte[] buffer = new byte[1024]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = btInputStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Error reading from btInputStream");
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                btOutputStream.write(bytes);
            }
            catch (IOException e) {
                Log.e(TAG, "Error when writing to btOutputStream");
                e.printStackTrace();
            }
        }



//        public void getWeight() {
//            mConnectedThread = new ConnectedThread(mConnectThread.getSocket());
//            mConnectedThread.start();
//        }




    }
            /*
http://stackoverflow.com/questions/23540754/send-data-from-arduino-to-android-app-via-bluetooth
 */
    // The Handler that gets information back from the Socket


    static class myHandler extends Handler{

        @Override
            public  void handleMessage(Message msg) {
//            Log.i("myHandler", "myHandler run()");
                switch (msg.what) {
                    case MESSAGE_WRITE:
                        //Do something when writing
                        break;
                    case MESSAGE_READ:
//                        Log.i("myHandler", "message_read run()");
                        //Get the bytes from the msg.obj
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        readMessage1 = new String(readBuf, 0, msg.arg1);
                        if(counter > 1) {
                            readMessage = readMessage + readMessage1;
                            Log.i("Weight String: ", "" + readMessage1);
                            counter = 1;
                        }
                        counter++;
                        break;
                }
            }
    };





}
