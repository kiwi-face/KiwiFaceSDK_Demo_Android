package com.kiwi.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.kiwi.ui.widget.KwDistortionView;
import com.kiwi.ui.widget.KwEyeAndThinView;
import com.kiwi.ui.widget.KwFaceBeauty2View;
import com.kiwi.ui.widget.KwFaceBeautyView;
import com.kiwi.ui.widget.kwStickerView;

/**
 * Created by song.ding on 2017/2/6.
 */

public class KwEffectView extends FrameLayout {
    /**
     * 自定义控件
     * 贴纸
     */
    private kwStickerView kwStickerView;
    /**
     * 自定义控件
     * 哈哈镜
     */
    private KwDistortionView kwDistortionView;
    /**
     * 自定义控件
     * 大眼瘦脸
     */
    private KwEyeAndThinView KwEyeAndThinView;
    /**
     * 自定义控件
     * 全局美颜
     */
    private KwFaceBeautyView kwFaceBeautyView;
    /**
     * 自定义控件
     * 全局美颜2
     */
    private KwFaceBeauty2View kwFaceBeauty2;

    private OnViewEventListener onEventListener;
    private RadioGroup mBottomView;

    private RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            if (id == R.id.btn_bar_beauty) {
                KwEyeAndThinView.setOnEventListener(onEventListener);
                KwEyeAndThinView.setVisibility(VISIBLE);
                KwEyeAndThinView.initBeautyAdjustView();
                setViewVisual(false, false, true, false, false);
            }

            if (id == R.id.btn_bar_sticker) {
                kwStickerView.setOnEventListener(onEventListener);
                kwStickerView.initStickerListView();
                setViewVisual(true, false, false, false, false);
            }

            if (id == R.id.btn_bar_distortion) {
                kwDistortionView.setOnEventListener(onEventListener);
                kwDistortionView.initDistortionListView();
                setViewVisual(false, true, false, false, false);
            }

            if (id == R.id.btn_bar_points) {
                kwFaceBeautyView.setOnEventListener(onEventListener);
                kwFaceBeautyView.setVisibility(VISIBLE);
                setViewVisual(false, false, false, true, false);
            }

            if (id == R.id.btn_bar_beauty_second) {
                kwFaceBeauty2.setVisibility(VISIBLE);
                kwFaceBeauty2.setOnEventListener(onEventListener);
                kwFaceBeauty2.initBeautyAdjustView();
                setViewVisual(false, false, false, false, true);
            }

        }
    };


    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public KwEffectView(Context context) {
        super(context);
        init(null, 0);
    }

    public KwEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwEffectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // 加载布局
        LayoutInflater.from(getContext()).inflate(R.layout.sticker_layout, this);

        kwStickerView = (kwStickerView) findViewById(R.id.sticker_view);
        kwDistortionView = (KwDistortionView) findViewById(R.id.distortion_view);
        KwEyeAndThinView = (KwEyeAndThinView) findViewById(R.id.eye_thin_view);
        kwFaceBeautyView = (KwFaceBeautyView) findViewById(R.id.face_beauty_view);
        kwFaceBeauty2 = (KwFaceBeauty2View) findViewById(R.id.face_beauty2_view);

        mBottomView = (RadioGroup) findViewById(R.id.bottom_view);
        mBottomView.setOnCheckedChangeListener(radioListener);


        kwFaceBeautyView.setOnItemSelectListener(new KwFaceBeautyView.OnItemSelectListener() {
            /**
             * 如果全局美颜1开启， 关闭全局美颜2
             *
             * @param open
             */
            @Override
            public void onItemSelect(boolean open) {
                if (open) {
                    onEventListener.onSwitchBeauty2(false);
                    kwFaceBeauty2.closeBeauty2();
                }
            }
        });

        kwFaceBeauty2.setOnSwitchBeautyListener(new KwFaceBeauty2View.OnSwitchBeauty2() {
            /**
             * 如果全局美颜2开启，关闭全局美颜1
             * @param open
             */
            @Override
            public void onSwitchBeauty(boolean open) {
                if (open) {
                    onSwitchBeauty(false);
                    kwFaceBeautyView.closeBeauty();
                }
            }
        });
    }


    private void setViewVisual(boolean stickerVisual, boolean distortionVisual, boolean beautyVisual, boolean skinwhite,
                               boolean skinWhiteSecond) {
        kwStickerView.setVisibility(stickerVisual ? VISIBLE : GONE);
        kwDistortionView.setVisibility(distortionVisual ? VISIBLE : GONE);
        KwEyeAndThinView.setVisibility(beautyVisual ? VISIBLE : GONE);
        kwFaceBeautyView.setVisibility(skinwhite ? VISIBLE : GONE);
        kwFaceBeauty2.setVisibility(skinWhiteSecond ? VISIBLE : GONE);
    }

}

