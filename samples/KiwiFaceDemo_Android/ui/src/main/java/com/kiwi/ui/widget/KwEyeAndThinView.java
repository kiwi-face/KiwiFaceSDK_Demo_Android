package com.kiwi.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kiwi.ui.FilterAdjuster;
import com.kiwi.ui.OnViewEventListener;
import com.kiwi.ui.R;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.ui.KwControlView.BEAUTY_BIG_EYE_TYPE;
import static com.kiwi.ui.KwControlView.BEAUTY_THIN_FACE_TYPE;


/**
 * Created by song.ding on 2017/2/6.
 * <p>
 * 大眼瘦脸
 */

public class KwEyeAndThinView extends FrameLayout implements View.OnClickListener {

    private OnViewEventListener onEventListener;
    private ImageView mSwitchBeauty;
    private LinearLayout mBeautyAdjustLayout;
    private TextView mTextSwitch;
    private List<FilterAdjuster> filterAdjusters;
    private boolean isChecked = false;


    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public KwEyeAndThinView(Context context) {
        super(context);
        init(null, 0);
    }

    public KwEyeAndThinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwEyeAndThinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.eye_thin_view, this);
        mSwitchBeauty = (ImageView) findViewById(R.id.switch_beauty);
        mBeautyAdjustLayout = (LinearLayout) findViewById(R.id.layout_beauty_adjust);
        mTextSwitch = (TextView) findViewById(R.id.text_kaiguan);
        mTextSwitch.setText(R.string.cancel);
        mSwitchBeauty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.switch_beauty) {
            if (isChecked) {
                isChecked = false;
            } else {
                isChecked = true;
            }
            mTextSwitch.setText(isChecked ? R.string.open : R.string.cancel);
            mSwitchBeauty.setImageResource(isChecked ? R.drawable.kai : R.drawable.guan);
            onEventListener.onSwitchBeautyFace(isChecked);
            for (FilterAdjuster param : filterAdjusters) {
                SeekBar seekBar = param.getSeekBar();
                if (seekBar != null) {
                    seekBar.setEnabled(isChecked);
                }
            }
        }
    }


    public void initBeautyAdjustView() {
        if (filterAdjusters == null || filterAdjusters.isEmpty()) {
            mBeautyAdjustLayout.removeAllViews();
            initBeautyFilter();
            for (FilterAdjuster adjusterParam : filterAdjusters) {
                View view = initSeekbarView(adjusterParam);
                mBeautyAdjustLayout.addView(view);
            }
        }
    }


    private void initBeautyFilter() {
        filterAdjusters = null;
        filterAdjusters = new ArrayList<>();
        filterAdjusters.add(new FilterAdjuster(getContext().getString(R.string.eyemagnify), BEAUTY_BIG_EYE_TYPE, 0.05f, 0.3f, 0.10f));
        filterAdjusters.add(new FilterAdjuster(getContext().getString(R.string.faceSculpt), BEAUTY_THIN_FACE_TYPE, 0.99f, 0.93f, 0.95f));
    }


    @NonNull
    private View initSeekbarView(final FilterAdjuster param) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.item_beauty_adjuster, null, false);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.adjuster_seekbar);
        TextView title = (TextView) view.findViewById(R.id.adjuster_title);
        final TextView value = (TextView) view.findViewById(R.id.adjuster_value);
        seekBar.setEnabled(isChecked);
        int defaultValue = param.getDefaultValue();
        seekBar.setProgress(defaultValue);
        value.setText(defaultValue + "%");
        title.setText(param.getTitle());
        param.setSeekBar(seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("UI", "onProgressChangedBB" + param.getValue(progress));
                onEventListener.onAdjustFaceBeauty(param.getType(), param.getValue(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }
}
