package com.kiwi.camera;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kiwi.camera.utils.camera.CameraHelper;
import com.kiwi.camera.utils.camera.CameraInfo2;
import com.kiwi.camera.widget.CameraSurfaceView;
import com.kiwi.ui.KwControlView;
import com.kiwi.ui.utils.FPSCounter;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity implements CameraSurfaceView.ICallActivity {
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter;
    private ImageView btn_mode;

    private ObjectAnimator animator;

    private KwTrackerWrapper kwTrackerWrapper;
    private CameraSurfaceView cameraSurfaceView;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;

    private boolean isRecording = false;
    private FPSCounter fpsCounter;
    public int height;
    public int width;
    private KwControlView kwControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.glsurfaceview_camera);
        initView();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        height = metric.heightPixels;
        width = metric.widthPixels;

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();

        kwTrackerWrapper = new KwTrackerWrapper(this, mCamera.mCurrentCameraId);
        kwTrackerWrapper.onCreate(this);

        kwControlView = (KwControlView) findViewById(R.id.camera_control_view);
        kwControlView.setOnEventListener(kwTrackerWrapper.initUIEventListener(
                new KwTrackerWrapper.UIClickListener() {

                    @Override
                    public void onTakeShutter() {
                        takeShutter(kwControlView);
                    }

                    @Override
                    public void onSwitchCamera() {
                        mCamera.switchCamera();
                        kwTrackerWrapper.switchCamera(mCamera.mCurrentCameraId);
                    }
                }));


        fpsCounter = new FPSCounter();
        fpsCounter.setOnFpsChangeListener(new FPSCounter.OnFpsChangeListener() {
            @Override
            public void onChange(int fps) {
                kwControlView.setFps(fps);
            }
        });
        cameraSurfaceView.setFpsCounter(fpsCounter);

        cameraSurfaceView.setKwTrackerWrapper(kwTrackerWrapper);

    }

    @Override
    protected void onResume() {
        super.onResume();
        kwTrackerWrapper.onResume(this);
        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        kwTrackerWrapper.onPause(this);
        mCamera.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kwTrackerWrapper.onDestroy(this);
    }

    private void initView() {
        btn_shutter = (ImageView) findViewById(R.id.btn_camera_shutter);
        btn_mode = (ImageView) findViewById(R.id.btn_camera_mode);

        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);

        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takeVideo();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_camera_mode:
                    switchMode();
                    break;
                case R.id.btn_camera_shutter:
                    takeShutter(v);
                    break;
            }
        }
    };

    private void takeShutter(View v) {
        if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    v.getId());
        } else {
            takeVideo();
        }
    }


    private void switchMode() {
        if (mode == MODE_PIC) {
            mode = MODE_VIDEO;
            btn_mode.setImageResource(R.drawable.icon_camera);
        } else {
            mode = MODE_PIC;
            btn_mode.setImageResource(R.drawable.icon_video);
        }
    }

    @Override
    public Camera.Size getCameraPreviewSize() {
        return mCamera.getPreviewSize();
    }


    private class CameraLoader {

        private int mCurrentCameraId;
        private Camera mCameraInstance;

        public CameraLoader(){
            if(Camera.getNumberOfCameras() > 1){
                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }else {
                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            Point surfaceSize = getSurfaceViewSize();
            //预览图像过大会很消耗性能，建议根据机型进行控制
            Camera.Size size = mCameraHelper.getOptimalPreviewSize(mCameraInstance, surfaceSize.y, surfaceSize.x, 799);
            //性能相关参数，省电模式，可以调小预览输出
            parameters.setPreviewSize(size.width, size.height);
            int fps[] = mCameraHelper.getPhotoPreviewFpsRange(parameters);
            Log.d("Tracker","setPreviewFpsRange:" + fps[0] + " " + fps[1]);
            parameters.setPreviewFpsRange(fps[0], fps[1]);
            // Update camera parameters.

//增加摄像头防抖
//            Log.i("Tracker", "isVideoStabilizationSupported: " +
//                    parameters.isVideoStabilizationSupported());
//            if (parameters.isVideoStabilizationSupported()) {
//                parameters.setVideoStabilization(true);
//            }

//            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//            parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
//            if(parameters.isAutoExposureLockSupported()) {
//                parameters.setAutoExposureLock(false);
//            }

//            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
//
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    CameraActivity.this, mCurrentCameraId);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            cameraSurfaceView.setUpCamera(mCameraInstance, orientation, false, flipHorizontal, size.width, size.height, cameraInfo.facing);
        }

        /**
         * A safe way to get an instance of the Camera object.
         */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                Log.e("Tracker", "can not open camera,id:" + id + ",error:" + e.toString());
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }

        private Camera.Size getPreviewSize() {
            return mCameraInstance.getParameters().getPreviewSize();
        }
    }

    private Point getSurfaceViewSize() {
        Point screenSize = new Point();
        if (cameraSurfaceView.getWidth() > 0 && cameraSurfaceView.getHeight() > 0) {
            screenSize.set(cameraSurfaceView.getWidth(), cameraSurfaceView.getHeight());
            return screenSize;
        }
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    private void takeVideo() {
        if (isRecording) {
            animator.end();
            cameraSurfaceView.changeRecordingState(false);
            Toast.makeText(this, "录制结束", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
            animator.start();
            cameraSurfaceView.changeRecordingState(true);
        }
        isRecording = !isRecording;
    }


}
