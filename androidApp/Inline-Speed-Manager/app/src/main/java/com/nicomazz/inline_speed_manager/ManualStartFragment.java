package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.models.Run;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ManualStartFragment extends BaseRunListFragment {

    private static final String TAG = "ManualStartFrag";
    protected RunDetector runDetector;

    protected boolean runStartIntercept = false;
    protected boolean runInProgress = false;
    private Timer validRunTimer;

    protected long startTime;

    protected long delayInTransmission = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        runDetector = new RunDetector(this, this, getActivity());
        initStartButton();
        return rootView;
    }

    protected void setStartTime(long time) {
        startTime = time;
    }

    protected void initStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startButton.getProgress() > 0 && startButton.getProgress() != 100) {
                    Log.e(TAG, "run in progress, set bad!");
                    setBadProgess();
                    return;
                }
                startTime = System.currentTimeMillis();
                try {
                    startRun();
                } catch (BTReceiverManager.NotConnectedException e) {
                    setBadProgess();
                }
            }
        });
    }


    private void startRun() {
        initValidTimer();
        runStartIntercept = true;
        runInProgress = true;
        runDetector.startRun();
    }


    @Override
    public void onRunDetected(Run run) {
        if (runStartIntercept) {
            Toast.makeText(getContext(), "Delay: " + delayInTransmission + " ms", Toast.LENGTH_SHORT).show();
        }
        updateLog();
        if (!runInProgress) return;
        if (runStartIntercept) {
            startButtonLoading();
            return;
        }
        run.durationMillis += delayInTransmission;
        run.trasmissionDelay = (int) delayInTransmission;
        onRunTimeReceived();
        super.onRunDetected(run);
    }

    protected void startButtonLoading() {
        runStartIntercept = false;
        startButton.setIndeterminateProgressMode(true);
        startButton.setProgress(50);
    }

    protected void onRunTimeReceived() {
        Log.e(TAG, "run time received!");
        runInProgress = false;
        if (validRunTimer != null) validRunTimer.cancel();
        startButton.setProgress(100);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startButton.setProgress(0);
            }
        }, 2000);
    }

    protected void initValidTimer() {
        if (validRunTimer != null) validRunTimer.cancel();
        validRunTimer = new Timer();
        validRunTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                setBadProgess();
            }
        }, 15000);
    }

    protected void setBadProgess() {
        if (validRunTimer != null) validRunTimer.cancel();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                runInProgress = false;
                runStartIntercept = false;
                startButton.setProgress(-1);
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startButton.setProgress(0);
            }
        }, 2000);
    }

    protected void initFabs() {
        super.initFabs();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDetector.onPause();
                runDetector.onResume();
            }
        });
    }


    @Override
    public void onNewTimeReceivedAt(Long receiveTime) {
        if (runStartIntercept)
            delayInTransmission = ((receiveTime - startTime) / 2);
        if (logView.getVisibility() == View.VISIBLE)
            updateLog();
    }

    public void updateLog() {
        logText.setText(runDetector.getLog());
    }


    @Override
    public void onPause() {
        super.onPause();
        runDetector.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        runDetector.onResume();
    }

}
