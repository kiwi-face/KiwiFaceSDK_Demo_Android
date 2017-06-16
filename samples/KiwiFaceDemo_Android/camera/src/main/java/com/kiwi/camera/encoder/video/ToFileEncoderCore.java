package com.kiwi.camera.encoder.video;

import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by shijian on 10/01/2017.
 */

public class ToFileEncoderCore extends VideoEncoderCore {
        private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     *
     * @param width
     * @param height
     * @param bitRate
     * @param outputFile
     */
    public ToFileEncoderCore(int width, int height, int bitRate, File outputFile) throws IOException {
        super(width, height, bitRate);
        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        mMuxer = new MediaMuxer(outputFile.toString(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    public void release() {
        super.release();
        if (mMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    protected void onOutputFormatChanged() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new RuntimeException("format changed twice");
        }

        MediaFormat newFormat = mEncoder.getOutputFormat();
        Log.d(TAG, "encoder output format changed: " + newFormat);

        // now that we have the Magic Goodies, start the muxer
        mTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
    }

    protected void output(ByteBuffer encodedData) {
        if (!mMuxerStarted) {
            throw new RuntimeException("muxer hasn't started");
        }

        // adjust the ByteBuffer values to match BufferInfo (not needed?)
        encodedData.position(mBufferInfo.offset);
        encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

        mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
        if (VERBOSE) {
            Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                    mBufferInfo.presentationTimeUs);
        }
    }
}
