package com.fomdev.awaken.quality;

import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public class QualityUtil {
    private static final Map<ResourceLocation, Quality> registeredQualities = new HashMap<>();

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

    @Nullable
    public static ResourceLocation getQualityId(
            Quality quality
    )
    {
        for (Map.Entry<ResourceLocation, Quality> entry : registeredQualities.entrySet())
        {
            if (entry.getValue().equals(quality))
            {
                return entry.getKey();
            }
        }
        return null;
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
        return "quality." + id + ".name";
    }

    public static Quality registerQuality(
            String modid,
            Quality quality
    )
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, quality.id());
        if (registeredQualities.containsKey(location))
            throw new IllegalArgumentException("Invalid register id: " + location + ", already registered");

        registeredQualities.put(location, quality);
        return quality;
    }

    static
    {
        naive = registerQuality(
                AwakenRPG.MODID,
                Quality.of(
                        "naive",
                        5,
                        0.01F,
                        110F,
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
                        120F,
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
                        0.21F,
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
                        17,
                        Quality.ColorPattern.CONTINUE,
                        new Color(0xFF, 0x00, 0x00),
                        new Color(0x00, 0x00, 0xFF)
                )
        );
    }

    public static Quality shuffleQuality(
            Random random,
            float diffFactor
    )
    {
        float chance = diffFactor * 100; // Get the percentage chance of the diffFactor

        List<Quality> qualities = new ArrayList<>();
        for (Quality quality: registeredQualities.values())
        {
            if (quality.level() <= chance)
                qualities.add(quality);
        }

        if (qualities.isEmpty())
            return null;

        return qualities.get(random.nextInt(qualities.size()));
    }
}