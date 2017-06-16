package com.kiwi.camera.encoder.video;

import android.media.MediaCodec;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by shijian on 10/01/2017.
 */

public class ToH264EncoderCore extends VideoEncoderCore {

    public interface OnOutputH264Listener {
        void onOutputStream(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo);
    }

    private OnOutputH264Listener onOutputStreamListener;

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     *
     * @param width
     * @param height
     * @param bitRate
     */
    public ToH264EncoderCore(int width, int height, int bitRate, OnOutputH264Listener onOutputStreamListener) throws IOException {
        super(width, height, bitRate);
        this.onOutputStreamListener = onOutputStreamListener;
    }

    @Override
    protected void output(ByteBuffer encodedData) {
        if(onOutputStreamListener != null){
            // adjust the ByteBuffer values to match BufferInfo (not needed?)
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            onOutputStreamListener.onOutputStream(encodedData,mBufferInfo);
            if (VERBOSE) {
                Log.d(TAG, "sent " + mBufferInfo.size + " bytes to h264, ts=" +
                        mBufferInfo.presentationTimeUs);
            }
        }

    }

    @Override
    protected void onOutputFormatChanged() {

    }
}
