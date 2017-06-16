package com.kiwi.camera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;

import com.kiwi.filter.utils.OpenGlUtils;
import com.kiwi.tracker.gles.FBO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by song.ding on 2017/2/27.
 */

public class TakePictureFBO extends FBO {
    private OnTakePhotoListener listener;
    protected boolean mEnable = true;
    private ByteBuffer buf;
    protected Context mContext;
    private static String SDpath = Environment.getExternalStorageDirectory().getPath();

    public interface OnTakePhotoListener {
        void onTakeSuccess(String path, byte[] data);
    }

    public void setListener(OnTakePhotoListener listener) {
        this.listener = listener;
    }

    public TakePictureFBO(int inputTextureType) {
        super(inputTextureType);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
    }

    @Override
    public int drawFrame(int texId, int texWidth, int texHeight) {
        return super.drawFrame(texId, texWidth, texHeight);
    }


    @Override
    public int drawFrame(int texId, int texWidth, int texHeight, FloatBuffer vertexArray, FloatBuffer texCoordArray) {
        GLES20.glDisable(GL10.GL_CULL_FACE);
        if (!mEnable) {
            return texId;
        }
        GLES20.glViewport(0, 0, texWidth, texHeight);
        if (mOffscreenTexture == 0) {
            prepareFramebuffer(texWidth, texHeight);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        if (null == mFullScreen.getFilter()) {
            changeFilter(createFilter(this.mContext));
        }
        mFullScreen.getFilter().setTextureSize(texWidth, texHeight);
        mFullScreen.drawFrame(texId, vertexArray, texCoordArray);
        glReadPixelsFBO(texWidth, texHeight);
        // Blit to display.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        return mOffscreenTexture;
    }

    public byte[] glReadPixelsFBO(int getWidth, int getHeight) {
        if (buf == null) {
            buf = ByteBuffer.allocateDirect(getWidth * getHeight * 4);
        }
        //目前只支持RGBA格式
        buf.order(ByteOrder.nativeOrder());
        buf.position(0);
        GLES20.glReadPixels(0, 0, getWidth, getHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        OpenGlUtils.checkGlError("glReadPixels");
        buf.rewind();
        saveBitmap(getWidth, getHeight, buf);
        byte[] array = buf.array();
        return array;
    }


    public void saveBitmap(int width, int height, ByteBuffer bf) {
        //根据需要自己调节图片的大小，如果卡顿将质量调低即可
//        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(bf);
        String facePath = getTempPath();
        new File(facePath).mkdirs();
        String path = facePath + System.currentTimeMillis() + ".png";
        boolean isSuccess = saveBitmap(result, new File(path));

        if (isSuccess) {
            //图片保存成功  可根据需要返回图片的byte[]或者bitmap地址
            byte[] array = buf.array();
            listener.onTakeSuccess(path, array);
        }
        Log.e("sys", "saveBitmap,path:" + path);
    }

    public static String getTempPath() {
        File file = new File(SDpath + "/KiwiMp4");
        if (!file.exists()) {
            file.mkdir();
        }
        return SDpath + "/KiwiMp4" + File.separator;
    }

    private static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null)
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
