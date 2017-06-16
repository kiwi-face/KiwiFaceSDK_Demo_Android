package com.kiwi.ui.utils;

/**
 * 计算帧率
 */
public class FPSCounter {
    public interface OnFpsChangeListener{
        void onChange(int fps);
    }

   private long startTime = System.nanoTime();
   private int frames = 0;
    private OnFpsChangeListener onFpsChangeListener;

    public void logFrame() {
        frames++;
        long now = System.nanoTime();
        if(now - startTime >= 1000000000) {
            //Log.d("FPSCounter", "fps: " + frames);
            if(onFpsChangeListener!= null){
                onFpsChangeListener.onChange(frames);
            }
            frames = 0;
            startTime = now;
        }
    }

    public void setOnFpsChangeListener(OnFpsChangeListener onFpsChangeListener) {
        this.onFpsChangeListener = onFpsChangeListener;
    }

}
