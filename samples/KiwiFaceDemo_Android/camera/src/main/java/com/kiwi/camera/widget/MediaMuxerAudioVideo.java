package com.kiwi.camera.widget;

import android.os.Environment;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by song.ding on 2016/12/19.
 */

public class MediaMuxerAudioVideo {


    private static String SDpath = Environment.getExternalStorageDirectory().getPath();

    public void mux(String videoPath, String audioPath) {
        try {

            File file = new File(SDpath + "/KiwiMp4");
            if (!file.exists()) {
                file.mkdir();
            }
            Movie countAudioEnglish = MovieCreator.build(audioPath);
            Track audioTrackEnglish = countAudioEnglish.getTracks().get(0);
            Movie countVideo = MovieCreator.build(videoPath);
            countVideo.addTrack(audioTrackEnglish);
            {
                Container out = new DefaultMp4Builder().build(countVideo);
                FileOutputStream fos = new FileOutputStream(new File(SDpath + "/KiwiMp4" + File.separator + "KiwiVideo" + System.currentTimeMillis() + ".mp4"));
                out.writeContainer(fos.getChannel());
                fos.close();
            }
            File fileVideo = new File(videoPath);
            if (fileVideo.exists()) {
                fileVideo.delete();
            }
            File fileAudio = new File(audioPath);
            if (fileAudio.exists()) {
                fileAudio.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
