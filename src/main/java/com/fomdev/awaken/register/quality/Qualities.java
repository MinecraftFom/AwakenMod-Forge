package com.fomdev.awaken.register.quality;

import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.load.ForceLoad;

import java.awt.*;

import static com.fomdev.awaken.quality.QualityUtil.registerQuality;

@ForceLoad(AwakenRegistries.SIG_AWAKEN_QUALITY)
public class Qualities
{
    public static final Quality naive;
    public static final Quality novice;
    public static final Quality prehistoric;
    //    public static final Quality pathetic;
//    public static final Quality basic;
//    public static final Quality started;
//    public static final Quality learner;
//    public static final Quality smiths;
//    public static final Quality ancient;
//    public static final Quality pizzazz;
//    public static final Quality symmetrical;
//    public static final Quality mysterious;
//    public static final Quality virtual;
//    public static final Quality tremendous;
//    public static final Quality masterpiece;
//    public static final Quality legendary;
    public static final Quality infinity;

    static
    {
        naive = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "naive",
                        5,
                        0.01F,
                        0.01F,
                        Quality.ColorPattern.SINGLE,
                        new Color(0xAA, 0xAA, 0xAA)
                )
        );

        novice = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "novice",
                        6,
                        0.012F,
                        0.05F,
                        Quality.ColorPattern.SINGLE,
                        new Color(0xAB, 0xAB, 0xAB)
                )
        );

        prehistoric = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "prehistoric",
                        8,
                        0.01F,
                        0.2F,
                        Quality.ColorPattern.MULTIPLE,
                        new Color(0xFF, 0x00, 0x00),
                        new Color(0xFF, 0xFF, 0x00),
                        new Color(0xFF, 0xFF, 0xFF),
                        new Color(0x00, 0xFF, 0xFF),
                        new Color(0x00, 0x00, 0xFF),
                        new Color(0x00, 0x00, 0x00)
                )
        );

        infinity = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "infinity",
                        255,
                        20F,
                        1700,
                        Quality.ColorPattern.CONTINUE,
                        new Color(0xFF, 0x00, 0x00),
                        new Color(0x00, 0x00, 0xFF)
                )
        );
    }

    @ForceLoad
    public static void placeholder() {}
}