package com.nicomazz.inline_speed_manager;

import android.os.Build;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 11/02/17 18.51.
 */

public class TTSHelper {
    static TextToSpeech ttsService;
    static boolean initialized = false;

    static void init() {
        ttsService = new TextToSpeech(SpeedManagerApplication.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsService.setLanguage(Locale.ITALY);
                initialized = true;
            }
        });
    }

    static void speakText(String text) {
        speakText(text, null);
    }

    static void speakText(String text, final OnSpeakFinishedListener onSpeakFinished) {
        if (!initialized) {
            // init();
            return;
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsService.speak(text, TextToSpeech.QUEUE_FLUSH, null, text);
        } else {
            ttsService.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        Thread.sleep(1500);
                        if (onSpeakFinished != null)
                            onSpeakFinished.onSpeakFinished();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        ttsService.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                Log.d("speak", "finito con utterance: " + utteranceId);
                if (onSpeakFinished != null)
                    onSpeakFinished.onSpeakFinished();
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    public interface OnSpeakFinishedListener {
        void onSpeakFinished();
    }
}
