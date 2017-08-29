package com.kiwi.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.utils.Utils;
import com.kiwi.tracker.bean.KwFilter;
import com.kiwi.ui.adapter.FilterAdapter;
import com.kiwi.ui.model.FilterConfigMgr;
import com.kiwi.ui.widget.EyeAndThinView;
import com.kiwi.ui.widget.FaceBeauty2View;
import com.kiwi.ui.widget.StickerView;

/**
 * Created by song.ding on 2017/2/3.
 */

public class KwControlView extends FrameLayout implements View.OnClickListener {

    public static final int BEAUTY_BIG_EYE_TYPE = 0;  //大眼
    public static final int BEAUTY_THIN_FACE_TYPE = 1;//瘦脸

    public static final int SKIN_TONE_PERFECTION = 2; //美白  全局美颜2
    public static final int REMOVE_BLEMISHES = 3;//磨皮
    public static final int SKIN_TONE_SATURATION = 4;//饱和
    public static final int SKIN_SHINNING_TENDERNESS = 5;  //粉嫩

    private TextView mFpsTextView;

    //底部三个按钮
    private LinearLayout mToolBarLayout;

    //滤镜面板
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;

    //特效面板
    private View mEffectView;
    private com.kiwi.ui.widget.StickerView mStickerView;
    private com.kiwi.ui.widget.EyeAndThinView mEyeAndThinView;
    private FaceBeauty2View mFaceBeauty2View;

    private OnViewEventListener onEventListener;

    public KwControlView(Context context) {
        super(context);
        init(null, 0);
    }

    public KwControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * 初始化加载布局
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        //初始化
        Utils.init(getContext());

        // 加载布局
        LayoutInflater.from(getContext()).inflate(getContentLayoutId(), this);

        mToolBarLayout = (LinearLayout) findViewById(R.id.layout_toolbar);
        mFilterLayout = (LinearLayout) findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mFpsTextView = (TextView) findViewById(R.id.text_fps);

        findViewById(R.id.btn_camera_filter).setOnClickListener(this);
        findViewById(R.id.btn_camera_effect).setOnClickListener(this);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(this);
        findViewById(R.id.layout_empty).setOnClickListener(this);
        findViewById(R.id.btn_camera_switch).setOnClickListener(this);


        initFilterListView();
        initEffectView();
    }


    protected int getContentLayoutId() {
        return R.layout.control_layout;
    }


    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        /*滤镜*/
        if (v.getId() == R.id.btn_camera_filter) {
            FilterAdapter mAdapter = new FilterAdapter(getContext(), FilterConfigMgr.getFilters());
            mFilterListView.setAdapter(mAdapter);
            mAdapter.setOnFilterChangeListener(new FilterAdapter.onFilterChangeListener() {
                @Override
                public void onFilterChanged(KwFilter filter) {
                    onEventListener.onSwitchFilter(filter);
                }
            });

            mToolBarLayout.setVisibility(GONE);
            mFilterLayout.setVisibility(View.VISIBLE);
        }
        /*贴纸以及美颜、瘦脸*/
        if (v.getId() == R.id.btn_camera_effect) {
            mToolBarLayout.setVisibility(GONE);
            mEffectView.setVisibility(VISIBLE);
            findViewById(R.id.btn_bar_sticker).performClick();
        }
        /*中间按钮*/
        if (v.getId() == R.id.btn_camera_shutter) {
            onEventListener.onTakeShutter();
        }

        /*没有控件部分*/
        if (v.getId() == R.id.layout_empty) {
            mEffectView.setVisibility(GONE);
            mFilterLayout.setVisibility(View.GONE);
            mToolBarLayout.setVisibility(View.VISIBLE);
        }
         /*切换摄像头*/
        if (v.getId() == R.id.btn_camera_switch) {
            onEventListener.onSwitchCamera();
        }

    }

    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public void setFps(final int fps) {
        mFpsTextView.post(new Runnable() {
            @Override
            public void run() {
                mFpsTextView.setText("FPS:" + fps);
            }
        });
    }


    /**
     * 滤镜
     */
    private void initFilterListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);
    }

    private void initEffectView() {
        mEffectView = findViewById(R.id.layout_effect);

        mStickerView = (StickerView) findViewById(R.id.sticker_view);
        mEyeAndThinView = (EyeAndThinView) findViewById(R.id.eye_thin_view);
        mFaceBeauty2View = (FaceBeauty2View) findViewById(R.id.face_beauty2_view);

        ((RadioGroup) findViewById(R.id.bottom_view)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                if (id == R.id.btn_bar_beauty) {
                    mEyeAndThinView.setOnEventListener(onEventListener);
                    mEyeAndThinView.setVisibility(VISIBLE);
                    setViewVisual(false, true, false);
                }

                if (id == R.id.btn_bar_sticker) {
                    mStickerView.setOnEventListener(onEventListener);
                    mStickerView.initStickerListView();
                    setViewVisual(true, false, false);
                }

                if (id == R.id.btn_bar_beauty_second) {
                    mFaceBeauty2View.setVisibility(VISIBLE);
                    mFaceBeauty2View.setOnEventListener(onEventListener);
                    setViewVisual(false, false, true);
                }

            }
        });
    }


    private void setViewVisual(boolean stickerVisual, boolean beautyVisual,
                               boolean skinWhiteSecond) {
        mStickerView.setVisibility(stickerVisual ? VISIBLE : GONE);
        mEyeAndThinView.setVisibility(beautyVisual ? VISIBLE : GONE);
        mFaceBeauty2View.setVisibility(skinWhiteSecond ? VISIBLE : GONE);
    }

}
