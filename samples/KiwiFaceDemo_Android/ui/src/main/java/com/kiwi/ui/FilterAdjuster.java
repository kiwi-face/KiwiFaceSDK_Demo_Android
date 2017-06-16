package com.kiwi.ui;

import android.widget.SeekBar;

/**
 * Created by song.ding on 2017/1/24.
 */
public class FilterAdjuster {
    private String title;
    private SeekBar seekBar;
    private int type;
    private float start;
    private float end;
    private float def;

    public FilterAdjuster(String title, int type, float min, float max, float defV) {
        this.title = title;
        this.type = type;
        this.start = min;
        this.end = max;
        this.def = defV;
    }

    public int getDefaultValue() {
        if (end > start) {
            return (int) ((this.def - this.start) * 100f / (end - start));
        } else {
            return (int) ((this.start - this.def) * 100f / (start - end));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public int getType() {
        return type;
    }

    public float getValue(int progress) {
        return (end - start) * progress / 100.0f + start;
    }
}
