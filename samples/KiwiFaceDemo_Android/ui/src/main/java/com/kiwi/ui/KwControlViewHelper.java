package com.kiwi.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.kiwi.tracker.KwFilterType;

import static android.view.View.GONE;

/**
 * Created by shijian on 08/12/2016.
 */

public class KwControlViewHelper {
    public static final KwFilterType[] types = new KwFilterType[]{
            KwFilterType.NONE,
//            KwFilterType.FAIRYTALE,
//            KwFilterType.SUNRISE,
//            KwFilterType.SUNSET,
//            KwFilterType.WHITECAT,
//            KwFilterType.BLACKCAT,
//            KwFilterType.SKINWHITEN,
//            KwFilterType.HEALTHY,
//            KwFilterType.SWEETS,
//            KwFilterType.ROMANCE,
//            KwFilterType.SAKURA,
//            KwFilterType.WARM,
//            KwFilterType.ANTIQUE,
//            KwFilterType.NOSTALGIA,
//            KwFilterType.CALM,
//            KwFilterType.LATTE,
//            KwFilterType.TENDER,
//            KwFilterType.COOL,
//            KwFilterType.EMERALD,
//            KwFilterType.EVERGREEN,
//            KwFilterType.CRAYON,
//            KwFilterType.SKETCH,
//            KwFilterType.AMARO,
//            KwFilterType.BRANNAN,
//            KwFilterType.BROOKLYN,
//            KwFilterType.EARLYBIRD,
//            KwFilterType.FREUD,
//            KwFilterType.HEFE,
//            KwFilterType.HUDSON,
//            KwFilterType.INKWELL,
//            KwFilterType.KEVIN,
//            KwFilterType.N1977,
//            KwFilterType.NASHVILLE,
//            KwFilterType.PIXAR,
//            KwFilterType.RISE,
//            KwFilterType.SIERRA,
//            KwFilterType.SUTRO,
//            KwFilterType.TOASTER2,
//            KwFilterType.VALENCIA,
//            KwFilterType.WALDEN,
//            KwFilterType.XPROII,

            KwFilterType.BLUEBERRY,
//            KwFilterType.DREAMY,
//            KwFilterType.HABANA,
//            KwFilterType.HAPPY,
//            KwFilterType.HARVEST,
//            KwFilterType.MISTY,
//            KwFilterType.SPRING,

            KwFilterType.COLD,
//            KwFilterType.FROG,
            KwFilterType.KD100,

            KwFilterType.DARK,
            KwFilterType.FSFP100CC,
            KwFilterType.LYON,
            KwFilterType.M106,
            KwFilterType.M109,
            KwFilterType.S114,

    };

    public static final KwFilterType[] distortion_types = new KwFilterType[]{
            KwFilterType.DISTORTION_NO,
            KwFilterType.DISTORTION_ET,
            KwFilterType.DISTORTION_PEAR_FACE,
            KwFilterType.DISTORTION_STRETCH
    };


    public static  void showLayout(final View layout){
//        if(layout.getVisibility() == View.VISIBLE) {
//            return;
//        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "translationY", layout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                layout.setVisibility(View.VISIBLE);
                layout.invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animator.start();
    }


    public static void hideLayout(final View layout, final Runnable animationEndRunable){
//        if(layout.getVisibility() == GONE) {
//            return;
//        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "translationY", 0 ,  layout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(GONE);
                if(animationEndRunable != null) {
                    animationEndRunable.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                layout.setVisibility(GONE);
                if(animationEndRunable != null) {
                    animationEndRunable.run();
                }
            }
        });
        animator.start();
    }
}
