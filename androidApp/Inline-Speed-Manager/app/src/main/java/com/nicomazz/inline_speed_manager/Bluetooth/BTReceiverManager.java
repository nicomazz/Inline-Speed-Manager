package com.nicomazz.inline_speed_manager.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 10/02/17 23.16.
 */

public class BTReceiverManager {
    private static final String TAG = "BTReceiverManager";
    private BluetoothDevice BTReceiver;

    private OnTimeReceived timeReceivedListener;
    private BTStatusInterface btStatusListener;
    private Context context;

    private Activity activity;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String getReceiverName() {
        return "speed";
    }


    public BTReceiverManager(OnTimeReceived timeReceivedListener, BTStatusInterface btStatusListener, Activity activity) {
        this.activity = activity;
        this.timeReceivedListener = timeReceivedListener;
        this.context = activity;
        this.btStatusListener = btStatusListener;
    }

    private void init(Activity activity) {
        if (isConnected() || !isBtEnabled(activity))
            return;
        BTReceiver = getReceiverDevice();
        if (BTReceiver == null) return;
        new ConnectBT().execute();
    }

    public boolean isBtEnabled(Activity activity) {
        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            log("Bluetooth Not Available");
            return false;
        }
        if (!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnBTon, 1);
            return false;
        }
        return true;
    }

    private BluetoothDevice getReceiverDevice() {
        ArrayList<BluetoothDevice> speedReceiverDevices
                = getReceiverWithNameContains(getReceiverName().toLowerCase());

        if (speedReceiverDevices.size() == 0) {
            log("No bounded devices found.");
            return null;
        }
        if (speedReceiverDevices.size() > 1) {
            log("Too many speed devices. return one at random.");
        }
        return speedReceiverDevices.get(new Random().nextInt(speedReceiverDevices.size()));
    }

    private ArrayList<BluetoothDevice> getReceiverWithNameContains(String s) {
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices)
            if (device.getName().toLowerCase().contains(s))
                devices.add(device);
        return devices;
    }

    //todo set this in preferences


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            log("Connecting...");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (isConnected()) return null;
                btSocket = BTReceiver.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                log("Connection failed.");
                return;
            }
            log("Connected");
            isBtConnected = true;

            new ReceiverThread(btSocket).start();
        }
    }

    public boolean isConnected() {
        return btSocket != null && isBtConnected;
    }


    private void disconnect() {
        if (!isConnected()) return; //If the btSocket is busy

        try {
            btSocket.close(); //close connection
            isBtConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
            log("Error in disconnecting");
        }
    }

    private long timeWhenMessageReceived = 0;

    private void handleMessageReceived(String received) {
        try {
            final Long millisToSend = Long.parseLong(received);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "time to handle message: " + (System.currentTimeMillis() - timeWhenMessageReceived));
                    timeReceivedListener.onTimeReceived(millisToSend, timeWhenMessageReceived);
                }
            });
        } catch (Exception e) {
            log("ReceivedStrangeThings: " + received);
        }
    }
    //create new class for connect thread
    private class ReceiverThread extends Thread {
        private InputStream mmInStream;

        //creation of the connect thread
        public ReceiverThread(BluetoothSocket socket) {
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Scanner in = new Scanner(mmInStream);
            in.useDelimiter("/");

            // Keep looping to listen for received messages
            while (true) {

                try {
                    String s = in.next();
                    timeWhenMessageReceived = System.currentTimeMillis();
                    handleMessageReceived(s);
                    // Log.d(TAG, "Message received: " + s);
                } catch (NoSuchElementException e) {
                    break;
                }

            }
        }
    }

    //write method
    public void write(String input) {
        if (!isConnected()) {
            Toast.makeText(context, "Not connected", Toast.LENGTH_LONG).show();
            throw new NotConnectedException();
        }
        try {
            btSocket.getOutputStream().write(input.getBytes());
        } catch (IOException e) {
            Toast.makeText(context, "Connection Failure", Toast.LENGTH_LONG).show();
        }

    }

    public interface OnTimeReceived {
        //il secondo parametro sono i millis ai quali si è ricevuto il tempo
        void onTimeReceived(long time, long receivingTime);
    }

    public interface BTStatusInterface {
        void onBtStatusUpdated(String s);
    }

    public void onPause() {
        disconnect();
    }

    public void onResume() {
        init(activity);
    }

    private void log(String s) {
        btStatusListener.onBtStatusUpdated(s);
    }

    public class NotConnectedException extends RuntimeException {
    }
}
