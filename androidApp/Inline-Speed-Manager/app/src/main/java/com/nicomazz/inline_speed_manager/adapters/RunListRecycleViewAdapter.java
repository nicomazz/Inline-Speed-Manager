package com.nicomazz.inline_speed_manager.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.nicomazz.inline_speed_manager.R;
import com.nicomazz.inline_speed_manager.models.Run;
import com.nicomazz.inline_speed_manager.views.RunItemView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class RunListRecycleViewAdapter extends RealmRecyclerViewAdapter<Run, RunListRecycleViewAdapter.ViewHolder> {
    public RunListRecycleViewAdapter(Context context, OrderedRealmCollection<Run> data) {
        super(context, data, true);
        setHasStableIds(true);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int index) {
        return getData().get(index).millisCreation;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.populate(getData().get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RunItemView packageItemView;
        Run mItem;
        View rootView;

        ViewHolder(View view) {
            super(view);
            rootView = view;
            packageItemView = new RunItemView(context);
            ((CardView) view).addView(packageItemView);
            view.setOnClickListener(this);
        }

        void populate(Run Run) {
            mItem = Run;
            packageItemView.populate(Run);
        }

        @Override
        public void onClick(View v) {
            //RunList.openEventDetails(mItem.getListName(), context);
        }
    }
}
