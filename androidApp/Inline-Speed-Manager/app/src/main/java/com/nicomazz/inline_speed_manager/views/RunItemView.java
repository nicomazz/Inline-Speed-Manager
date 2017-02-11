package com.nicomazz.inline_speed_manager.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicomazz.inline_speed_manager.R;
import com.nicomazz.inline_speed_manager.models.Run;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nicolò Mazzucato (nicomazz97) on 10/02/17 23:03
 */

public class RunItemView extends LinearLayout {

    @BindView(R.id.creationTime)
    TextView creationDateTime;

    @BindView(R.id.time)
    TextView time;


    private Context context;
    private Run run;


    public RunItemView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RunItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RunItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(generateDefaultLayoutParams());
       // setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.run_layout, this, true);

        View root = getChildAt(0);
        ButterKnife.bind(this, root);
    }

    @SuppressLint("DefaultLocale")
    public void populate(Run run) {
        this.run = run;
        time.setText(String.format("%.3f s", ((double) run.durationMillis) / 1000));
        creationDateTime.setText(getDateTimeFromMillis(run.millisCreation));
    }

    public static String getDateTimeFromMillis(long millis) {
        DateFormat simpleDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM,
                Locale.ITALIAN);
        //simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date d = new Date(millis);
        return simpleDateFormat.format(d);
    }

}
