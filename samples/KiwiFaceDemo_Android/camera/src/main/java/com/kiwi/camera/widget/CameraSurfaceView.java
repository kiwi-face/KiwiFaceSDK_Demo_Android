package com.kiwi.camera.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.kiwi.camera.KwTrackerWrapper;
import com.kiwi.camera.encoder.video.TextureMovieEncoder;
import com.kiwi.camera.encoder.video.ToH264EncoderCore;
import com.kiwi.filter.utils.OpenGlUtils;
import com.kiwi.filter.utils.Rotation;
import com.kiwi.tracker.KwFilterType;
import com.kiwi.tracker.common.Config;
import com.kiwi.tracker.utils.FTCameraUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.kiwi.camera.encoder.video.TextureMovieEncoder.EncoderConfig.OUTPUT_TO_FILE;
import static com.kiwi.tracker.KwFilterType.NONE;
import static com.kiwi.tracker.common.Config.isDebug;

/**
 * Created by why8222 on 2016/2/25.
 */
public class CameraSurfaceView extends BaseSurfaceView implements Camera.PreviewCallback {

    //        private int outputType = OUTPUT_TO_H264;
    private int outputType = OUTPUT_TO_FILE;
    private TackPictureTask tackPictureTask = new TackPictureTask();


    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    private KwTrackerWrapper kwTrackerWrapper;

    private SurfaceTexture mSurfaceTexture = null;
    protected int mSurfaceTextureId = OpenGlUtils.NO_TEXTURE;

    private boolean recordingEnabled;
    private int recordingStatus;

    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private static TextureMovieEncoder videoEncoder = new TextureMovieEncoder();

    private File outputFile;
    private MediaRecorder myRecorder;
    private String audioPath;
    public String videoPath;
    private boolean isTakePhoto;

    public int getmCurrentCameraId() {
        return mCurrentCameraId;
    }

    public void setmCurrentCameraId(int mCurrentCameraId) {
        this.mCurrentCameraId = mCurrentCameraId;
    }

    private int mCurrentCameraId;

    public boolean isTakePhoto() {
        return isTakePhoto;
    }

    public void setTakePhoto(boolean takePhoto) {
        isTakePhoto = takePhoto;
    }


    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);

