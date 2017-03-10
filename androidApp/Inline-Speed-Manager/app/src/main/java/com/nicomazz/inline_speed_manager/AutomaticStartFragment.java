package com.nicomazz.inline_speed_manager;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class AutomaticStartFragment extends ManualStartFragment {


    private static final String TAG = "AutoStart";
    private AtomicBoolean voiceInProgress = new AtomicBoolean(false);

    private Thread voiceThread;
    private SoundPool soundPool;
    private int soundID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(getContext(), R.raw.beep, 1);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void initStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceInProgress.get() || (startButton.getProgress() > 0 && startButton.getProgress() != 100)) {
                    voiceThread.interrupt();
                    voiceInProgress.set(false);
                    setBadProgess();
                    return;
                }
                startVoice();
            }
        });
    }

    void startVoice() {

        voiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    voiceInProgress.set(true);
                    Log.d("autoStart", "ai posti");
                    /**
                     * AI POSTI
                     */
                    TTSHelper.speakText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ai_posti", "ai posti"));
                    Thread.sleep(1500);

                    /**
                     * PRONTI
                     */
                    Log.d("autoStart", "pronti");
                    TTSHelper.speakText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pronti", "pronti"));
                    int max_millis = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("before_start", "5000"));
                    final int val = new Random(System.currentTimeMillis()).nextInt(Math.max(1000, max_millis - 1000)) + 1000;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "prima di partire: " + val + " ms", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Thread.sleep(val);

                    /**
                     * VIA
                     */
                    Log.d("autoStart", "via");

                    startRun();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    voiceInProgress.set(false);
                }
            }
        });
        voiceThread.start();
    }

    private void startRun() {
        initValidTimer();
        runStartIntercept = true;
        runInProgress = true;
        setStartTime(System.currentTimeMillis());
        try {
            runDetector.requestTime();
            playStartSound();
        } catch (BTReceiverManager.NotConnectedException e) {
            setBadProgess();
        }
    }

    @Override
    public void onNewTimeReceived() {
        super.onNewTimeReceived();
        if (voiceInProgress.get() && !runInProgress) {
            Log.d(TAG, "falsa");
            TTSHelper.speakText("FALSA");
            return;
        }
        if (runStartIntercept) {
            delayInTransmission = ((System.currentTimeMillis() - startTime) / 2);
            runStartIntercept = false;
            startButtonLoading();
         //   playStartSound();
        }
    }

    private void playStartSound() {
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1f);

        /*  final MediaPlayer mp = MediaPlayer.create(AutomaticStartFragment.this.getContext(), R.raw.beep);
        mp.start();*/
    }


}
