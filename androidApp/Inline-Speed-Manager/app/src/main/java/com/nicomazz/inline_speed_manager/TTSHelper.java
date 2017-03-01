package com.nicomazz.inline_speed_manager;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 11/02/17 18.51.
 */

public class TTSHelper {
    static TextToSpeech ttsService;
    static boolean initialized = false;

    static void init(){
        ttsService = new TextToSpeech(SpeedManagerApplication.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsService.setLanguage(Locale.ITALY);
                initialized = true;
            }
        });
    }
    static void speakText(String text){
        if (!initialized) {
           // init();
            return;
        }
        ttsService.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
