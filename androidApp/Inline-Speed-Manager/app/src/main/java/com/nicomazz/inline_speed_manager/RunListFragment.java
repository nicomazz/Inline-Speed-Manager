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
public class RunListFragment extends Fragment implements BTReceiverManager.BTStatusInterface, RunDetector.OnRunDetected {


    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.log_view)
    View logView;

    @BindView(R.id.log_text)
    TextView logText;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.sortFab)
    FloatingActionButton sortFab;

    private RunListRecycleViewAdapter adapter;
    private Realm realm;
    private RunDetector runDetector;

    private Snackbar snackBTInfo;

    private SortType sortType = SortType.creationTime;

    enum SortType {
        creationTime,
        bestTime
    }

    ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.run_list_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        realm = Realm.getDefaultInstance();
        initRecycleView();

        runDetector = new RunDetector(this, this, getActivity());

        initFabs();
        setHasOptionsMenu(true);
        TTSHelper.init();
        return rootView;
    }

    private void initFabs() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDetector.onPause();
                runDetector.onResume();
            }
        });
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
        //  llm.setReverseLayout(true);
        //llm.setStackFromEnd(true);
        list.setLayoutManager(llm);
        adapter = new RunListRecycleViewAdapter(getContext(), getRealmData());
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
        updateLog();
        TTSHelper.speakText(run.toString());
    }

    public void updateLog() {
        //if (isLogVisible())
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
        //logView.setVisibility(logText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        if (isLogVisible())
            logView.setVisibility(View.GONE);
        else logView.setVisibility(View.VISIBLE);
        updateLog();
    }
}
