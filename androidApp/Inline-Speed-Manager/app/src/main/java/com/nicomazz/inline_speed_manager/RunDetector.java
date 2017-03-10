package com.nicomazz.inline_speed_manager;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.models.Run;

import java.util.ArrayList;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 10/02/17 23.16.
 */

public class RunDetector implements BTReceiverManager.OnTimeReceived {

    private static final String TAG = "RunDetector";
    static private ArrayList<Long> runTimes = new ArrayList<>();

    private OnRunDetected listener;
    private BTReceiverManager btManager;

    private long lastTime = 0;

    public static int worseTime = -1;
    public static int bestTime = -1;

    private boolean dontSendNextRun;

    //activity must implements  BTStatusInterface
    public RunDetector(OnRunDetected listener, BTReceiverManager.BTStatusInterface btListener, Activity activity) {
        this.listener = listener;
        btManager = new BTReceiverManager(this, btListener, activity);
    }

    @Override
    public void onTimeReceived(long time, long receivingTime){
        boolean isRunToBeSent = !dontSendNextRun;
        dontSendNextRun = false;


        long runTime = time - lastTime;
        logTime(runTime);

        listener.onNewTimeReceivedAt(receivingTime);

        if (runTime < getBestPossibleTime())
            return; // come se non ci fosse stato

        lastTime = time;
        if (!isPossibleTime(runTime)) return;

        if (isRunToBeSent)
            addRun(new Run(runTime));

    }

    private void addRun(Run run) {
        if (listener != null)
            listener.onRunDetected(run);
    }

    private void logTime(long runTime) {
        runTimes.add(runTime);

    }

    public String getLog() {
        StringBuilder sb = new StringBuilder();
        for (int i = runTimes.size() - 1; i >= runTimes.size() - 20 && i >= 0; i--) {
            Float time = (float) runTimes.get(i) / 1000;
            sb.append(time.toString()).append("\n");
        }
        return sb.toString();

    }

    public ArrayList<Long> getLogArray() {
        synchronized (runTimes) {
            return (ArrayList<Long>) runTimes.clone();
        }
    }

    private boolean isPossibleTime(long runTime) {
        return getBestPossibleTime() < runTime && runTime < getWorseTime();
    }

    private long getBestPossibleTime() {
        try {
            if (bestTime < 0)
                bestTime = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(SpeedManagerApplication.getContext()).getString("lowTime", "0"));
            return bestTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 1000;
        }
    }

    private long getWorseTime() {
        try {
            if (worseTime < 0)
                worseTime = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(SpeedManagerApplication.getContext()).getString("highTime", "0"));
            return worseTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 10 * 1000;
        }
    }

    public void startRun() {
        Log.d(TAG, "start run message sent");
        btManager.write("!");
    }

    public void requestTime() {
        dontSendNextRun = true;
        startRun();
    }

    public interface OnRunDetected {
        void onRunDetected(Run run);

        //receive time is System.currentTimeMillis() when the message is received
        void onNewTimeReceivedAt(Long receiveTime);
    }

    public void onPause() {
        btManager.onPause();
    }

    public void onResume() {
        btManager.onResume();
    }
}
