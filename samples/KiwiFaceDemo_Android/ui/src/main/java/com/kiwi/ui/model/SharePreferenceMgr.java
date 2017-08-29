package com.kiwi.ui.model;

import com.blankj.utilcode.utils.SPUtils;

/**
 * Created by shijian on 2017/5/3.
 */

public class SharePreferenceMgr {

    public static SharePreferenceMgr getInstance() {
        return instance;
    }

    private static SharePreferenceMgr instance = new SharePreferenceMgr();

    private SPUtils beautyConfig;
    private SharePreferenceMgr(){
        beautyConfig = new SPUtils("beautyConfig");
    }

    public boolean isBeautyEnabled(){
        return beautyConfig.getBoolean("beautyEnabled",true);
    }

    public void setBeautyEnabled(boolean enabled){
        beautyConfig.putBoolean("beautyEnabled",enabled);
    }

    public int getSkinWhite(){
        return beautyConfig.getInt("skinPerfection",50);
    }

    public void setSkinPerfection(int value){
        beautyConfig.putInt("skinPerfection",value);
    }


    public int getSkinRemoveBlemishes(){
        return beautyConfig.getInt("skinRemoveBlemishes",50);
    }

    public void setSkinRemoveBlemishes(int value){
        beautyConfig.putInt("skinRemoveBlemishes",value);
    }

    public int getSkinSaturation(){
        return beautyConfig.getInt("skinSaturation",50);
    }

    public void setSkinSaturation(int value){
        beautyConfig.putInt("skinSaturation",value);
    }

    public int getSkinTenderness(){
        return beautyConfig.getInt("skinTenderness",50);
    }

    public void setSkinTenderness(int value){
        beautyConfig.putInt("skinTenderness",value);
    }

    public boolean isLocalBeautyEnabled() {
        return  beautyConfig.getBoolean("localBeautyEnbaled",false);
    }

    public void setLocalBeautyEnabled(boolean isOpen) {
        beautyConfig.putBoolean("localBeautyEnbaled",isOpen);
    }

    public int getBigEye() {
        return beautyConfig.getInt("bigEye",50);
    }

    public int getThinFace() {
        return beautyConfig.getInt("thinFace",50);
    }

    public void setBigEye(int progress) {
        beautyConfig.putInt("bigEye",progress);
    }

    public void setThinFace(int progress) {
        beautyConfig.putInt("thinFace",progress);
    }


}
