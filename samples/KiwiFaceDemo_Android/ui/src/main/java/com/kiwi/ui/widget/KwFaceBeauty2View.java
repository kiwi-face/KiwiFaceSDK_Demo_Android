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

import static com.kiwi.ui.KwControlView.REMOVE_BLEMISHES;
import static com.kiwi.ui.KwControlView.SKIN_SHINNING_TENDERNESS;
import static com.kiwi.ui.KwControlView.SKIN_TONE_PERFECTION;
import static com.kiwi.ui.KwControlView.SKIN_TONE_SATURATION;

/**
 * Created by song.ding on 2017/2/7.
 */

public class KwFaceBeauty2View extends FrameLayout implements View.OnClickListener {
    private ImageView mSwitchBeautySecond;
    private TextView mTextSwitchSecond;
    private LinearLayout mBeautyAdjustLayoutSecond;
    private boolean isOpen = false;
    private OnViewEventListener onEventListener;
    private List<FilterAdjuster> filterAdjusters2;

    private OnSwitchBeauty2 listener;

    public void setOnSwitchBeautyListener(OnSwitchBeauty2 listener) {
        this.listener = listener;
    }

    public interface OnSwitchBeauty2 {
        void onSwitchBeauty(boolean open);
    }

    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public KwFaceBeauty2View(Context context) {
        super(context);
        init(null, 0);
    }

    public KwFaceBeauty2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwFaceBeauty2View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.face_beauty2, this, true);
        //全局美颜2
        mSwitchBeautySecond = (ImageView) findViewById(R.id.switch_beauty_second);
        mTextSwitchSecond = (TextView) findViewById(R.id.text_kaiguan_second);
        mBeautyAdjustLayoutSecond = (LinearLayout) findViewById(R.id.layout_beauty_adjust_second);
        mTextSwitchSecond.setText(R.string.cancel);
        mSwitchBeautySecond.setOnClickListener(this);
    }


    private void initFilter() {
        filterAdjusters2 = null;
        filterAdjusters2 = new ArrayList<>();
        filterAdjusters2.add(new FilterAdjuster(getContext().getString(R.string.skinPerfection), SKIN_TONE_PERFECTION, 0.95f, 0.6f, 0.8f));
        filterAdjusters2.add(new FilterAdjuster(getContext().getString(R.string.skinRemoveBlemishes), REMOVE_BLEMISHES, 0.95f, 0.3f, 0.5f));
        filterAdjusters2.add(new FilterAdjuster(getContext().getString(R.string.skinSaturation), SKIN_TONE_SATURATION, 0.05f, 0.9f, 0.5f));
        filterAdjusters2.add(new FilterAdjuster(getContext().getString(R.string.skinTenderness), SKIN_SHINNING_TENDERNESS, 0.05f, 0.4f, 0.15f));
    }


    public void closeBeauty2() {
        isOpen = false;
        setSeekBarEnabled();
        mSwitchBeautySecond.setImageResource(R.drawable.guan);
        mTextSwitchSecond.setText(getResources().getString(R.string.cancel));
    }

    private void setSeekBarEnabled() {
        if (filterAdjusters2 == null) {
            return;
        }
        for (FilterAdjuster param : filterAdjusters2) {
            SeekBar seekBar = param.getSeekBar();
            if (seekBar != null) {
                seekBar.setEnabled(isOpen);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.switch_beauty_second) {
            if (isOpen) {
                isOpen = false;
            } else {
                isOpen = true;
            }
            listener.onSwitchBeauty(isOpen);
            mTextSwitchSecond.setText(isOpen ? R.string.open : R.string.cancel);
            mSwitchBeautySecond.setImageResource(isOpen ? R.drawable.kai : R.drawable.guan);
            onEventListener.onSwitchBeauty2(isOpen);
            for (FilterAdjuster param : filterAdjusters2) {
                SeekBar seekBar = param.getSeekBar();
                if (seekBar != null) {
                    seekBar.setEnabled(isOpen);
                }
            }
        }
    }

    public void initBeautyAdjustView() {
        if (filterAdjusters2 == null || filterAdjusters2.isEmpty()) {
            mBeautyAdjustLayoutSecond.removeAllViews();
            initFilter();
            for (FilterAdjuster adjusterParam : filterAdjusters2) {
                View view = initSeekbarView(adjusterParam);
                mBeautyAdjustLayoutSecond.addView(view);
            }
        }
    }

    @NonNull
    private View initSeekbarView(final FilterAdjuster param) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.item_beauty_adjuster, null, false);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.adjuster_seekbar);
        TextView title = (TextView) view.findViewById(R.id.adjuster_title);
        final TextView value = (TextView) view.findViewById(R.id.adjuster_value);
        seekBar.setEnabled(isOpen);
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
