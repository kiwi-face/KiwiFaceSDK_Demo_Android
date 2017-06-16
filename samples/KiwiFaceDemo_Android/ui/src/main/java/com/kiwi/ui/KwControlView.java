package com.kiwi.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiwi.tracker.bean.Filter;
import com.kiwi.ui.adapter.FilterAdapter;
import com.kiwi.ui.model.FilterConfigMgr;

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
    public static final float LEVEL1 = 1.0f;
    public static final float LEVEL2 = 2.0f;
    public static final float LEVEL3 = 3.0f;

    private LinearLayout mToolBarLayout;
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private ImageView mFilter;
    private ImageView mSticker;
    private ImageView mShutter;
    private RelativeLayout mRlempty;
    private OnViewEventListener onEventListener;
    private TextView mFpsTextView;
    private ImageView mSwitchCamera;
    private KwEffectView kwEffectView;

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
        // 加载布局
        LayoutInflater.from(getContext()).inflate(getContentLayoutId(), this);

        mToolBarLayout = (LinearLayout) findViewById(R.id.layout_toolbar);
        mFilterLayout = (LinearLayout) findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        mFpsTextView = (TextView) findViewById(R.id.text_fps);


        mFilter = (ImageView) findViewById(R.id.btn_camera_filter);
        mSticker = (ImageView) findViewById(R.id.btn_camera_sticker);
        mShutter = (ImageView) findViewById(R.id.btn_camera_shutter);
        mRlempty = (RelativeLayout) findViewById(R.id.layout_empty);
        mSwitchCamera = (ImageView) findViewById(R.id.btn_camera_switch);

        kwEffectView = (KwEffectView) findViewById(R.id.layout_sticker);
        initFilterListView();

        mFilter.setOnClickListener(this);
        mSticker.setOnClickListener(this);
        mShutter.setOnClickListener(this);
        mRlempty.setOnClickListener(this);
        mSwitchCamera.setOnClickListener(this);
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
                public void onFilterChanged(Filter filter) {
                    onEventListener.onSwitchFilter(filter);
                }
            });

            mToolBarLayout.setVisibility(GONE);
            mFilterLayout.setVisibility(View.VISIBLE);
        }
        /*贴纸以及美颜、瘦脸*/
        if (v.getId() == R.id.btn_camera_sticker) {
            mToolBarLayout.setVisibility(GONE);
            kwEffectView.setVisibility(VISIBLE);
            kwEffectView.setOnEventListener(onEventListener);
            findViewById(R.id.btn_bar_sticker).performClick();
        }
        /*中间按钮*/
        if (v.getId() == R.id.btn_camera_shutter) {
            onEventListener.onTakeShutter();
        }

        /*没有控件部分*/
        if (v.getId() == R.id.layout_empty) {
            kwEffectView.setVisibility(GONE);
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
}
