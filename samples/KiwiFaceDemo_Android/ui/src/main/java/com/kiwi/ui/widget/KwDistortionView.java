package com.kiwi.ui.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.kiwi.tracker.KwFilterType;
import com.kiwi.ui.KwControlViewHelper;
import com.kiwi.ui.OnViewEventListener;
import com.kiwi.ui.R;
import com.kiwi.ui.adapter.DistortionAdapter;

/**
 * Created by song.ding on 2017/2/6.
 * 哈哈镜控件
 */

public class KwDistortionView extends FrameLayout {
    private RecyclerView mDistortionListView;
    private DistortionAdapter mDistortionAdapter;
    private OnViewEventListener onEventListener;

    public void setOnEventListener(OnViewEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public KwDistortionView(Context context) {
        super(context);
        init(null, 0);
    }

    public KwDistortionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KwDistortionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.distortion_view, this);
        mDistortionListView = (RecyclerView) findViewById(R.id.distortion_listView);

    }


    public void initDistortionListView() {
        //哈哈镜
        if (mDistortionAdapter == null) {
            mDistortionListView.setLayoutManager(new GridLayoutManager(getContext(), 5));
            mDistortionAdapter = new DistortionAdapter(getContext(), KwControlViewHelper.distortion_types);
            mDistortionListView.setAdapter(mDistortionAdapter);
            mDistortionAdapter.setOnFilterChangeListener(new DistortionAdapter.onFilterChangeListener() {
                @Override
                public void onFilterChanged(KwFilterType filterType) {
                    onEventListener.onDistortionChanged(filterType);
                }
            });
        }
    }
}
