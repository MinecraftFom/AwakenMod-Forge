package com.fomdev.awaken.quality;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.interpreter.ForceLoader;
import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

@Mod.EventBusSubscriber(modid = AwakenContent.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class QualityUtil {
    private static final Map<ResourceLocation, Quality> registeredQualities = new HashMap<>();

    private static boolean frozen = false;

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
        if (frozen)
            throw new IllegalStateException("Quality register frozen");

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, quality.id());
        if (registeredQualities.containsKey(location))
            throw new IllegalArgumentException("Invalid register id: " + location + ", already registered");

        registeredQualities.put(location, quality);
        return quality;
    }

    public static Quality shuffleQuality(
            Random random,
            float diffFactor
    )
    {
        List<Quality> qualities = new ArrayList<>();
        for (Quality quality: registeredQualities.values())
        {
            if (quality.level() <= diffFactor)
                qualities.add(quality);
        }

        if (qualities.isEmpty())
            return null;

        return qualities.get(random.nextInt(qualities.size()));
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event)
    {
        Awaken.LOGGER.info("Freezing registry on FML-Common-Setup");
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_QUALITY);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_QUALITY));
        frozen = true;
        Awaken.LOGGER.info("QU> Quality register state frozen");
    }
}