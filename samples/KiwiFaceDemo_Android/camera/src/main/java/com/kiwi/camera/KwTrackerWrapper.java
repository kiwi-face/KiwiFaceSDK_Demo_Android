package com.kiwi.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.utils.ImageUtils;
import com.kiwi.sticker.StickerMgr;
import com.kiwi.tracker.KwFaceTracker;
import com.kiwi.tracker.KwFilterType;
import com.kiwi.tracker.KwTrackerContext;
import com.kiwi.tracker.KwTrackerManager;
import com.kiwi.tracker.KwTrackerSettings;
import com.kiwi.tracker.bean.Filter;
import com.kiwi.tracker.bean.KwTrackResult;
import com.kiwi.tracker.bean.WaterMarkSettings;
import com.kiwi.tracker.bean.conf.StickerConfig;
import com.kiwi.tracker.common.Config;
import com.kiwi.ui.OnViewEventListener;
import com.kiwi.ui.helper.KwResourceHelper;

import static com.kiwi.ui.KwControlView.BEAUTY_BIG_EYE_TYPE;
import static com.kiwi.ui.KwControlView.BEAUTY_THIN_FACE_TYPE;
import static com.kiwi.ui.KwControlView.REMOVE_BLEMISHES;
import static com.kiwi.ui.KwControlView.SKIN_SHINNING_TENDERNESS;
import static com.kiwi.ui.KwControlView.SKIN_TONE_PERFECTION;
import static com.kiwi.ui.KwControlView.SKIN_TONE_SATURATION;

/**
 * Created by shijian on 2016/9/28.
 */

public class KwTrackerWrapper {
    private static final String TAG = KwTrackerWrapper.class.getName();
    private KwTrackResult EMPTY_TRACK_RESULT = new KwTrackResult(KwFaceTracker.KW_E_NO_DETECT_FACE);


    interface UIClickListener {

        void onTakeShutter();

        void onSwitchCamera();

    }

    private KwTrackerSettings kwTrackerSetting;
    private KwTrackerManager kwTrackerManager;
    private KwTrackerContext kwTrackerContext = new KwTrackerContext();

    public KwTrackerWrapper(final Context context, int cameraFaceId) {

        kwTrackerSetting = new KwTrackerSettings().
                setBeauty2Enabled(true).
                setBeautySettings2(new KwTrackerSettings.BeautySettings2(0.7f, 0.365f, 0.20f, 0.12f)).
                setCameraFaceId(cameraFaceId);

        kwTrackerManager = new KwTrackerManager(context).
                setTrackerSetting(kwTrackerSetting).
                setStickerMgr(new StickerMgr())
                .setTrackerContext(kwTrackerContext)
                .build();

        //copy assets config/sticker/filter to sdcard
        KwResourceHelper.copyResource2SD(context);

        initKiwiConfig();

        int id = R.drawable.kiwi_logo;
        int gravity = WaterMarkSettings.Gravity.BOTTOM | WaterMarkSettings.Gravity.RIGHT;
        initWaterMark(context, id, gravity, 140, 80, 10, 10);
    }

    private void initWaterMark(Context context, int id, int gravity, int width, int height, int verticalMargin, int horizontalMargin) {
        Bitmap waterMark = ImageUtils.getBitmap(context.getResources(), id);
        kwTrackerSetting.setWaterMarkSettings(new WaterMarkSettings(true, waterMark, width, height, gravity, verticalMargin, horizontalMargin));
    }

    private void initKiwiConfig() {
        //推荐配置，以下情况，选择性能优先模式
        //1.oppo vivo你懂的
        //2.小于5.0的机型可能配置比较差
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        Log.i(TAG, String.format("manufacturer:%s,model:%s,sdk:%s", manufacturer, Build.MODEL, Build.VERSION.SDK_INT));
        boolean isOppoVivo = manufacturer.contains("oppo") || manufacturer.contains("vivo");
        Log.i(TAG, "initKiwiConfig buildVersion" + Build.VERSION.RELEASE);
        if (isOppoVivo || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Config.TRACK_MODE = Config.TRACK_PRIORITY_PERFORMANCE;
        }

        //关闭日志打印,release版本请务必关闭日志打印
        Config.isDebug = false;
    }


    public void onCreate(Activity activity) {
        kwTrackerContext.onCreate(activity);

    }

    public void onResume(Activity activity) {
        kwTrackerContext.onResume(activity);
    }

    public void onPause(Activity activity) {
        kwTrackerContext.onPause(activity);
    }

    public void onDestroy(Activity activity) {
        kwTrackerContext.onDestory(activity);
    }

    public void onSurfaceCreated(Context context) {
        kwTrackerContext.onSurfaceCreated(context);
    }

    public void onSurfaceChanged(int width, int height, int previewWidth, int previewHeight) {
        kwTrackerContext.onSurfaceChanged(width, height, previewWidth, previewHeight);
    }

    public void onSurfaceDestroyed() {
        kwTrackerContext.onSurfaceDestroyed();
    }

