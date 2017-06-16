package com.kiwi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kiwi.ui.OnViewEventListener;
import com.kiwi.ui.R;

import static com.kiwi.ui.KwControlView.LEVEL1;
import static com.kiwi.ui.KwControlView.LEVEL2;
import static com.kiwi.ui.KwControlView.LEVEL3;

/**
 * Created by song.ding on 2017/2/7.
 */

public class KwFaceBeautyView extends FrameLayout implements View.OnClickListener {

    private OnViewEventListener onEventListener;
    private ImageView mBeautyLevel1;
    private ImageView mBeautyLevel2;
    private ImageView mBeautyLevel3;
    private ImageView mControl;

    private RelativeLayout RealFirst;
    private RelativeLayout RealSecond;
    private RelativeLayout RealThird;


    private OnItemSelectListener listener;

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(boolean isClick);
    }

    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public KwFaceBeautyView(Context context) {
        super(context);
        init(null, 0);
    }

    public KwFaceBeautyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwFaceBeautyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.face_beauty, this);

        mBeautyLevel1 = (ImageView) findViewById(R.id.first);
        mBeautyLevel2 = (ImageView) findViewById(R.id.second);
        mBeautyLevel3 = (ImageView) findViewById(R.id.third);
        mControl = (ImageView) findViewById(R.id.switch_image);

        RealFirst = (RelativeLayout) findViewById(R.id.first_real);
        RealSecond = (RelativeLayout) findViewById(R.id.second_real);
        RealThird = (RelativeLayout) findViewById(R.id.third_real);
        RealFirst.setOnClickListener(this);
        RealSecond.setOnClickListener(this);
        RealThird.setOnClickListener(this);
        mControl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.switch_image) {
            setBeauty2ViewClose();
            onEventListener.onSwitchBeauty(false);
            setImageResource(mControl);
        }

        if (v.getId() == R.id.first_real) {
            setBeauty2ViewClose();
            onEventListener.onSwitchBeauty(true);
            setImageResource(mBeautyLevel1);
            onEventListener.onFaceBeautyLevel(LEVEL1);
        }

        if (v.getId() == R.id.second_real) {
            setBeauty2ViewClose();
            onEventListener.onSwitchBeauty(true);
            onEventListener.onFaceBeautyLevel(LEVEL2);
            setImageResource(mBeautyLevel2);
        }

        if (v.getId() == R.id.third_real) {
            setBeauty2ViewClose();
            onEventListener.onSwitchBeauty(true);
            onEventListener.onFaceBeautyLevel(LEVEL3);
            setImageResource(mBeautyLevel3);
        }

    }

    /**
     * 关闭全局美颜2
     */
    private void setBeauty2ViewClose() {
        listener.onItemSelect(true);
    }


    public void closeBeauty() {
        setImageResource(mControl);
    }


    public void setImageResource(View btn) {
        setImageHighLight(btn, this.mControl, R.drawable.quxiao, R.drawable.quxiaonormal);
        setImageHighLight(btn, this.mBeautyLevel1, R.drawable.heidian, R.drawable.huangdian);
        setImageHighLight(btn, this.mBeautyLevel2, R.drawable.heidian, R.drawable.huangdian);
        setImageHighLight(btn, this.mBeautyLevel3, R.drawable.heidian, R.drawable.huangdian);
    }

    private void setImageHighLight(View btn, ImageView src, int normalResId, int highLightResId) {
        if (btn.equals(src)) {
            src.setImageResource(highLightResId);
        } else {
            src.setImageResource(normalResId);
        }
    }
}
