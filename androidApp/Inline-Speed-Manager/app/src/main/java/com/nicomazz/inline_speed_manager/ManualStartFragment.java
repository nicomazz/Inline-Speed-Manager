package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.models.Run;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ManualStartFragment extends BaseRunListFragment {


    private RunDetector runDetector;

    private boolean runStartIntercept = false;
    private boolean runInProgress = false;
    private Timer validRunTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        runDetector = new RunDetector(this, this, getActivity());
        initStartButton();
        return rootView;
    }

    private void initStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startRun();
                    initValidTimer();
                } catch (BTReceiverManager.NotConnectedException e) {
                    setBadProgess();
                }
            }
        });
    }


    private void startRun() {
        runStartIntercept = true;
        runInProgress = true;
        runDetector.startRun();
    }


    @Override
    public void onRunDetected(Run run) {
        updateLog();
        if (!runInProgress) return;
        if (runStartIntercept) {
            startButtonLoading();
            return;
        }
        onRunTimeReceived();
        super.onRunDetected(run);
    }

    private void startButtonLoading() {
        runStartIntercept = false;
        startButton.setIndeterminateProgressMode(true);
        startButton.setProgress(50);
    }

    private void onRunTimeReceived() {
        runInProgress = false;
        if (validRunTimer != null) validRunTimer.cancel();
        startButton.setProgress(100);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startButton.setProgress(0);
            }
        },2000);
    }

    private void initValidTimer() {
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

    private void setBadProgess() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                runInProgress = false;
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
    public void onNewTimeReceived() {
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
