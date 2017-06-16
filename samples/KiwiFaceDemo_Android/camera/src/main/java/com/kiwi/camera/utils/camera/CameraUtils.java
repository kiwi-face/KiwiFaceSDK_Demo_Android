package com.kiwi.camera.utils.camera;


import android.hardware.Camera;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

public class CameraUtils {
    private static final String TAG = "CameraUtils";

    public CameraUtils() {
    }

    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if(ppsfv != null) {
            Log.d("CameraUtils", "Camera preferred preview size for video is " + ppsfv.width + "x" + ppsfv.height);
        }

        Iterator var4 = parms.getSupportedPreviewSizes().iterator();

        Camera.Size size;
        while(var4.hasNext()) {
            size = (Camera.Size)var4.next();
            Log.d("CameraUtils", "supported: " + size.width + "x" + size.height);
        }

        var4 = parms.getSupportedPreviewSizes().iterator();

        do {
            if(!var4.hasNext()) {
                Log.w("CameraUtils", "Unable to set preview size to " + width + "x" + height);
                if(ppsfv != null) {
                    parms.setPreviewSize(ppsfv.width, ppsfv.height);
                }

                return;
            }

            size = (Camera.Size)var4.next();
        } while(size.width != width || size.height != height);

        Log.d("CameraUtils", "setting preview size: " + width + "x" + height);
        parms.setPreviewSize(width, height);
    }

//    public static Size choosePreviewSize(Parameters parms, PREVIEW_SIZE_RATIO ratio, PREVIEW_SIZE_LEVEL level) {
//        List list = sortCameraPrvSize(filterCameraPrvSize(parms.getSupportedPreviewSizes(), ratio));
//        Iterator size = list.iterator();
//
//        while(size.hasNext()) {
//            Size index = (Size)size.next();
//            Log.i("CameraUtils", "after filter size.w:" + index.width + ", size.h:" + index.height);
//        }
//        Size size1;
//        switch(level) {
//            case SMALL:
//                size1 = (Size)list.get(0);
//                break;
//            case MEDIUM:
//                int index1 = 0;
//                if(list.size() - 1 >= 0) {
//                    index1 = (list.size() - 1) / 2;
//                }
//
//                size1 = (Size)list.get(index1);
//                break;
//            case LARGE:
//                size1 = (Size)list.get(list.size() - 1);
//                break;
//            default:
//                throw new IllegalArgumentException("cannot support level:" + level);
//        }
//
//        Log.i("CameraUtils", "preview size width:" + size1.width + ",height:" + size1.height);
//        parms.setPreviewSize(size1.width, size1.height);
//        return size1;
//    }
//
//    private static List<Size> sortCameraPrvSize(List<Size> list) {
//        Collections.sort(list, new Comparator<Size>() {
//            public int compare(Size a, Size b) {
//                return a.width * a.height - b.width * b.height;
//            }
//        });
//        return list;
//    }
//
//    private static List<Size> filterCameraPrvSize(List<Size> list, PREVIEW_SIZE_RATIO ratio) {
//        double ASPECT_TOLERANCE = 0.05D;
//        double targetRatio;
//        switch(ratio) {
//            case RATIO_4_3:
//                targetRatio = 1.3333333333333333D;
//                break;
//            case RATIO_16_9:
//                targetRatio = 1.7777777777777777D;
//                break;
//            default:
//                throw new IllegalArgumentException("cannot support ratio:" + ratio);
//        }
//
//        Iterator iterator = list.iterator();
//
//        while(iterator.hasNext()) {
//            Size size = (Size)iterator.next();
//            Log.i("CameraUtils", "size.width:" + size.width + ",size.height:" + size.height);
//            double r = (double)size.width / (double)size.height;
//            if(Math.abs(r - targetRatio) > 0.05D) {
//                iterator.remove();
//            }
//        }
//
//        return list;
//    }

    public static int[] chooseFixedPreviewFps(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }
}
