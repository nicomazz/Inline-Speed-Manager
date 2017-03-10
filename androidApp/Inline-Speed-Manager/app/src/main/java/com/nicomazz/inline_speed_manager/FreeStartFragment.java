package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicomazz.inline_speed_manager.models.Run;

import butterknife.BindView;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class FreeStartFragment extends BaseRunListFragment {


    private RunDetector runDetector;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        runDetector = new RunDetector(this, this, getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
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
    public void onPause() {
        super.onPause();
        runDetector.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        runDetector.onResume();
    }


    @Override
    public void onRunDetected(Run run) {
        super.onRunDetected(run);
        updateLog();
    }

    @Override
    public void onNewTimeReceivedAt(Long receiveTime){
        updateLog();
    }

    public void updateLog() {
        if (isLogVisible())
            logText.setText(runDetector.getLog());
    }




    private boolean isLogVisible() {
        return logView.getVisibility() == View.VISIBLE;
    }

}
