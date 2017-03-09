package com.nicomazz.inline_speed_manager;

import android.media.MediaPlayer;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class AutomaticStartFragment extends ManualStartFragment {


    private AtomicBoolean voiceInProgress = new AtomicBoolean(false);
    private AtomicBoolean cancelRun = new AtomicBoolean(false);

    protected void initStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceInProgress.get() || (startButton.getProgress() > 0 && startButton.getProgress() != 100)) {
                    setBadProgess();
                    cancelRun.set(true);
                    return;
                }
                voiceInProgress.set(true);
                startVoice();
                try {
                    startRun();
                } catch (BTReceiverManager.NotConnectedException e) {
                    setBadProgess();
                }
            }
        });
    }

    void startVoice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    Log.d("autoStart","ai posti");
                    TTSHelper.speakText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ai_posti","ai posti"));
                    Thread.sleep(1500);
                    if (cancelRun.get()) {
                        cancelRun.set(false);
                        voiceInProgress.set(false);
                        return;
                    }
                    Log.d("autoStart","pronti");
                    TTSHelper.speakText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pronti","pronti"));
                    int val = new Random(System.currentTimeMillis()).nextInt(5000);
                    Thread.sleep(val);
                    if (cancelRun.get()) {
                        cancelRun.set(false);
                        voiceInProgress.set(false);
                        return;
                    }
                    Log.d("autoStart","via");

                    final MediaPlayer mp = MediaPlayer.create(AutomaticStartFragment.this.getContext(), R.raw.beep);
                    mp.start();

                   // TTSHelper.speakText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("via","via"));
                    setStartTime(System.currentTimeMillis());
                    try {
                        startRun();
                    } catch (BTReceiverManager.NotConnectedException e) {
                        setBadProgess();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    voiceInProgress.set(false);
                }
            }
        }).start();
    }
}
