package com.kiwi.ui;

import com.kiwi.tracker.KwFilterType;
import com.kiwi.tracker.bean.Filter;
import com.kiwi.tracker.bean.conf.StickerConfig;

/**
 * Created by shijian on 08/12/2016.
 */

public interface OnViewEventListener {
    void onTakeShutter();

    void onSwitchCamera();

    void onStickerChanged(StickerConfig stickerConfig);

    void onSwitchBeauty(boolean enable);

    void onSwitchBeautyFace(boolean enable);

    void onDistortionChanged(KwFilterType filterType);

    void onAdjustFaceBeauty(int type, float param);

    void onFaceBeautyLevel(float level);

    void onSwitchBeauty2(boolean enable);

    void onSwitchFilter(Filter filter);
}