package com.kiwi.camera.widget;


import android.content.Context;
import android.util.Log;

import com.kiwi.camera.CameraActivity;
import com.kiwi.filter.utils.Rotation;
import com.kiwi.filter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.kiwi.filter.utils.TextureRotationUtil.CUBE;
import static com.kiwi.filter.utils.TextureRotationUtil.DOWN_CUBE;
import static com.kiwi.filter.utils.TextureRotationUtil.TEXTURE_NO_ROTATION;

/**
 * Created by shijian on 2017/2/27.
 */
public class TackPictureTask {
    private TakePictureFBO rotateFBO;

    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;
    private int mCurrentCameraId;


    public void saveTexture(Context context, int id, int texWidth, int texHeight, int mCurrentCameraId) {
        this.mCurrentCameraId = mCurrentCameraId;
        //mCurrentCameraId  1  前置摄像头
        //mCurrentCameraId  0  后置摄像头
        if (null == rotateFBO) {
            rotateFBO = new TakePictureFBO(mCurrentCameraId);
            rotateFBO.initialize(context);
        }
        initBuffer();

        rotateFBO.setListener(new TakePictureFBO.OnTakePhotoListener() {
            @Override
            public void onTakeSuccess(String path, byte[] data) {
                Log.i("UI", "onTakeSuccess" + path);
            }
        });
        rotateFBO.drawFrame(id, texWidth, texHeight,
                mGLCubeBuffer, mGLTextureBuffer);
    }

    private void initBuffer() {
        //调整生成图片方向，特殊机型可自己加入判断
        if (mCurrentCameraId == 1) {
            mGLCubeBuffer = ByteBuffer.allocateDirect(DOWN_CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(DOWN_CUBE).position(0);
        } else if (mCurrentCameraId == 0) {
            mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(CUBE).position(0);
        }
        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.ROTATION_270, false, true);
        mGLTextureBuffer.put(textureCords).position(0);
    }

}
