package com.kiwi.camera.utils.camera;

import android.hardware.Camera;

/**
 * Created by shijian on 17/12/2016.
 */

public interface CameraHelperImpl {
    int getNumberOfCameras();

    Camera openCamera(int id);

    Camera openDefaultCamera();

    Camera openCameraFacing(int facing);

    boolean hasCamera(int cameraFacingFront);

    void getCameraInfo(int cameraId, CameraInfo2 cameraInfo);
}