//        String videoPath = Environment.getExternalStorageDirectory().getPath();
//        String videoName = "kw_test_video.mp4";
//        audioPath = videoPath;
//
//        outputFile = new File(videoPath, videoName);
        recordingStatus = -1;
        recordingEnabled = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        setFilter(NONE);
        kwTrackerWrapper.onSurfaceCreated(getContext());

        recordingEnabled = videoEncoder.isRecording();
        if (recordingEnabled)
            recordingStatus = RECORDING_RESUMED;
        else
            recordingStatus = RECORDING_OFF;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        kwTrackerWrapper.onSurfaceChanged(width, height, mImageWidth, mImageHeight);

        adjustImageScaling();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (mSurfaceTexture == null) {
            return;
        }

        if (fpsCounter != null) {
            fpsCounter.logFrame();
        }

        long start = System.currentTimeMillis();

        mSurfaceTexture.updateTexImage();

        record();

        int id;
        if (isTrackDataFromCamera()) {
            id = kwTrackerWrapper.onDrawOESTexture(mCameraNV21Byte, mSurfaceTextureId, mImageWidth, mImageHeight, mCameraPreviewDegree);
        } else {
            id = kwTrackerWrapper.onDrawOESTexture(mSurfaceTextureId, mImageWidth, mImageHeight);
        }

        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);

        if (filter != null) {
            filter.onDrawFrame(id, mGLCubeBuffer, mGLTextureBuffer);
        }

        if (isDebug)
            Log.i("Tracker", "[end][succ]onDrawFrame,cost:" + (System.currentTimeMillis() - start));

        if (isTakePhoto) {
            isTakePhoto = false;
            tackPictureTask.saveTexture(getContext(), id, mOutputWidth, mOutputHeight, mCurrentCameraId);
        }

        videoEncoder.setTextureId(id);
        videoEncoder.frameAvailable(mSurfaceTexture);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void record() {
        if (recordingEnabled) {
            switch (recordingStatus) {
                case RECORDING_OFF:
                    Camera.Size size = getCameraPreviewSize();
                    int previewWidth = size.width;
                    int previewHeight = size.height;
                    if (true) {
                        previewWidth = size.height;
                        previewHeight = size.width;
                    }

                    videoEncoder.setPreviewSize(previewWidth, previewHeight);
                    videoEncoder.setTextureBuffer(mGLTextureBuffer);
                    videoEncoder.setCubeBuffer(mGLCubeBuffer);

                    String sdPath = Environment.getExternalStorageDirectory().getPath();
                    String videoName = System.currentTimeMillis() + "kw_test_video.mp4";
                    videoPath = sdPath + File.separator + videoName;
                    File output = new File(sdPath, videoName);

                    if (outputType == OUTPUT_TO_FILE) {
                        //录屏
                        videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                                output, previewWidth, previewHeight,
                                1000000, EGL14.eglGetCurrentContext()));
                    } else {
                        ToH264EncoderCore.OnOutputH264Listener listener = new ToH264EncoderCore.OnOutputH264Listener() {
                            @Override
                            public void onOutputStream(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
                                Log.e("Tracker", String.format("h264 size:%s,offset:%s,time:%s,flags", bufferInfo.size, bufferInfo.offset, bufferInfo.presentationTimeUs, bufferInfo.flags));
                            }
                        };
                        //h.264编码
                        videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                                listener, previewWidth, previewHeight,
                                1000000, EGL14.eglGetCurrentContext()));
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startAudio();
                        }
                    }).start();

                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_ON:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        } else {
            switch (recordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                    videoEncoder.stopRecording();
                    myRecorder.stop();
                    recordingStatus = RECORDING_OFF;
                    //开启合成音频和视频
                    MediaMuxerAudioVideo mediaMuxerAudioVideo = new MediaMuxerAudioVideo();
                    mediaMuxerAudioVideo.mux(videoPath, audioPath);
//                    MediaMuxerAudioVideo.muxerAudio();
//                    MediaMuxerAudioVideo.comVideoAndAudio();
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown status " + recordingStatus);
            }
        }
    }


    @Override
    public void setFilter(KwFilterType type) {
        super.setFilter(type);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        kwTrackerWrapper.onSurfaceDestroyed();
    }

    public void setKwTrackerWrapper(KwTrackerWrapper kwTrackerWrapper) {
        this.kwTrackerWrapper = kwTrackerWrapper;
    }

    public void setUpCamera(final Camera camera, final int degrees, final boolean flipHorizontal,
                            final boolean flipVertical, int previewWidth, int previewHeight, int cameraFacingId) {
        mImageWidth = previewWidth;
        mImageHeight = previewHeight;

        mCameraPreviewDegree = FTCameraUtils.getOrientation(Config.getContext(), cameraFacingId);
        if (isTrackDataFromCamera()) {
            addCameraPreviewCallback(camera);
        }

        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            setUpSurfaceTexture(camera, degrees, flipHorizontal, flipVertical);
        } else {
            camera.startPreview();
            updateRotation(degrees, flipHorizontal, flipVertical);
        }
    }

    private void addCameraPreviewCallback(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        final Camera.Size previewSize = params.getPreviewSize();
        final int bitsPerPixel = ImageFormat.getBitsPerPixel(params.getPreviewFormat());
        final int previewBufferSize = (previewSize.width * previewSize.height * bitsPerPixel) / 8;
        for (int i = 0; i < MAX_CALLBACK_BUFFER_NUM; i++) {
            camera.addCallbackBuffer(new byte[previewBufferSize]);
        }
        camera.setPreviewCallbackWithBuffer(this);
    }

    private void updateRotation(int degrees, boolean flipHorizontal, boolean flipVertical) {
        setRotation(Rotation.fromInt(degrees), flipHorizontal, flipVertical);
    }

    @TargetApi(11)
    public void setUpSurfaceTexture(final Camera camera, final int degrees, final boolean flipHorizontal, final boolean flipVertical) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                Log.i("Tracker", "try to start camera preview");
                mSurfaceTextureId = OpenGlUtils.getExternalOESTextureID();
                mSurfaceTexture = new SurfaceTexture(mSurfaceTextureId);
                mSurfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);

                try {
                    camera.setPreviewTexture(mSurfaceTexture);
                    camera.startPreview();
                } catch (IOException e) {
                    Log.e("Tracker", "failed to open camera,error:" + e.toString());
                }

                updateRotation(degrees, flipHorizontal, flipVertical);
            }
        });
    }

    public void changeRecordingState(boolean isRecording) {
        recordingEnabled = isRecording;
    }

    public interface ICallActivity {
        Camera.Size getCameraPreviewSize();
    }

    private Camera.Size getCameraPreviewSize() {
        return ((ICallActivity) (getContext())).getCameraPreviewSize();
    }

    private void startAudio() {
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String audioName = System.currentTimeMillis() + "outAudio.mp4";
        audioPath = sdPath + File.separator + audioName;
        myRecorder.setOutputFile(audioPath);
        try {
            myRecorder.prepare();
        } catch (Exception e) {
            Log.e("tracker", "startAudio error:" + e);
        }
        myRecorder.start();
    }

    private int mCameraPreviewDegree;
    public static final int MAX_CALLBACK_BUFFER_NUM = 3;
    byte[] mCameraNV21Byte;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (camera == null || data == null) {
            return;
        }

        onPreviewFrameCaptured(data, mImageWidth, mImageHeight, mCameraPreviewDegree, false, ImageFormat.NV21, System.nanoTime());

        if (camera != null) {
            camera.addCallbackBuffer(data);
        }
    }

    private void onPreviewFrameCaptured(byte[] data, int mImageWidth, int mImageHeight, int mCameraPreviewDegree, boolean b, int nv21, long l) {
        mCameraNV21Byte = data;
    }

    /**
     * 从camera中输出nv21预览图像
     *
     * @return true
     * 优点：对gpu要求低，gpu兼容性好
     * 缺点：cpu/内存 占用稍高
     * <p>
     * false
     * 优点：cpu/内存 占用低
     * 缺点：该方法对gpu有些要求，早期mali gpu的性能可能导致卡顿
     */
    private boolean isTrackDataFromCamera() {

        return true;
    }
}
