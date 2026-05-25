package com.fomdev.awaken.awaken;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.flib.util.Suggested;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AwakenLevelRegister
{
    // Injected a new registry event for awaken levels (WILL FREEZE AFTER CALLING!!!)
    public static final ResourceKey<Registry<AwakenLevel>> AWAKEN_LEVEL = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AwakenContent.MODID, "awaken_level"));

    private static final Map<ResourceLocation, AwakenLevel> registeredLevels = new HashMap<>();

    private static Map<ResourceLocation, AwakenLevel> frozenMap = null;
    private static final List<AwakenLevel> sortedFrozenCache = new ArrayList<>();

    @Nullable
    public static AwakenLevel getLevel(ResourceLocation location)
    {
        if (frozenMap == null)
            throw new IllegalStateException("Currently, registration HAS NOT finished");

        return frozenMap.get(location);
    }

    @Nullable
    public static AwakenLevel getLevel(String modid, String id)
    {
        return getLevel(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    /* WARNING: YOU MAY FOUND DUPLICATED IDS IN DIFFERENT MOD NAMESPACES. SO, IF YOU WANT ACCURACY, JUST CALL THE getLevel(ResourceLocation) or getLevel(String, String), not this! */
    @Suggested
    @Nullable
    public static AwakenLevel getLevel(String id)
    {
        if (frozenMap == null)
            throw new IllegalStateException("Currently, registration HAS NOT finished");

        AtomicReference<AwakenLevel> level = new AtomicReference<>();
        frozenMap.forEach((key, value) -> {
            if (value.id().equals(id))
                level.set(value);
        });

        return level.get();
    }

    @Nullable
    public static AwakenLevel getLevel(double level)
    {
        if (frozenMap == null)
            throw new IllegalStateException("Current state: registration. You have no access to this method until the registration freeze take place");

        if (sortedFrozenCache.isEmpty())
            return null;

//        int minCoords = 0;
//        int maxCoords = sortedFrozenCache.size() - 1;
//
//        int resultCoords = 0;
//        boolean found = false;
//        while (!found && minCoords <= maxCoords)
//        {
//            int centerCoords = minCoords + (maxCoords - minCoords) / 2;
//            if (centerCoords == 0)
//            {
//                found = true;
//                continue;
//            }
//            if (centerCoords == sortedFrozenCache.size() - 1/* Adding this condition to check if the centerCoords has been out of bound */)
//            {
//                found = true; // Nothing match, breaks loop
//                resultCoords = sortedFrozenCache.size() - 1;
//                continue;
//            }
//
//            int lastCoords = centerCoords - 1;
//
//            AwakenLevel centerLevel = sortedFrozenCache.get(centerCoords);
//            AwakenLevel lastLevel = sortedFrozenCache.get(lastCoords);
//
//            if (centerLevel.min() < level)
//            {
//                minCoords = centerCoords + 1;
//            }
//            else if (lastLevel.min() <= level && level <= centerLevel.min())
//            {
//                found = true;
//                resultCoords = lastCoords;
//            }
//            else if (lastLevel.min() > level)
//            {
//                maxCoords = centerCoords - 1;
//            }
//        }

//        return sortedFrozenCache.get(resultCoords);

        for (int i = 0; i < sortedFrozenCache.size(); i++)
        {
            if (i == sortedFrozenCache.size() - 1)
                return sortedFrozenCache.get(i);

            AwakenLevel curr = sortedFrozenCache.get(i);
            AwakenLevel next = sortedFrozenCache.get(i + 1);
            if (curr.min() <= level && level <= next.min())
                return curr;
        }

        return null;
    }

    public static AwakenLevel register(AwakenLevel level, String modid)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, level.id());

        if (registeredLevels.containsKey(location))
            throw new IllegalArgumentException("Duplicated registry location: " + location);

        if (frozenMap != null)
            throw new IllegalStateException("Freezed registries: unable to register");

        return registeredLevels.put(location, level);
    }

    @SubscribeEvent
    public static void onFmlCommonSetup(FMLCommonSetupEvent event)
    {
        Awaken.LOGGER.info("Freezing register on FMLCommonSetup");
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AWAKEN_LEVEL));

        concludeSortedData();
    }

    private static void concludeSortedData()
    {
        frozeMap();
        if (frozenMap == null)
            // LoL
            throw new IllegalStateException("Currently, registration HAS NOT finished. But wait... are you using reflection? I'm NOT supposing to see this happen. Or are you using mixin? Just please, STOP! I DON'T WANT TO BE BOTHERED!!!");

        for (AwakenLevel level: frozenMap.values())
        {
            if (sortedFrozenCache.isEmpty())
            {
                sortedFrozenCache.add(level);
                continue;
            }

            double value = level.min();
            int min = 0;
            int max = sortedFrozenCache.size() - 1;
            int cord = 0;

            boolean found = false;
            while (!found)
            {
                int centerCoordinates = min + (max - min) / 2; // Center coordinates

                if (centerCoordinates == 0 && sortedFrozenCache.get(0).min() > value)
                {
                    // cord is defaulted to 0
                    found = true;
                    continue;
                }

                if (centerCoordinates == sortedFrozenCache.size() - 1 && sortedFrozenCache.get(sortedFrozenCache.size() - 1).min() < value)
                {
                    cord = sortedFrozenCache.size() - 1;
                    found = true;
                    continue;
                }

                AwakenLevel centerLevel = sortedFrozenCache.get(centerCoordinates);
                AwakenLevel lastLevel = sortedFrozenCache.get(centerCoordinates - 1);

                if (lastLevel.min() <= value && value <= centerLevel.min())
                {
                    // Perfect situation!
                    cord = centerCoordinates;
                    found = true;
                }
                else if (value < centerLevel.min() && value < lastLevel.min())
                {
                    // The coordinates are TOO big!
                    max = centerCoordinates - 1; // Makes sure size isn't 0
                }
                else if (centerLevel.min() < value && lastLevel.min() < value)
                {
                    // The coordinates are TOO small!
                    min = centerCoordinates + 1; // Makes sure size isn't 0
                }
            }

            sortedFrozenCache.add(cord, level);
        }
    }

    private static void frozeMap()
    {
        frozenMap = new HashMap<>(registeredLevels);
    }

    @ApiStatus.Internal
    public static void printCaches()
    {
        Awaken.LOGGER.info("SORTED: " + Arrays.toString(sortedFrozenCache.stream().map(v -> v.id() + v.min()).toArray()));
    }
}