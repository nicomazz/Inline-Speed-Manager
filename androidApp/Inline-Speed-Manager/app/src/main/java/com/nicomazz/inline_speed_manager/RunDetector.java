package com.nicomazz.inline_speed_manager;

import android.app.Activity;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.models.Run;

import java.util.ArrayList;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 10/02/17 23.16.
 */

public class RunDetector implements BTReceiverManager.OnTimeReceived {

    static private ArrayList<Long> runTimes = new ArrayList<>();

    private OnRunDetected listener;
    private BTReceiverManager btManager;

    private long lastTime = 0;

    //activity must implements  BTStatusInterface
    public RunDetector(OnRunDetected listener, BTReceiverManager.BTStatusInterface btListener, Activity activity) {
        this.listener = listener;
        btManager = new BTReceiverManager(this, btListener, activity);
    }

    @Override
    public void onTimeReceived(long time) {
        long runTime = time - lastTime;
        logTime(runTime);
        listener.onNewTimeReceived();
        lastTime = time;
        //if (isPossibleTime(runTime))
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

    //todo add setting for these
    private long getBestPossibleTime() {
        return 1000 * 3;
    }

    private long getWorseTime() {
        return 1000 * 15;
    }

    public void startRun() {
        btManager.write("!");
    }

    public interface OnRunDetected {
        void onRunDetected(Run run);
        void onNewTimeReceived();
    }

    public void onPause() {
        btManager.onPause();
    }

    public void onResume() {
        btManager.onResume();
    }
}
