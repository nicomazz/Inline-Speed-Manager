package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.adapters.RunListRecycleViewAdapter;
import com.nicomazz.inline_speed_manager.models.Run;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * A placeholder fragment containing a simple view.
 */
public class RunListFragment extends Fragment implements BTReceiverManager.BTStatusInterface, RunDetector.OnRunDetected {


    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.log_view)
    View logView;

    @BindView(R.id.log_text)
    TextView logText;

    private RunListRecycleViewAdapter adapter;
    private Realm realm;
    private RunDetector runDetector;

    private Snackbar snackBTInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.run_list_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        realm = Realm.getDefaultInstance();
        initRecycleView();

        runDetector = new RunDetector(this, this, getActivity());

        setHasOptionsMenu(true);
        return rootView;
    }

    private void initRecycleView() {
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RunListRecycleViewAdapter(getContext(), getRealmData());
        list.setAdapter(adapter);
    }

    private RealmResults<Run> getRealmData() {
        return realm.where(Run.class).findAllAsync();
    }


    private Run addRunToRealm(final Run run) {
        realm.beginTransaction();
        Run persistentPacking = realm.copyToRealm(run);
        realm.commitTransaction();
        return persistentPacking;
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
    public void onBtStatusUpdated(String s) {
        if (snackBTInfo == null) {
            snackBTInfo = Snackbar.make(list, s, LENGTH_LONG);
        }
        snackBTInfo.setText(s);
        if (!snackBTInfo.isShown())
            snackBTInfo.show();
    }

    @Override
    public void onRunDetected(Run run) {
        addRunToRealm(run);
        updateLog();
    }

    public void updateLog() {
        if (logView.getVisibility() == View.VISIBLE)
            logText.setText(runDetector.getLog());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.debug)
            changeAllTimesVisibility();
        return super.onOptionsItemSelected(item);
    }

    private void changeAllTimesVisibility() {
        logView.setVisibility(logText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        updateLog();
    }
}
