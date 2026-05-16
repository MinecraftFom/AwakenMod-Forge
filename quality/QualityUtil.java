package com.fomdev.awaken.quality;

import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class QualityUtil
{
    private static final Map<ResourceLocation, Quality> registeredQualities = new HashMap<>();

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
}