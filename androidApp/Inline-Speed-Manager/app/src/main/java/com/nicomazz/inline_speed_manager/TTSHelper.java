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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (Voice tmpVoice : ttsService.getVoices()) {
                        Log.d("ttsHelper","voice: "+tmpVoice.getName());
                    }
                }

                initialized = true;
            }
        });
    }
    static void speakText(String text){
        if (!initialized) {
           // init();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int sz  = ttsService.getVoices().size();
            int pos = new Random().nextInt(sz);
            //ttsService.setVoice((Voice) ttsService.getVoices().toArray()[pos]);
        }
        ttsService.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        //ttsService.speak(text)//
    }
}
