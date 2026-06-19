package com.fomdev.awaken.spore;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.interpreter.ForceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenContent.MODID)
public class SporeManager
{
    static final Map<ResourceLocation, Pollinate> registeredPollinates = new HashMap<>();
    static final Map<ResourceLocation, Spore> registeredSpores = new HashMap<>();
    private static boolean pollinateFrozen = false;
    private static boolean sporeFrozen = false;

    public static Pollinate getPollinate(
            ResourceLocation location
    )
    {
        return registeredPollinates.get(location);
    }

    public static Spore getSpore(
            ResourceLocation location
    )
    {
        return registeredSpores.get(location);
    }

    @Nullable
    public static ResourceLocation getPollinateId(Pollinate pollinate)
    {
        for (Map.Entry<ResourceLocation, Pollinate> pollinates: registeredPollinates.entrySet())
        {
            if (pollinates.getValue().equals(pollinate))
                return pollinates.getKey();
        }

        return null;
    }

    @Nullable
    public static ResourceLocation getSporeId(Spore spore)
    {
        for (Map.Entry<ResourceLocation, Spore> spores: registeredSpores.entrySet())
        {
            if (spores.getValue().equals(spore))
                return spores.getKey();
        }

        return null;
    }

    public static Pollinate register(String modid, Pollinate pollinate)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, pollinate.id());
        if (pollinateFrozen)
            throw new IllegalStateException("Pollinate registry state frozen");

        if (registeredPollinates.containsKey(location))
            throw new IllegalStateException("Registered pollinate " + location);

        return registeredPollinates.put(location, pollinate);
    }

    public static Spore register(String modid, Spore spore)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, spore.id());
        if (sporeFrozen)
            throw new IllegalStateException("Spore registry state frozen");

        if (registeredSpores.containsKey(location))
            throw new IllegalStateException("Registered spore " + location);

        return registeredSpores.put(location, spore);
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event)
    {
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_POLLINATE);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_POLLINATE));
        pollinateFrozen = true;

        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_SPORE);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_SPORE));
        sporeFrozen = true;
    }

    public static List<Pollinate> getPollinates(
            EquipmentSlot slot
    )
    {
        return registeredPollinates.values().stream().filter(v -> Arrays.asList(v.suitableOn()).contains(slot)).toList();
    }

    public static List<Spore> getSpores(
            EquipmentSlot slot
    )
    {
        return registeredSpores.values().stream().filter(v -> Arrays.asList(v.suitableOn()).contains(slot)).toList();
    }
}