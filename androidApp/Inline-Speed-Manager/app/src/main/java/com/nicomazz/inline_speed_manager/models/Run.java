package com.nicomazz.inline_speed_manager.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 10/02/17 23:07
 */

public class Run extends RealmObject {
    @PrimaryKey
    public long millisCreation;

    public long durationMillis;
    public int conesFallen;
    public int trasmissionDelay = 0;

    public Run() {
    }

    public Run(long time) {
        durationMillis = time;
        millisCreation = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return durationMillis / 1000 + " secondi e " + (durationMillis % 1000)  + " millesimi";
    }
}
