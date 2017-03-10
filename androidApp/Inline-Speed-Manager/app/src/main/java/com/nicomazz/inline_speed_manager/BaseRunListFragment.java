package com.nicomazz.inline_speed_manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
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

import com.dd.CircularProgressButton;
import com.nicomazz.inline_speed_manager.Bluetooth.BTReceiverManager;
import com.nicomazz.inline_speed_manager.adapters.RunListRecycleViewAdapter;
import com.nicomazz.inline_speed_manager.models.Run;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * A placeholder fragment containing a simple view.
 */
public class BaseRunListFragment extends Fragment implements BTReceiverManager.BTStatusInterface, RunDetector.OnRunDetected, RunListRecycleViewAdapter.OnTimeDelete {


    @BindView(R.id.log_text)
    TextView logText;


    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.log_view)
    View logView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.sortFab)
    FloatingActionButton sortFab;

    @BindView(R.id.start_button)
    CircularProgressButton startButton;



    private RunListRecycleViewAdapter adapter;
    private Realm realm;

    private Snackbar snackBTInfo;

    private SortType sortType = SortType.creationTime;



    enum SortType {
        creationTime,
        bestTime
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.run_list_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        realm = Realm.getDefaultInstance();
        initRecycleView();


        initFabs();
        setHasOptionsMenu(true);
        return rootView;
    }

    protected void initFabs() {
        sortFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortType.equals(SortType.creationTime)) {
                    sortType = SortType.bestTime;
                    sortFab.setImageResource(R.drawable.ic_timer_white_24dp);
                    onBtStatusUpdated("Sorted by best time");
                } else {
                    sortType = SortType.creationTime;
                    sortFab.setImageResource(R.drawable.ic_sort_white_24dp);
                    onBtStatusUpdated("Sorted by creation time");
                }
                initRecycleView();
            }
        });
    }

    private void initRecycleView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        list.setLayoutManager(llm);
        adapter = new RunListRecycleViewAdapter(getContext(), getRealmData(),this);
        list.setAdapter(adapter);
    }

    private RealmResults<Run> getRealmData() {
        switch (sortType) {
            case creationTime:
                return realm.where(Run.class).findAllSortedAsync("millisCreation", Sort.DESCENDING);
            default:
                return realm.where(Run.class).findAllSortedAsync("durationMillis");
        }
    }


    private void addRunToRealm(final Run run) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(run);
            }
        });
    }
    @Override
    public void onTimeDelete(final Run run) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Run> result = realm.where(Run.class).equalTo("millisCreation",run.millisCreation).findAll();
                        result.deleteFirstFromRealm();
                    }
                });
            }
        });

    }

    @Override
    public void onBtStatusUpdated(final String s) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (snackBTInfo == null) {
                    snackBTInfo = Snackbar.make(list, s, LENGTH_LONG);
                }
                snackBTInfo.setText(s);
                if (!snackBTInfo.isShown())
                    snackBTInfo.show();
            }
        });
    }

    @Override
    public void onRunDetected(Run run) {
        addRunToRealm(run);
        TTSHelper.speakText(run.toString());
        updateLog();
    }

    @Override
    public void onNewTimeReceivedAt(Long receiveTime){
    updateLog();
    }

    public void updateLog() {
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
        else if (item.getItemId() == R.id.delete_all)
            deleteAllItems();
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Run.class);
            }
        });
    }

    private boolean isLogVisible() {
        return logView.getVisibility() == View.VISIBLE;
    }

    private void changeAllTimesVisibility() {
        if (isLogVisible())
            logView.setVisibility(View.GONE);
        else logView.setVisibility(View.VISIBLE);
        updateLog();
    }
}
