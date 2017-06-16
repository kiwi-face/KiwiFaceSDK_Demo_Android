package com.kiwi.ui.helper;

import com.kiwi.tracker.KwFilterType;
import com.kiwi.ui.R;


public class FilterTypeHelper {

    public static int FilterType2Color(KwFilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.color.filter_color_grey_light;
            case WHITECAT:
            case BLACKCAT:
            case SUNRISE:
            case SUNSET:
                return R.color.filter_color_brown_light;
            case COOL:
                return R.color.filter_color_blue_dark;
            case EMERALD:
            case EVERGREEN:
                return R.color.filter_color_blue_dark_dark;
            case FAIRYTALE:
                return R.color.filter_color_blue;
            case ROMANCE:
            case SAKURA:
            case WARM:
                return R.color.filter_color_pink;
            case AMARO:
            case BRANNAN:
            case BROOKLYN:
            case EARLYBIRD:
            case FREUD:
            case HEFE:
            case HUDSON:
            case INKWELL:
            case KEVIN:
            case LOMO:
            case N1977:
            case NASHVILLE:
            case PIXAR:
            case RISE:
            case SIERRA:
            case SUTRO:
            case TOASTER2:
            case VALENCIA:
            case WALDEN:
            case XPROII:
                return R.color.filter_color_brown_dark;
            case ANTIQUE:
            case NOSTALGIA:
                return R.color.filter_color_green_dark;
            case SKINWHITEN:
            case HEALTHY:
                return R.color.filter_color_red;
            case SWEETS:
                return R.color.filter_color_red_dark;
            case CALM:
            case LATTE:
            case TENDER:
                return R.color.filter_color_brown;
            case BLUEBERRY:
            case DREAMY:
            case HABANA:
            case HAPPY:
            case HARVEST:
            case MISTY:
            case SPRING:
            case COLD:
            case FROG:
            case KD100:

            case DARK:
            case FSFP100CC:
            case LYON:
            case M106:
            case M109:
            case S114:

                return R.color.filter_color_brown_dark;


            default:
                return R.color.filter_color_grey_light;
        }
    }

    public static int FilterType2Thumb(KwFilterType filterType) {
        switch (filterType) {
            case BLUEBERRY:
                return R.drawable.blueberry_icon;
            case DREAMY:
                return R.drawable.dreamy_icon;
            case HABANA:
                return R.drawable.habana_icon;
            case HAPPY:
                return R.drawable.happy_icon;
            case HARVEST:
                return R.drawable.harvest_icon;
            case MISTY:
                return R.drawable.misty_icon;
            case SPRING:
                return R.drawable.spring_icon;

            case COLD:
                return R.drawable.cold_icon;
            case FROG:
                return R.drawable.frog_icon;
            case KD100:
                return R.drawable.kd100_icon;

            case DARK:
                return R.drawable.dark_icon;
            case FSFP100CC:
                return R.drawable.fsfp100cc_icon;
            case LYON:
                return R.drawable.lyon_icon;
            case M106:
                return R.drawable.m106_icon;
            case M109:
                return R.drawable.m109_icon;
            case S114:
                return R.drawable.s114_icon;


            case NONE:
                return R.drawable.filter_thumb_original;
            case WHITECAT:
                return R.drawable.filter_thumb_whitecat;
            case BLACKCAT:
                return R.drawable.filter_thumb_blackcat;
            case ROMANCE:
                return R.drawable.filter_thumb_romance;
            case SAKURA:
                return R.drawable.filter_thumb_sakura;
            case AMARO:
                return R.drawable.filter_thumb_amoro;
            case BRANNAN:
                return R.drawable.filter_thumb_brannan;
            case BROOKLYN:
                return R.drawable.filter_thumb_brooklyn;
            case EARLYBIRD:
                return R.drawable.filter_thumb_earlybird;
            case FREUD:
                return R.drawable.filter_thumb_freud;
            case HEFE:
                return R.drawable.filter_thumb_hefe;
            case HUDSON:
                return R.drawable.filter_thumb_hudson;
            case INKWELL:
                return R.drawable.filter_thumb_inkwell;
            case KEVIN:
                return R.drawable.filter_thumb_kevin;
            case LOMO:
                return R.drawable.filter_thumb_lomo;
            case N1977:
                return R.drawable.filter_thumb_1977;
            case NASHVILLE:
                return R.drawable.filter_thumb_nashville;
            case PIXAR:
                return R.drawable.filter_thumb_piaxr;
            case RISE:
                return R.drawable.filter_thumb_rise;
            case SIERRA:
                return R.drawable.filter_thumb_sierra;
            case SUTRO:
                return R.drawable.filter_thumb_sutro;
            case TOASTER2:
                return R.drawable.filter_thumb_toastero;
            case VALENCIA:
                return R.drawable.filter_thumb_valencia;
            case WALDEN:
                return R.drawable.filter_thumb_walden;
            case XPROII:
                return R.drawable.filter_thumb_xpro;
            case ANTIQUE:
                return R.drawable.filter_thumb_antique;
            case SKINWHITEN:
                return R.drawable.filter_thumb_beauty;
            case CALM:
                return R.drawable.filter_thumb_calm;
            case COOL:
                return R.drawable.filter_thumb_cool;
            case EMERALD:
                return R.drawable.filter_thumb_emerald;
            case EVERGREEN:
                return R.drawable.filter_thumb_evergreen;
            case FAIRYTALE:
                return R.drawable.filter_thumb_fairytale;
            case HEALTHY:
                return R.drawable.filter_thumb_healthy;
            case NOSTALGIA:
                return R.drawable.filter_thumb_nostalgia;
            case TENDER:
                return R.drawable.filter_thumb_tender;
            case SWEETS:
                return R.drawable.filter_thumb_sweets;
            case LATTE:
                return R.drawable.filter_thumb_latte;
            case WARM:
                return R.drawable.filter_thumb_warm;
            case SUNRISE:
                return R.drawable.filter_thumb_sunrise;
            case SUNSET:
                return R.drawable.filter_thumb_sunset;
            case CRAYON:
                return R.drawable.filter_thumb_crayon;
            case SKETCH:
                return R.drawable.filter_thumb_sketch;

            case DISTORTION_NO:
                return R.drawable.filter_none;
            case DISTORTION_ET:
                return R.drawable.filter_thumb_et;
            case DISTORTION_EYE:
                return R.drawable.filter_thumb_eye;
            case DISTORTION_HALF_PINCH:
                return R.drawable.filter_thumb_half_pinch;
            case DISTORTION_PEAR_FACE:
                return R.drawable.filter_thumb_pear_face;
            case DISTORTION_STRETCH:
                return R.drawable.filter_thumb_stretch;
            case DISTORTION_ET_NEW:
                return R.drawable.filter_thumb_stretch;
            case DISTORTION_SLIM_FACE:
                return R.drawable.filter_thumb_slim_face;
            case DISTORTION_FAT_FACE:
                return R.drawable.filter_thumb_fat_face;

            default:
                return R.drawable.filter_thumb_original;
        }
    }

    public static int FilterType2Name(KwFilterType filterType) {
        switch (filterType) {
            case BLUEBERRY:
                return R.string.blueberry;
            case DREAMY:
                return R.string.dreamy;
            case HABANA:
                return R.string.habana;
            case HAPPY:
                return R.string.happy;
            case HARVEST:
                return R.string.harvest;
            case MISTY:
                return R.string.misty;
            case SPRING:
                return R.string.spring;
            case COLD:
                return R.string.cold;
            case FROG:
                return R.string.frog;
            case KD100:
                return R.string.kd100;

            case DARK:
                return R.string.dark;
            case FSFP100CC:
                return R.string.fsfp100cc;
            case LYON:
                return R.string.lyon;
            case M106:
                return R.string.m106;
            case M109:
                return R.string.m109;
            case S114:
                return R.string.s114;


            case NONE:
                return R.string.filter_none;
            case WHITECAT:
                return R.string.filter_whitecat;
            case BLACKCAT:
                return R.string.filter_blackcat;
            case ROMANCE:
                return R.string.filter_romance;
            case SAKURA:
                return R.string.filter_sakura;
            case AMARO:
                return R.string.filter_amaro;
            case BRANNAN:
                return R.string.filter_brannan;
            case BROOKLYN:
                return R.string.filter_brooklyn;
            case EARLYBIRD:
                return R.string.filter_Earlybird;
            case FREUD:
                return R.string.filter_freud;
            case HEFE:
                return R.string.filter_hefe;
            case HUDSON:
                return R.string.filter_hudson;
            case INKWELL:
                return R.string.filter_inkwell;
            case KEVIN:
                return R.string.filter_kevin;
            case LOMO:
                return R.string.filter_lomo;
            case N1977:
                return R.string.filter_n1977;
            case NASHVILLE:
                return R.string.filter_nashville;
            case PIXAR:
                return R.string.filter_pixar;
            case RISE:
                return R.string.filter_rise;
            case SIERRA:
                return R.string.filter_sierra;
            case SUTRO:
                return R.string.filter_sutro;
            case TOASTER2:
                return R.string.filter_toastero;
            case VALENCIA:
                return R.string.filter_valencia;
            case WALDEN:
                return R.string.filter_walden;
            case XPROII:
                return R.string.filter_xproii;
            case ANTIQUE:
                return R.string.filter_antique;
            case CALM:
                return R.string.filter_calm;
            case COOL:
                return R.string.filter_cool;
            case EMERALD:
                return R.string.filter_emerald;
            case EVERGREEN:
                return R.string.filter_evergreen;
            case FAIRYTALE:
                return R.string.filter_fairytale;
            case HEALTHY:
                return R.string.filter_healthy;
            case NOSTALGIA:
                return R.string.filter_nostalgia;
            case TENDER:
                return R.string.filter_tender;
            case SWEETS:
                return R.string.filter_sweets;
            case LATTE:
                return R.string.filter_latte;
            case WARM:
                return R.string.filter_warm;
            case SUNRISE:
                return R.string.filter_sunrise;
            case SUNSET:
                return R.string.filter_sunset;
            case SKINWHITEN:
                return R.string.filter_skinwhiten;
            case CRAYON:
                return R.string.filter_crayon;
            case SKETCH:
                return R.string.filter_sketch;

            case DISTORTION_ET:
                return R.string.filter_et;
            case DISTORTION_EYE:
                return R.string.filter_big_eye;
            case DISTORTION_HALF_PINCH:
                return R.string.filter_half_pinch;
            case DISTORTION_PEAR_FACE:
                return R.string.filter_pear_face;
            case DISTORTION_STRETCH:
                return R.string.filter_stretch;

            default:
                return R.string.filter_none;
        }
    }
}
