package com.fomdev.awaken.quality;

import com.fomdev.awaken.gen.GeneratingMobTypes;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class QualityUtil
{
    private static final Map<ResourceLocation, Quality> registeredQualities = new HashMap<>();

    public static final Quality naive;
    public static final Quality novice;
//    public static final Quality prehistoric;
//    public static final Quality prelithic;
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

    @Nullable
    public static ResourceLocation getQualityId(
            Quality quality
    )
    {
        AtomicReference<ResourceLocation> location = new AtomicReference<>();
        registeredQualities.forEach((loc, qa) -> {
            if (qa == quality) location.set(loc);
        });

        return location.get();
    }

    @Nullable
    public static Quality getQuality(
            ResourceLocation location
    )
    {
        return registeredQualities.get(location);
    }

    @Nullable
    @Suggested
    public static Quality getQuality(
            String modid,
            String id
    )
    {
        return getQuality(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static String localize(
            String id
    )
    {
        return "quality."+id+".name";
    }

    public static Quality registerQuality(
            String modid,
            Quality quality
    )
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, quality.id());
        if (registeredQualities.containsKey(location))
            throw new IllegalArgumentException("Invalid register id: " + location + ", already registered");

        return registeredQualities.put(location, quality);
    }

    static
    {
        naive = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "naive",
                        new Color(0xAA, 0xAA, 0xAA),
                        false,
                        0.7F,
                        0.1F,
                        5,
                        0.01F,
                        1,
                        new GeneratingMobTypes[]{GeneratingMobTypes.Types.MONSTER},
                        3000,
                        6.0F
                )
        );

        novice = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "novice",
                        new Color(0xAB, 0xAB, 0xAB),
                        false,
                        0.67F,
                        0.1F,
                        6,
                        0.012F,
                        2,
                        new GeneratingMobTypes[]{GeneratingMobTypes.Types.MONSTER}
                )
        );

        infinity = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "infinity",
                        new Color(0xFF, 0xFE, 0xFE, 0xA),
                        true,
                        0.00001F,
                        0.5F,
                        255,
                        20F,
                        17,
                        new GeneratingMobTypes[]{GeneratingMobTypes.Types.BOSS}
                )
        );
    }
}