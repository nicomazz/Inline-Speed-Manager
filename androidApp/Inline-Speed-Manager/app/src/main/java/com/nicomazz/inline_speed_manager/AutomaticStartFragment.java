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
    private boolean interrotto = false;

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
                   stopRun();
                   return;
                }
                startVoice();
            }
        });
    }

    private void stopRun(){
        voiceThread.interrupt();
        voiceInProgress.set(false);
        setBadProgess();
        interrotto = true;
        Toast.makeText(getContext(), "interrompo precedente!", Toast.LENGTH_SHORT).show();
    }
    private int getDelay(String key, String def) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(key, def));

    }

    void startVoice() {

        voiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    voiceInProgress.set(true);

                    int initialDelay = getDelay("initial_delay", "0");
                    Thread.sleep(initialDelay);
                    checkInterrupted();
                    Log.d("autoStart", "ai posti");
                    /**
                     * AI POSTI
                     */
                    aiPosti();
                } catch (InterruptedException e) {
                    Looper.prepare();
                    Toast.makeText(getContext(), "start interrotto!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    voiceInProgress.set(false);
                    interrotto = false;
                } finally {

                }
            }
        });
        voiceThread.start();
    }

    private void checkInterrupted() throws InterruptedException {
       if (interrotto) throw  new InterruptedException();
    }
    private void aiPosti() {
        /**
         * AI POSTI
         */
        String aiPosti = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ai_posti", "ai posti");
        TTSHelper.speakText(aiPosti, new TTSHelper.OnSpeakFinishedListener() {
            @Override
            public void onSpeakFinished() {
                try {
                    Thread.sleep(getDelay("ai_posti_pronti", "3000"));
                    checkInterrupted();
                    pronti();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    voiceInProgress.set(false);
                    interrotto = false;
                }

            }
        });
    }

    private void pronti() {
        /**
         * PRONTI
         */
        Log.d("autoStart", "pronti");
        String pronti = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pronti", "pronti");
        TTSHelper.speakText(pronti, new TTSHelper.OnSpeakFinishedListener() {
            @Override
            public void onSpeakFinished() {
               // Looper.prepare();
                delayVia();
            }
        });

    }

    private void delayVia() {
        try {
            final int final_delay = getRandomOffset();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "prima di partire: " + final_delay + " ms", Toast.LENGTH_SHORT).show();
                }
            });
            Thread.sleep(final_delay);
            checkInterrupted();
        } catch (InterruptedException e) {
            Looper.prepare();
            Toast.makeText(getContext(), "INTERROTTO!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            voiceInProgress.set(false);
            interrotto = false;
            return;
        }
        /**
         * VIA
         */
        Log.d("autoStart", "via");

        startRun();
    }

    private int getRandomOffset() {
        int delayConst = getDelay("min_delay_via", "1000");
        int max_variable_delay = getDelay("max_random_offset", "2000");
        int random_delay = new Random(System.currentTimeMillis()).nextInt(max_variable_delay);
        final int final_delay = delayConst + random_delay;
        return final_delay;
    }

    private void startRun() {
        initValidTimer();
        runStartIntercept = true;
        runInProgress = true;
        try {
            runDetector.requestTime();
            playStartSound();
            setStartTime(System.currentTimeMillis());
        } catch (BTReceiverManager.NotConnectedException e) {
            setBadProgess();
        }
        voiceInProgress.set(false);
    }

    @Override
    public void onNewTimeReceivedAt(Long receiveTime) {
        super.onNewTimeReceivedAt(receiveTime);

        if (voiceInProgress.get() && !runInProgress) {
            Log.d(TAG, "falsa");
            TTSHelper.speakText("FALSA");
            stopRun();
            return;
        }
        if (runStartIntercept) {
            runStartIntercept = false;
            startButtonLoading();
        }
    }

    private void playStartSound() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1f);

            }
        });

        /*  final MediaPlayer mp = MediaPlayer.create(AutomaticStartFragment.this.getContext(), R.raw.beep);
        mp.start();*/
    }


}
