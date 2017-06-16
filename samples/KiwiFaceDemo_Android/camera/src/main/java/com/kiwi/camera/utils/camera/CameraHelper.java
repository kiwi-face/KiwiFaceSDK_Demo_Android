/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiwi.camera.utils.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;

import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;
import static com.kiwi.tracker.common.Config.TAG;

public class CameraHelper {
    private final CameraHelperImpl mImpl;

    public CameraHelper(final Context context) {
        if (SDK_INT >= GINGERBREAD) {
            mImpl = new CameraHelperGB();
        } else {
            mImpl = new CameraHelperBase(context);
        }
    }



    public int getNumberOfCameras() {
        return mImpl.getNumberOfCameras();
    }

    public Camera openCamera(final int id) {
        return mImpl.openCamera(id);
    }

    public Camera openDefaultCamera() {
        return mImpl.openDefaultCamera();
    }

    public Camera openFrontCamera() {
        return mImpl.openCameraFacing(CameraInfo.CAMERA_FACING_FRONT);
    }

    public Camera openBackCamera() {
        return mImpl.openCameraFacing(CameraInfo.CAMERA_FACING_BACK);
    }

    public boolean hasFrontCamera() {
        return mImpl.hasCamera(CameraInfo.CAMERA_FACING_FRONT);
    }

    public boolean hasBackCamera() {
        return mImpl.hasCamera(CameraInfo.CAMERA_FACING_BACK);
    }

    public void getCameraInfo(final int cameraId, final CameraInfo2 cameraInfo) {
        mImpl.getCameraInfo(cameraId, cameraInfo);
    }

    public void setCameraDisplayOrientation(final Activity activity,
            final int cameraId, final Camera camera) {
        int result = getCameraDisplayOrientation(activity, cameraId);
        camera.setDisplayOrientation(result);
    }

    public int getCameraDisplayOrientation(final Activity activity, final int cameraId) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        CameraInfo2 info = new CameraInfo2();
        getCameraInfo(cameraId, info);
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    public Size getOptimalPreviewSize(Camera camera, int w, int h,int max)
    {
        Camera.Parameters parameters = camera.getParameters();
        List<Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        return getOptimalPreviewSize(mSupportedPreviewSizes, w, h,max);
    }

    public Size getOptimalPreviewSize(List<Size> sizes, int w, int h,int max) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            Log.i("Tracker","support preview size:,w:"+size.width+",h:"+size.height);

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if(size.width > max) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.i("Tracker","can not find optimalSize size");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if(size.width > max) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Log.e("Tracker","getOptimalPreviewSize,w:"+optimalSize.width+",h:"+optimalSize.height);
        return optimalSize;
    }

    public int[] getPreviewFpsRange( Camera.Parameters param,int minFrame) {
        List<int[]> supportedFPSRange = param.getSupportedPreviewFpsRange();

        for (int[] item : supportedFPSRange) {
            Log.e("Tracker","supported fps range " + item[0] + " " + item[1]);
            if(item[0] > minFrame ) {
                Log.d("Tracker","setPreviewFpsRange " + item[0] + " " + item[1]);
                return item;
            }
        }

        int from = supportedFPSRange.get(0)[0];
        int [] ret = supportedFPSRange.get(0);
        for (int[] item : supportedFPSRange) {
            if(item[0] >= from) {
                ret = item;
                from = item[0];
            }
        }

        Log.d("Tracker","setPreviewFpsRange max:" + ret[0] + " " + ret[1]);
        return ret;
    }

    /**
     * For still image capture, we need to get the right fps range such that the
     * camera can slow down the framerate to allow for less-noisy/dark
     * viewfinder output in dark conditions.
     *
     * @param params Camera's parameters.
     * @return null if no appropiate fps range can't be found. Otherwise, return
     *         the right range.
     */
    public static int[] getPhotoPreviewFpsRange(Camera.Parameters params) {
        return getPhotoPreviewFpsRange(params.getSupportedPreviewFpsRange());
    }

    // For calculate the best fps range for still image capture.
    private final static int MAX_PREVIEW_FPS_TIMES_1000 = 400000;
    private final static int PREFERRED_PREVIEW_FPS_TIMES_1000 = 30000;

    public static int[] getPhotoPreviewFpsRange(List<int[]> frameRates) {
        if (frameRates.size() == 0) {
            Log.e(TAG, "No suppoted frame rates returned!");
            return null;
        }
        // Find the lowest min rate in supported ranges who can cover 30fps.
        int lowestMinRate = MAX_PREVIEW_FPS_TIMES_1000;
        for (int[] rate : frameRates) {
            int minFps = rate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int maxFps = rate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            if (maxFps >= PREFERRED_PREVIEW_FPS_TIMES_1000 &&
                    minFps <= PREFERRED_PREVIEW_FPS_TIMES_1000 &&
                    minFps < lowestMinRate) {
                lowestMinRate = minFps;
            }
        }
        // Find all the modes with the lowest min rate found above, the pick the
        // one with highest max rate.
        int resultIndex = -1;
        int highestMaxRate = 0;
        for (int i = 0; i < frameRates.size(); i++) {
            int[] rate = frameRates.get(i);
            int minFps = rate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int maxFps = rate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            if (minFps == lowestMinRate && highestMaxRate < maxFps) {
                highestMaxRate = maxFps;
                resultIndex = i;
            }
        }
        if (resultIndex >= 0) {
            return frameRates.get(resultIndex);
        }
        Log.e(TAG, "Can't find an appropiate frame rate range!");
        return null;
    }

}