    public void switchCamera(int ordinal) {
        kwTrackerManager.switchCamera(ordinal);
    }

    /**
     * 对纹理进行特效处理（美颜、大眼瘦脸、人脸贴纸、哈哈镜、滤镜）
     * 优点：cpu/内存 占用低，高通/三星 gpu兼容性好
     * 缺点：该方法对gpu有些要求，早期mali gpu的性能可能导致卡顿
     *
     * @param texId     YUV格式纹理
     * @param texWidth  纹理宽度
     * @param texHeight 纹理高度
     * @return 特效处理后的纹理
     */
    public int onDrawOESTexture(int texId, int texWidth, int texHeight) {

        //解开绑定
        int newTexId = texId;
        int maxFaceCount = 1;
        int filterTexId = kwTrackerManager.onDrawOESTexture(texId, texWidth, texHeight, maxFaceCount);
        if (filterTexId != -1) {
            newTexId = filterTexId;
        }
        GLES20.glGetError();//请勿删除当前行获取opengl错误代码
        return newTexId;
    }

    /**
     * 对纹理进行特效处理（美颜、大眼瘦脸、人脸贴纸、哈哈镜、滤镜）
     * 优点：对gpu要求低
     * 缺点：cpu/内存 占用高
     *
     * @param nv21        nv21 preview data
     * @param texId       external texture id
     * @param texWidth    texture width
     * @param texHeight   texture height
     * @param orientation texture orientation
     * @return output texture id
     */
    public int onDrawOESTexture(byte[] nv21, int texId, int texWidth, int texHeight, int orientation) {

        long start = System.currentTimeMillis();

        //解开绑定
        int newTexId = texId;
        int maxFaceCount = 1;

        KwTrackResult kwTrackResult = EMPTY_TRACK_RESULT;
        if (kwTrackerSetting.isNeedTrack()) {
            kwTrackResult = kwTrackerManager.track(nv21, KwFaceTracker.KW_FORMAT_NV21, texWidth, texHeight, maxFaceCount, orientation);
            if (Config.isDebug) Log.i(TAG, "track cost:" + (System.currentTimeMillis() - start));
        }

        int filterTexId = kwTrackerManager.onDrawOESTexture(texId, texWidth, texHeight, kwTrackResult);
        if (filterTexId != -1) {
            newTexId = filterTexId;
        }

        GLES20.glGetError();//请勿删除当前行获取opengl错误代码

        if (Config.isDebug)
            Log.i(TAG, "onDrawOESTexture cost:" + (System.currentTimeMillis() - start));
        return newTexId;
    }

    /**
     * UI事件处理类
     *
     * @param uiClickListener
     * @return
     */
    public OnViewEventListener initUIEventListener(final UIClickListener uiClickListener) {
        OnViewEventListener eventListener = new OnViewEventListener() {

            @Override
            public void onSwitchBeauty2(boolean enable) {
                getKwTrackerManager().setBeauty2Enabled(enable);
            }

            @Override
            public void onTakeShutter() {
                uiClickListener.onTakeShutter();
            }

            @Override
            public void onSwitchCamera() {
                uiClickListener.onSwitchCamera();
            }

            @Override
            public void onSwitchFilter(Filter filter) {
                getKwTrackerManager().switchFilter(filter);
            }

            @Override
            public void onStickerChanged(StickerConfig item) {
                getKwTrackerManager().switchSticker(item);
            }

            @Override
            public void onSwitchBeauty(boolean enable) {
                getKwTrackerManager().setBeautyEnabled(enable);
            }

            @Override
            public void onSwitchBeautyFace(boolean enable) {
                getKwTrackerManager().setBeautyFaceEnabled(enable);
            }

            @Override
            public void onDistortionChanged(KwFilterType filterType) {
                getKwTrackerManager().switchDistortion(filterType);
            }

            @Override
            public void onAdjustFaceBeauty(int type, float param) {
                switch (type) {
                    case BEAUTY_BIG_EYE_TYPE:
                        getKwTrackerManager().adjustFaceBigEyeScale(param);
                        break;
                    case BEAUTY_THIN_FACE_TYPE:
                        getKwTrackerManager().adjustFaceThinFaceScale(param);
                        break;
                    case SKIN_SHINNING_TENDERNESS:
                        //粉嫩
                        getKwTrackerManager().adjustSkinShinningTenderness(param);
                        break;
                    case SKIN_TONE_SATURATION:
                        //饱和
                        getKwTrackerManager().adjustSkinToneSaturation(param);
                        break;
                    case REMOVE_BLEMISHES:
                        //磨皮
                        getKwTrackerManager().adjustRemoveBlemishes(param);
                        break;
                    case SKIN_TONE_PERFECTION:
                        //美白
                        getKwTrackerManager().adjustSkinTonePerfection(param);
                        break;
                }

            }

            @Override
            public void onFaceBeautyLevel(float level) {
                getKwTrackerManager().adjustBeauty(level);
            }

        };

        return eventListener;
    }

    public KwTrackerManager getKwTrackerManager() {
        return kwTrackerManager;
    }


}
